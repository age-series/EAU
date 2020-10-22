package org.ja13.eau.transparentnode.sterlingengine

import org.ja13.eau.EAU
import org.ja13.eau.cable.CableRender
import org.ja13.eau.cable.CableRenderDescriptor
import org.ja13.eau.cable.CableRenderType
import org.ja13.eau.i18n.I18N
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.FunctionTable
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.LRDUMask
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.SlewLimiter
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.Utils.setGlColorFromDye
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeElement
import org.ja13.eau.node.transparent.TransparentNodeElementRender
import org.ja13.eau.node.transparent.TransparentNodeEntity
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.PhysicalConstant
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.mna.component.Resistor
import org.ja13.eau.sim.mna.component.VoltageSource
import org.ja13.eau.sim.mna.misc.IRootSystemPreStepProcess
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import org.ja13.eau.sim.nbt.NbtThermalLoad
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog
import org.ja13.eau.sim.process.destruct.WorldExplosion
import org.ja13.eau.sound.LoopedSound
import net.minecraft.client.audio.ISound
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import org.lwjgl.opengl.GL11
import java.io.DataInputStream
import java.io.DataOutputStream

class SterlingEngineDescriptor(
        name: String,
        obj3D: org.ja13.eau.misc.Obj3D,
        val cable: org.ja13.eau.cable.CableRenderDescriptor,
        val dTtoU: FunctionTable
): org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, SterlingEngineElement::class.java, SterlingEngineRender::class.java) {

    private val main: org.ja13.eau.misc.Obj3D.Obj3DPart = obj3D.getPart("main")

    init {
        voltageTier = VoltageTier.HIGH_HOUSEHOLD
    }

    fun applyTo(load: org.ja13.eau.sim.ThermalLoad) {
        load.C = 25.0
        // 350 is deltaT
        // 3000.0 is power
        // 0.75 (and inverse) is efficiency
        load.Rs = 0.00625
        load.Rp = 10.0

        // DeltaTForInput = 250/40
        // nominalP = 1000
        // 1 - (25 / 370)
        // 1 - 0.06
        // 0.94
        // nominalEff = Math.abs(1 - (0 + PhysicalConstant.Tref) / (nominalDeltaT + PhysicalConstant.Tref));

        // 250 / 40 / (1000 / 0.94)
        // 0.00625
        //this.thermalRs = DeltaTForInput / (nominalP / nominalEff);

        //nominalPowerLost = nominalP / 40
        // 250 / 25
        // 10
        //this.thermalRp = nominalDeltaT / nominalPowerLost;
    }

    fun applyTo(load: org.ja13.eau.sim.ElectricalLoad) {
        load.rs = org.ja13.eau.EAU.getSmallRs()
    }

    fun draw() {
        main.draw()
    }

    override fun handleRenderType(item: ItemStack, type: ItemRenderType): Boolean {
        return true
    }

    override fun shouldUseRenderHelper(type: ItemRenderType?, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper?): Boolean {
        return type != ItemRenderType.INVENTORY
    }

    override fun renderItem(type: ItemRenderType, item: ItemStack?, vararg data: Any?) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, *data)
        } else {
            draw()
        }
    }

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)
        list.add(org.ja13.eau.i18n.I18N.tr("Generates electricity using heat."))
    }
}

