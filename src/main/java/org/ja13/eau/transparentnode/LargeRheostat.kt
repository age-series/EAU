package org.ja13.eau.transparentnode

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import org.ja13.eau.i18n.I18N.tr
import org.ja13.eau.misc.BlackBodyColor
import org.ja13.eau.misc.BlackBodyPower
import org.ja13.eau.misc.BlackBodyTemperature
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.SlewLimiter
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.transparentnode.heatsink.HeatsinkDescriptor
import org.lwjgl.opengl.GL11
import java.io.DataInputStream
import java.io.DataOutputStream

// TODO: Make the whole thing brighter when it heats up, not just redder.

class LargeRheostatDescriptor(name: String, val heatsink: HeatsinkDescriptor, val cable: org.ja13.eau.sixnode.genericcable.GenericCableDescriptor, val maxResistance: Double) :
    org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, LargeRheostatElement::class.java, LargeRheostatRender::class.java) {

    init {
        voltageTier = VoltageTier.NEUTRAL
    }

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)
        // TODO: Substantiate this with some data
        list.add(tr("Control resistance with signal"))
        list.add(tr("Dissapates ~4kW of heat passively"))

    }

    fun draw(position: Float = 0f) {
        heatsink.draw()
        GL11.glRotatef((1f - position) * 300f, 0f, 1f, 0f)
        heatsink.obj.getPart("wiper")?.draw()
    }

    override fun handleRenderType(item: ItemStack, type: IItemRenderer.ItemRenderType) = true
    override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType, item: ItemStack,
                                       helper: IItemRenderer.ItemRendererHelper) = type != IItemRenderer.ItemRenderType.INVENTORY

    override fun renderItem(type: IItemRenderer.ItemRenderType, item: ItemStack, vararg data: Any) =
        if (type != IItemRenderer.ItemRenderType.INVENTORY) draw() else super.renderItem(type, item, *data)
}

class LargeRheostatElement(node: org.ja13.eau.node.transparent.TransparentNode, desc_: org.ja13.eau.node.transparent.TransparentNodeDescriptor) :
    org.ja13.eau.node.transparent.TransparentNodeElement(node, desc_) {
    val desc = desc_ as LargeRheostatDescriptor

    val aLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("aLoad")
    val bLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("bLoad")
    val resistor = org.ja13.eau.sim.mna.component.Resistor(aLoad, bLoad).apply { r = desc.maxResistance }

    val control = org.ja13.eau.sim.nbt.NbtElectricalGateInput("control")
    val controlProcess = ControlProcess()

    val thermalLoad = org.ja13.eau.sim.nbt.NbtThermalLoad("thermalLoad")
    val heater = org.ja13.eau.sim.process.heater.ResistorHeatThermalLoad(resistor, thermalLoad)
    val thermalWatchdog = org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog()

    init {
        // Electrics
        grounded = false
        electricalLoadList.add(aLoad)
        electricalLoadList.add(bLoad)
        electricalComponentList.add(resistor)
        electricalLoadList.add(control)
        slowProcessList.add(controlProcess)
        // Heating
        thermalLoadList.add(thermalLoad)
        thermalFastProcessList.add(heater)
        slowProcessList.add(thermalWatchdog)
        thermalWatchdog.set(thermalLoad).setTMax(desc.heatsink.maxTemp)
            .set(org.ja13.eau.sim.process.destruct.WorldExplosion(this).machineExplosion())
    }

    inner class ControlProcess : org.ja13.eau.sim.IProcess {
        var lastC = -1000.0
        var lastH = -1000.0

        override fun process(time: Double) {
            //println("Normalized control: ${control.normalized}")
            val desiredRs = (control.normalized + 0.01) / 1.01 * desc.maxResistance
            if (desiredRs > lastC * 1.01 || desiredRs < lastC * 0.99) {
                resistor.r = desiredRs
                lastC = desiredRs
                needPublish()
            }
            if (thermalLoad.Tc > lastH * 1.05 || thermalLoad.Tc < lastH * 0.95) {
                lastH = thermalLoad.Tc
                needPublish()
            }
        }
    }

    override fun getElectricalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ElectricalLoad? {
        if (lrdu != LRDU.Down) return null
        return when (side) {
            front.right() -> aLoad
            front.left() -> bLoad
            front -> control
            else -> null
        }
    }

    override fun getThermalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ThermalLoad? {
        if (lrdu != LRDU.Down) return null
        // This one's insulated, since its max heat is way above that of cables.
        return when (side) {
            front.back() -> thermalLoad
            else -> null
        }
    }

    override fun getConnectionMask(side: Direction, lrdu: LRDU): Int {
        if (lrdu != LRDU.Down) return 0
        return when (side) {
            front -> org.ja13.eau.node.NodeBase.MASK_ELECTRIC
            front.back() -> org.ja13.eau.node.NodeBase.MASK_THERMAL
            else -> org.ja13.eau.node.NodeBase.MASK_ELECTRIC
        }
    }


    override fun multiMeterString(side: Direction): String {
        val u = -Math.abs(aLoad.u - bLoad.u)
        val i = Math.abs(resistor.i)
        return Utils.plotOhm(resistor.r, Utils.plotUIP(u, i)) + Utils.plotPercent(control.normalized, "Control Signal:")
    }

    override fun thermoMeterString(side: Direction) =
        Utils.plotCelsius(thermalLoad.Tc) + Utils.plotPower(thermalLoad.power, "Thermal Power:")

    override fun initialize() {
        desc.heatsink.applyTo(thermalLoad)
        aLoad.rs = org.ja13.eau.sim.mna.misc.MnaConst.noImpedance
        bLoad.rs = org.ja13.eau.sim.mna.misc.MnaConst.noImpedance
        connect()
    }

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        stream.writeDouble(thermalLoad.Tc)
        stream.writeDouble(control.normalized)
    }

    override fun getWaila(): Map<String, String> = mutableMapOf(
        Pair(tr("Resistance"), Utils.plotOhm(resistor.r)),
        Pair(tr("Temperature"), Utils.plotCelsius(thermalLoad.t)),
        Pair(tr("Power loss"), Utils.plotPower(resistor.p))
    )
}

class LargeRheostatRender(entity: org.ja13.eau.node.transparent.TransparentNodeEntity, desc: org.ja13.eau.node.transparent.TransparentNodeDescriptor) :
    org.ja13.eau.node.transparent.TransparentNodeElementRender(entity, desc) {
    val desc = desc as LargeRheostatDescriptor

    val baseColor = BlackBodyColor(1f, 1f, 1f)
    var color = BlackBodyTemperature(0f)
    val positionAnimator = SlewLimiter(0.3)

    init {
        positionAnimator.target = -1.0
    }

    override fun draw() {
        front.glRotateZnRef()
        // TODO: Get this thing *really* glowing.
        // glColor doesn't let me exceed 1.0, the way I'd quite like to do.
        GL11.glColor3f(color.red, color.green, color.blue)
        desc.draw(positionAnimator.position.toFloat())
    }

    override fun refresh(deltaT: Double) {
        super.refresh(deltaT)
        positionAnimator.step(deltaT)
    }

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        val temp = stream.readFloat() + 273
        val c = BlackBodyTemperature(temp)
        val p = BlackBodyPower(temp)
        val bbc = c * (p / desc.heatsink.nominalP.toFloat())
        color = (bbc + baseColor).normalize()

        if (positionAnimator.target == -1.0) {
            positionAnimator.target = stream.readDouble()
            positionAnimator.position = positionAnimator.target
        } else {
            positionAnimator.target = stream.readDouble()
        }
    }
}
