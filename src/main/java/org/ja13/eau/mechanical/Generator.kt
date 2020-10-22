package org.ja13.eau.mechanical

import org.ja13.eau.EAU
import org.ja13.eau.cable.CableRenderDescriptor
import org.ja13.eau.misc.*
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.transparent.EntityMetaTag
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeEntity
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.ThermalLoadInitializer
import org.ja13.eau.sim.mna.component.Resistor
import org.ja13.eau.sim.mna.component.VoltageSource
import org.ja13.eau.sim.mna.misc.IRootSystemPreStepProcess
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import org.ja13.eau.sim.nbt.NbtThermalLoad
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog
import org.ja13.eau.sim.process.destruct.WorldExplosion
import org.ja13.eau.sim.process.heater.ElectricalLoadHeatThermalLoad
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.io.DataInputStream
import java.io.DataOutputStream


class GeneratorDescriptor(
    name: String,
    obj: org.ja13.eau.misc.Obj3D,
    nominalRads: Float,
    nominalU: Float,
    powerOutPerDeltaU: Float,
    nominalP: Float,
    thermalLoadInitializer: org.ja13.eau.sim.ThermalLoadInitializer) :
    SimpleShaftDescriptor(name, GeneratorElement::class, GeneratorRender::class, org.ja13.eau.node.transparent.EntityMetaTag.Basic) {

    val RtoU = LinearFunction(0f, 0f, nominalRads, nominalU)
    val thermalLoadInitializer = thermalLoadInitializer
    val powerOutPerDeltaU = powerOutPerDeltaU
    val nominalRads = nominalRads
    val nominalP = nominalP
    val nominalU = nominalU
    val generationEfficiency = 0.95
    override val sound = "eln:generator"

    init {
        thermalLoadInitializer.setMaximalPower(nominalP.toDouble() * (1 - generationEfficiency))

        voltageTier = VoltageTier.INDUSTRIAL
    }

    override val obj = obj
    override val static = arrayOf(
        obj.getPart("Cowl"),
        obj.getPart("Stand")
    ).requireNoNulls()
    override val rotating = arrayOf(obj.getPart("Shaft")).requireNoNulls()
    val powerLights = arrayOf(
        obj.getPart("LED_0"),
        obj.getPart("LED_1"),
        obj.getPart("LED_2"),
        obj.getPart("LED_3"),
        obj.getPart("LED_4"),
        obj.getPart("LED_5"),
        obj.getPart("LED_6")
    ).requireNoNulls()

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        list.add("Converts mechanical energy into ")
        list.add("electricity, or (badly) vice versa.")
        list.add("Nominal usage ->")
        list.add(Utils.plotVolt(nominalU.toDouble(), "Nominal Voltage:"))
        list.add(Utils.plotPower(nominalP.toDouble(), "Nominal Power:"))
        list.add(Utils.plotRads(nominalRads.toDouble(), "Nominal Speed:"))
        list.add(Utils.plotRads(absoluteMaximumShaftSpeed, "Absolute Maximum Speed:"))
    }
}

class GeneratorRender(entity: org.ja13.eau.node.transparent.TransparentNodeEntity, desc_: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : ShaftRender(entity, desc_) {
    val entity = entity

    override val cableRender = org.ja13.eau.EAU.mediumInsulationMediumCurrentRender
    val desc = desc_ as GeneratorDescriptor

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

    fun calcPower(power: Double) {
        if (power < 0) {
            for (i in 1..6) {
                ledColors[i] = Color.black
            }
            ledColors[0] = RED.adjustLuminanceClamped((-power / desc.nominalP * 4 * 100).toFloat(), 0f, 60f)
        } else {
            val slice = desc.nominalP / 5
            var remainder = power
            for (i in 0..6) {
                ledColors[i] = ledColorBase[i].adjustLuminanceClamped((remainder / slice * 100).toFloat(), 0f, 65f)
                remainder -= slice
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
                desc.powerLights[i].draw()
            }
        }
    }

    override fun getCableRender(side: Direction, lrdu: LRDU): org.ja13.eau.cable.CableRenderDescriptor? {
        if (lrdu == LRDU.Down && side == front) return org.ja13.eau.EAU.mediumInsulationMediumCurrentRender
        return null
    }

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        val power = stream.readDouble()
        calcPower(power)
        volumeSetting.target = 0.05 + Math.abs(power / desc.nominalP).toFloat() / 4.0
    }
}