class SterlingEngineElement(node: org.ja13.eau.node.transparent.TransparentNode, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElement(node, descriptor) {
    val descriptor = descriptor as SterlingEngineDescriptor

    val inputLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("inputLoad")
    val positiveLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("positiveLoad")
    val inputToTurbineResistor = org.ja13.eau.sim.mna.component.Resistor(inputLoad, positiveLoad)
    val warmLoad = org.ja13.eau.sim.nbt.NbtThermalLoad("warmLoad")
    val coolLoad = org.ja13.eau.sim.nbt.NbtThermalLoad("coolLoad")
    val voltageSource = org.ja13.eau.sim.mna.component.VoltageSource("Power Source", positiveLoad, null)

    private val thermalProcess = SterlingEnglineThermalProcess(this)
    val electricProcess = SterlingEngineElectricalProcess(this)

    init {
        electricalLoadList.add(inputLoad)
        electricalLoadList.add(positiveLoad)
        electricalComponentList.add(inputToTurbineResistor)
        thermalLoadList.add(warmLoad)
        thermalLoadList.add(coolLoad)
        electricalComponentList.add(voltageSource)
        thermalFastProcessList.add(thermalProcess)

        val explosion = org.ja13.eau.sim.process.destruct.WorldExplosion(this).machineExplosion()
        val thermalWatchdog = org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog()
        thermalWatchdog.set(warmLoad).setTMax(800.0).set(explosion)
    }

    override fun connectJob() {
        super.connectJob()
        org.ja13.eau.EAU.simulator.mna.addProcess(electricProcess)
    }

    override fun disconnectJob() {
        super.disconnectJob()
        org.ja13.eau.EAU.simulator.mna.removeProcess(electricProcess)
    }

    override fun getElectricalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ElectricalLoad? {
        if (lrdu != LRDU.Down) return null
        if (side == front) return inputLoad
        if (side == front.back()) return inputLoad
        return null
    }

    override fun getThermalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ThermalLoad? {
        if (side == front.left()) return warmLoad
        if (side == front.right()) return coolLoad
        return null
    }

    override fun getConnectionMask(side: Direction, lrdu: LRDU): Int {
        if (lrdu == LRDU.Down) {
            if (side == front) return org.ja13.eau.node.NodeBase.MASK_ELECTRIC
            if (side == front.back()) return org.ja13.eau.node.NodeBase.MASK_ELECTRIC
            if (side == front.left()) return org.ja13.eau.node.NodeBase.MASK_THERMAL
            if (side == front.right()) return org.ja13.eau.node.NodeBase.MASK_THERMAL
        }
        return 0
    }

    override fun multiMeterString(side: Direction?): String {
        return Utils.plotVolt(positiveLoad.u) + " " + Utils.plotAmpere(positiveLoad.current)
    }

    override fun thermoMeterString(side: Direction?): String {
        return Utils.plotCelsius(warmLoad.Tc - coolLoad.Tc, "Delta Temperature")
    }

    override fun initialize() {
        descriptor.applyTo(inputLoad)
        descriptor.applyTo(warmLoad)
        descriptor.applyTo(coolLoad)
        inputToTurbineResistor.r = org.ja13.eau.EAU.getSmallRs()
        connect()
    }

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        node.lrduCubeMask.getTranslate(front.down()).serialize(stream)
        stream.writeDouble(warmLoad.Tc - coolLoad.Tc)
    }

    override fun getWaila(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        map["Delta Temperature"] = Utils.plotCelsius(warmLoad.Tc - coolLoad.Tc)
        if (org.ja13.eau.EAU.wailaEasyMode) {
            map["Voltage"] = Utils.plotVolt(voltageSource.u)
            map["Current"] = Utils.plotAmpere(voltageSource.current)
        }
        return map
    }
}

class SterlingEngineElectricalProcess(val element: SterlingEngineElement): org.ja13.eau.sim.IProcess, org.ja13.eau.sim.mna.misc.IRootSystemPreStepProcess {

    override fun process(time: Double) {
        val powerOutPerDeltaU = 50.0
        val deltaT = element.warmLoad.Tc - element.coolLoad.Tc
        val targetU = element.descriptor.dTtoU.getValue(deltaT)

        val th = element.positiveLoad.subSystem.getTh(element.positiveLoad, element.voltageSource)
        var Ut = when {
            targetU < th.U -> {
                th.U
            }
            th.isHighImpedance -> {
                targetU
            }
            else -> {
                val a = 1 / th.R
                val b: Double = powerOutPerDeltaU - th.U / th.R
                val c: Double = -powerOutPerDeltaU * targetU
                (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a)
            }
        }

        val i = (Ut - th.U) / th.R
        val p = i * Ut
        val pMax = 3000.0 * 1.5

        if (p > pMax) {
            Ut = (Math.sqrt(th.U * th.U + 4 * pMax * th.R) + th.U) / 2
            Ut = Math.min(Ut, targetU)
            if (!Ut.isFinite()) Ut = 0.0
            if (Ut < th.U) Ut = th.U
        }
        element.voltageSource.u = Ut
    }

