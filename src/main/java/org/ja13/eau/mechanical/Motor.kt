package org.ja13.eau.mechanical

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import org.ja13.eau.EAU
import org.ja13.eau.cable.CableRenderDescriptor
import org.ja13.eau.misc.Coordonate
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.GREEN
import org.ja13.eau.misc.HSLColor
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.LinearFunction
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.RED
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.Utils.getLength
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.misc.YELLOW
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.transparent.EntityMetaTag
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeBlock
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeEntity
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.ThermalLoadInitializer
import org.ja13.eau.sim.mna.component.Resistor
import org.ja13.eau.sim.mna.component.VoltageSource
import org.ja13.eau.sim.mna.misc.IRootSystemPreStepProcess
import org.ja13.eau.sim.mna.misc.MnaConst
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import org.ja13.eau.sim.nbt.NbtThermalLoad
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog
import org.ja13.eau.sim.process.destruct.WorldExplosion
import org.ja13.eau.sim.process.heater.ElectricalLoadHeatThermalLoad
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor
import org.ja13.eau.sound.LoopedSound
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.MathHelper
import net.minecraftforge.client.event.RenderGameOverlayEvent
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.io.DataInputStream
import java.io.DataOutputStream

class MotorDescriptor(
    name: String,
    obj: org.ja13.eau.misc.Obj3D,
    cable: org.ja13.eau.sixnode.genericcable.GenericCableDescriptor,
    nominalRads: Float,
    nominalU: Float,
    nominalP: Float,
    elecPPerDU: Float,
    thermalLoadInitializer: org.ja13.eau.sim.ThermalLoadInitializer
) : SimpleShaftDescriptor(
    name,
    MotorElement::class,
    MotorRender::class,
    org.ja13.eau.node.transparent.EntityMetaTag.Basic
) {
    val cable = cable
    val nominalRads = nominalRads
    val nominalU = nominalU
    val nominalP = nominalP
    val maxP = 32000f  // TODO (Grissess): Calculate?
    val elecPPerDU = elecPPerDU
    val thermalLoadInitializer = thermalLoadInitializer

    val radsToU = LinearFunction(0f, 0f, nominalRads, nominalU)

    val customSound = "eln:shaft_motor"
    val efficiency = 0.99

    override val obj = obj
    override val static = arrayOf(
        obj.getPart("Cowl"),
        obj.getPart("Stand")
    ).requireNoNulls()
    override val rotating = arrayOf(obj.getPart("Shaft")).requireNoNulls()
    val leds = arrayOf(
        obj.getPart("LED_0"),
        obj.getPart("LED_1"),
        obj.getPart("LED_2"),
        obj.getPart("LED_3"),
        obj.getPart("LED_4"),
        obj.getPart("LED_5"),
        obj.getPart("LED_6")
    ).requireNoNulls()

    init {
        thermalLoadInitializer.setMaximalPower(nominalP.toDouble())
        voltageTier = VoltageTier.INDUSTRIAL
        this.name = name
    }

    override fun addInfo(stack: ItemStack, player: EntityPlayer, list: MutableList<String>) {
        list.add("Converts electricity into mechanical energy, or (badly) vice versa.")
        list.add("Nominal usage ->")
        list.add(Utils.plotVolt(nominalU.toDouble(), "Nominal Voltage:"))
        list.add(Utils.plotPower(nominalP.toDouble(), "Nominal Power:"))
        list.add(Utils.plotRads(nominalRads.toDouble(), "Nominal Speed:"))
        list.add(Utils.plotRads(absoluteMaximumShaftSpeed, "Absolute Maximum Speed:"))
    }
}