class GeneratorElement(node: org.ja13.eau.node.transparent.TransparentNode, desc_: org.ja13.eau.node.transparent.TransparentNodeDescriptor) :
    SimpleShaftElement(node, desc_) {
    val desc = desc_ as GeneratorDescriptor

    internal val inputLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("inputLoad")
    internal val positiveLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("positiveLoad")
    internal val inputToPositiveResistor = org.ja13.eau.sim.mna.component.Resistor(inputLoad, positiveLoad)
    internal val electricalPowerSource = org.ja13.eau.sim.mna.component.VoltageSource("PowerSource", positiveLoad, null)
    internal val electricalProcess = GeneratorElectricalProcess()
    internal val shaftProcess = GeneratorShaftProcess()

    internal val thermal = org.ja13.eau.sim.nbt.NbtThermalLoad("thermal")
    internal val heater: org.ja13.eau.sim.process.heater.ElectricalLoadHeatThermalLoad
    internal val thermalLoadWatchDog = org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog()

    init {
        electricalLoadList.add(positiveLoad)
        electricalLoadList.add(inputLoad)
        electricalComponentList.add(electricalPowerSource)
        electricalComponentList.add(inputToPositiveResistor)

        electricalProcessList.add(shaftProcess)
        org.ja13.eau.EAU.uninsulatedHighCurrentCopperCable.applyTo(inputLoad)
        org.ja13.eau.EAU.uninsulatedHighCurrentCopperCable.applyTo(inputToPositiveResistor)
        org.ja13.eau.EAU.uninsulatedHighCurrentCopperCable.applyTo(positiveLoad)

        desc.thermalLoadInitializer.applyTo(thermal)
        desc.thermalLoadInitializer.applyTo(thermalLoadWatchDog)
        thermal.setAsSlow()
        thermalLoadList.add(thermal)
        thermalLoadWatchDog.set(thermal).set(org.ja13.eau.sim.process.destruct.WorldExplosion(this).machineExplosion())
        slowProcessList.add(thermalLoadWatchDog)

        heater = org.ja13.eau.sim.process.heater.ElectricalLoadHeatThermalLoad(inputLoad, thermal)
        thermalFastProcessList.add(heater)

        // TODO: Add running lights. (More. Electrical sparks, perhaps?)
        // TODO: Add the thermal explosions—there should be some.
    }

    inner class GeneratorElectricalProcess : org.ja13.eau.sim.IProcess, org.ja13.eau.sim.mna.misc.IRootSystemPreStepProcess {
        override fun process(time: Double) {
            val targetU = desc.RtoU.getValue(shaft.rads)

            // Most things below were copied from TurbineElectricalProcess.
            // Some comments on what math is going on would be great.
            val th = positiveLoad.subSystem.getTh(positiveLoad, electricalPowerSource)
            val Ut = when {
                targetU < th.U -> th.U * 0.999 + targetU * 0.001
                th.isHighImpedance -> targetU
                else -> {
                    val a = 1 / th.R
                    val b = desc.powerOutPerDeltaU - th.U / th.R
                    val c = -desc.powerOutPerDeltaU * targetU
                    (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a)
                }
            }
            electricalPowerSource.u = Ut
        }

        override fun rootSystemPreStepProcess() {
            process(0.0)
        }
    }

    inner class GeneratorShaftProcess: org.ja13.eau.sim.IProcess {
        private var powerFraction = 0.0f

        override fun process(time: Double) {
            val p = electricalPowerSource.p
            powerFraction = (p / desc.nominalP).toFloat()
            var E = p * time
            if (E.isNaN())
                E = 0.0
            if (E < 0)
                E = 0.0
                //E *= 0.75  // Not a very efficient motor.
            maybePublishE(E / time)
            // The Math.max makes the shaft harder to spin up without an auxilliary power source.
            E += defaultDrag * Math.max(shaft.rads, 10.0)
            shaft.energy -= (E * desc.generationEfficiency)
            thermal.movePowerTo(E * (1 - desc.generationEfficiency))
        }
    }

    var lastE = 0.0
    fun maybePublishE(E: Double) {
        if (Math.abs(E - lastE) / desc.nominalP > 0.01) {
            lastE = E
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
        if (lrdu != LRDU.Down) return null
        return when (side) {
            front -> inputLoad
            front.back() -> inputLoad
            else -> null
        }
    }

    override fun getThermalLoad(side: Direction?, lrdu: LRDU?) = thermal

    override fun getConnectionMask(side: Direction?, lrdu: LRDU?): Int {
        if (lrdu == LRDU.Down && (side == front || side == front.back())) return org.ja13.eau.node.NodeBase.MASK_ELECTRIC
        return 0
    }

    override fun multiMeterString(side: Direction?) =
        Utils.plotER(shaft.energy, shaft.rads) + Utils.plotUIP(electricalPowerSource.u, electricalPowerSource.i)

    override fun thermoMeterString(side: Direction?) = Utils.plotCelsius(thermal.t)

    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float, vz: Float): Boolean {
        return false
    }

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        stream.writeDouble(lastE)
    }

    override fun getWaila(): Map<String, String> {
        val info = mutableMapOf<String, String>()
        info["Energy"] = Utils.plotEnergy( shaft.energy)
        info["Speed"] = Utils.plotRads(shaft.rads)
        if (org.ja13.eau.EAU.wailaEasyMode) {
            info["Voltage"] = Utils.plotVolt(electricalPowerSource.u)
            info["Current"] = Utils.plotAmpere(electricalPowerSource.i)
            info["Temperature"] = Utils.plotCelsius(thermal.t)
        }
        return info
    }
}