    override fun rootSystemPreStepProcess() {
        process(0.0)
    }
}

class SterlingEnglineThermalProcess(val element: SterlingEngineElement): org.ja13.eau.sim.IProcess {

    private val powerOutputMap = FunctionTable(doubleArrayOf(0.0, 0.2,
        0.4, 0.6, 0.8, 1.0, 1.3, 1.8, 2.7), 8.0 / 5.0)

    override fun process(time: Double) {
        var efficiency = Math.abs(1 - (element.coolLoad.Tc + org.ja13.eau.sim.PhysicalConstant.Tref) / (element.warmLoad.Tc + org.ja13.eau.sim.PhysicalConstant.Tref))
        if (efficiency < 0.05) efficiency = 0.05
        val e = element.voltageSource.p * time / org.ja13.eau.EAU.heatTurbinePowerFactor
        val pOut = e / time
        val pIn = powerOutputMap.getValue(pOut) / efficiency
        element.warmLoad.movePowerTo(-pIn)
        element.coolLoad.movePowerTo(pIn * (1 - efficiency))
    }
}

class SterlingEngineRender(entity: org.ja13.eau.node.transparent.TransparentNodeEntity, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElementRender(entity, descriptor) {
    val descriptor = descriptor as SterlingEngineDescriptor
    val factorLimiter = SlewLimiter(0.2)
    var connectionType: org.ja13.eau.cable.CableRenderType? = null

    val eConn = LRDUMask()
    val tConn = LRDUMask()

    init {
        addLoopedSound(object : LoopedSound("eln:heat_turbine_50v", coordonate(), ISound.AttenuationType.LINEAR) {
            override fun getVolume(): Float {
                return 0.1f * factorLimiter.position.toFloat()
            }

            override fun getPitch(): Float {
                return 0.9f + 0.2f * factorLimiter.position.toFloat()
            }
        })
    }

    override fun draw() {
        GL11.glPushMatrix()
        front.glRotateXnRef()
        GL11.glScalef(1.0f, 1.0f, 1.0f)
        descriptor.draw()
        GL11.glPopMatrix()
        connectionType = org.ja13.eau.cable.CableRender.connectionType(tileEntity, eConn, front.down())

        glCableTransforme(front.down())
        descriptor.cable.bindCableTexture()

        for (lrdu in LRDU.values()) {
            setGlColorFromDye(connectionType!!.otherdry[lrdu.toInt()])
            if (!eConn[lrdu]) continue
            if (lrdu !== front.down().getLRDUGoingTo(front) && lrdu.inverse() !== front.down().getLRDUGoingTo(front)) continue
            tConn.set(1 shl lrdu.toInt())
            org.ja13.eau.cable.CableRender.drawCable(descriptor.cable, tConn, connectionType)
        }
    }

    override fun getCableRender(side: Direction, lrdu: LRDU): org.ja13.eau.cable.CableRenderDescriptor? {
        if (lrdu == LRDU.Down) {
            if (side == front) return descriptor.cable
            if (side == front.back()) return descriptor.cable
        }
        return null
    }

    override fun refresh(deltaT: Double) {
        factorLimiter.step(deltaT)
        super.refresh(deltaT)
    }

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        eConn.deserialize(stream)
        val deltaT = stream.readDouble()
        if (deltaT >= 40.0) {
            factorLimiter.target = deltaT / 350.0
        }
        factorLimiter.target = 0.0
    }
}