class MotorRender(entity: org.ja13.eau.node.transparent.TransparentNodeEntity, desc_: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : ShaftRender(entity, desc_) {
    val entity = entity

    override val cableRender = org.ja13.eau.EAU.mediumInsulationMediumCurrentRender
    val desc = desc_ as MotorDescriptor

    val ledColors: Array<Color> = arrayOf(
        Color.black,
        Color.black,
        Color.black,
        Color.black,
        Color.black,
        Color.black,
        Color.black
    )
    val ledColorBase: Array<HSLColor> = arrayOf(
        GREEN,
        GREEN,
        GREEN,
        GREEN,
        YELLOW,
        RED,
        RED
    )

    inner class MotorLoopedSound(sound: String, coord: Coordonate) :
        LoopedSound(sound, coord) {
        override fun getPitch() = Math.max(0.05, rads / desc.nominalRads).toFloat()
        override fun getVolume() = volumeSetting.position.toFloat()
    }

    init {
        addLoopedSound(MotorLoopedSound(desc.customSound, coordonate()))
        mask.set(LRDU.Down, true)
    }

    fun setPower(power: Double) {
        if(power < 0) {
            for(i in 1..6) ledColors[i] = Color.black
            ledColors[0] = RED.adjustLuminanceClamped((-power / desc.nominalP * 400).toFloat(), 0f, 60f)
        } else {
            val slice = desc.maxP / 5
            var current = power
            for(i in 0..6) {
                ledColors[i] = ledColorBase[i].adjustLuminanceClamped((current / slice * 100).toFloat(), 0f, 65f)
                current -= slice
            }
        }
    }

    override fun draw() {
        draw {
            ledColors.forEachIndexed { i, color ->
                GL11.glColor3f(
                    color.red / 255f,
                    color.green / 255f,
                    color.blue / 255f
                )
                desc.leds[i].draw()
            }
        }
    }

    override fun getCableRender(side: Direction, lrdu: LRDU): org.ja13.eau.cable.CableRenderDescriptor? {
        if(lrdu == LRDU.Down && side == front) return org.ja13.eau.EAU.mediumInsulationMediumCurrentRender
        return null
    }

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        val power = stream.readDouble()

        setPower(power)
        volumeSetting.target = Math.min(1.0, Math.abs(power / desc.maxP)) / 4.0
    }
}

class MotorElement(node: org.ja13.eau.node.transparent.TransparentNode, desc_: org.ja13.eau.node.transparent.TransparentNodeDescriptor) :
    SimpleShaftElement(node, desc_) {
    val desc = desc_ as MotorDescriptor

    internal val wireLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("wireLoad")
    internal val shaftLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("shaftLoad")
    internal val wireShaftResistor = org.ja13.eau.sim.mna.component.Resistor(wireLoad, shaftLoad)
    internal val powerSource = org.ja13.eau.sim.mna.component.VoltageSource("powerSource", shaftLoad, null)

    internal val electricalProcess = MotorElectricalProcess()
    internal val shaftProcess = MotorShaftProcess()

    internal val thermal = org.ja13.eau.sim.nbt.NbtThermalLoad("thermal")
    internal val heater: org.ja13.eau.sim.process.heater.ElectricalLoadHeatThermalLoad
    internal val thermalWatchdog = org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog()

    init {
        electricalLoadList.addAll(arrayOf(wireLoad, shaftLoad))
        electricalComponentList.addAll(arrayOf(wireShaftResistor, powerSource))

        electricalProcessList.add(shaftProcess)

        desc.cable.applyTo(wireLoad)
        desc.cable.applyTo(shaftLoad)
        desc.cable.applyTo(wireShaftResistor)

        desc.thermalLoadInitializer.applyTo(thermal)
        desc.thermalLoadInitializer.applyTo(thermalWatchdog)
        thermal.setAsSlow()
        thermalLoadList.add(thermal)
        thermalWatchdog.set(thermal).set(org.ja13.eau.sim.process.destruct.WorldExplosion(this).machineExplosion())
        slowProcessList.add(thermalWatchdog)

        heater = org.ja13.eau.sim.process.heater.ElectricalLoadHeatThermalLoad(wireLoad, thermal)
        thermalFastProcessList.add(heater)
    }

    inner class MotorElectricalProcess : org.ja13.eau.sim.IProcess, org.ja13.eau.sim.mna.misc.IRootSystemPreStepProcess {
        override fun process(time: Double) {
            val noTorqueU = desc.radsToU.getValue(shaft.rads)

            // Most of this was copied from Generator.kt, and bears the same
            // admonition: I don't actually know how this works.
            val th = wireLoad.subSystem.getTh(wireLoad, powerSource)
            if (th.U.isNaN()) {
                th.U = noTorqueU
                th.R = org.ja13.eau.sim.mna.misc.MnaConst.highImpedance
            }
            var U: Double
            if(noTorqueU < th.U) {
                // Input is greater than our output, spin up the shaft
                U = th.U * 0.997 + noTorqueU * 0.003
            } else if(th.isHighImpedance) {
                // No actual connection, let the system float
                U = noTorqueU
            } else {
                // Provide an output voltage by
                // solving a quadratic, I guess?
                val a = 1 / th.R
                val b = desc.elecPPerDU - th.U / th.R
                val c = -desc.elecPPerDU * noTorqueU
                U = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a)
            }
            powerSource.u = U
        }

        override fun rootSystemPreStepProcess() {
            process(0.0)
        }
    }

    inner class MotorShaftProcess : org.ja13.eau.sim.IProcess {
        override fun process(time: Double) {
            val p = powerSource.p
            var E = -p * time
            if (E.isNaN())
                E = 0.0
            if(E < 0) {
                // Pushing power--this is very inefficient
                E = 0.0
                //E = E * 10.0
            }
            maybePublishP(E / time)
            E = E - defaultDrag * Math.max(shaft.rads, 10.0)
            shaft.energy += E * desc.efficiency
            thermal.movePowerTo(E * (1 - desc.efficiency))
        }
    }

    var lastP = 0.0
    fun maybePublishP(P: Double) {
        if(Math.abs(P - lastP) / desc.nominalP > 0.01) {
            lastP = P
            needPublish()
        }
    }

    override fun connectJob() {
        super.connectJob()
        org.ja13.eau.EAU.simulator.mna.addProcess(electricalProcess)
    }

    override fun disconnectJob() {
        super.disconnectJob()
        org.ja13.eau.EAU.simulator.mna.removeProcess(electricalProcess)
    }

    override fun getElectricalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ElectricalLoad? {
        if(lrdu != LRDU.Down) return null
        return when(side) {
            front -> wireLoad
            front.back() -> wireLoad
            else -> null
        }
    }

    override fun getThermalLoad(side: Direction, lrdu: LRDU) = thermal

    override fun getConnectionMask(side: Direction?, lrdu: LRDU?): Int {
        if(lrdu == LRDU.Down && (side == front || side == front.back())) return org.ja13.eau.node.NodeBase.MASK_ELECTRIC
        return 0
    }

    override fun multiMeterString(side: Direction?) =
        Utils.plotER(shaft.energy, shaft.rads) +
            Utils.plotUIP(powerSource.u, powerSource.i)

    override fun thermoMeterString(side: Direction?) = Utils.plotCelsius(thermal.t)

    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float, vz: Float) =
        false

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        stream.writeDouble(lastP)
    }

    override fun getWaila(): MutableMap<String, String> {
        val info = mutableMapOf<String, String>()
        info["Energy"] = Utils.plotEnergy(shaft.energy)
        info["Speed"] = Utils.plotRads(shaft.rads)
        if(org.ja13.eau.EAU.wailaEasyMode) {
            info["Voltage"] = Utils.plotVolt(powerSource.u)
            info["Current"] = Utils.plotAmpere(powerSource.i)
            info["Temperature"] = Utils.plotCelsius(thermal.t)
        }
        return info
    }
}

class MotorOverlay {
    @SubscribeEvent
    fun render(event: RenderGameOverlayEvent.Text) {
        val mc = Minecraft.getMinecraft()
        val player = mc.thePlayer
        val px = MathHelper.floor_double(player.posX)
        val py = MathHelper.floor_double(player.posY)
        val pz = MathHelper.floor_double(player.posZ)
        val r = 1
        val w = player.worldObj
        var best: MotorRender? = null
        var bestDistance = 10000.0
        for (x in px - r..px + r) {
            for (y in py - r..py + r) {
                for (z in pz - r..pz + r) {
                    if (w.getBlock(x, y, z) is org.ja13.eau.node.transparent.TransparentNodeBlock) {
                        val e = w.getTileEntity(x, y, z)
                        if (e is org.ja13.eau.node.transparent.TransparentNodeEntity) {
                            val render = e.elementRender
                            if (render is MotorRender) {
                                val d = getLength(player.posX, player.posY, player.posZ, x + 0.5, y + 0.5, z + 0.5)
                                if (d < bestDistance) {
                                    bestDistance = d
                                    best = render
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
        if (best != null) {
            GL11.glPushMatrix()
            GL11.glScalef(0.5f, 0.5f, 0.5f)
            val y = event.resolution.scaledHeight
            Minecraft.getMinecraft().fontRenderer.drawString("Shaft speed: ${best.rads}rad/s", 5 + event.resolution.scaledWidth, 3 + y, 0xFFFFFF)
            GL11.glPopMatrix()
        }
    }
}
