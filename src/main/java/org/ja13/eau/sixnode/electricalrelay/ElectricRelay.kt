package org.ja13.eau.sixnode.electricalrelay

import org.ja13.eau.EAU
import org.ja13.eau.cable.CableRenderDescriptor
import org.ja13.eau.gui.GuiHelper
import org.ja13.eau.gui.GuiScreenEln
import org.ja13.eau.gui.IGuiObject
import org.ja13.eau.i18n.I18N
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.Obj3D.Obj3DPart
import org.ja13.eau.misc.RcInterpolator
import org.ja13.eau.misc.Utils.plotAmpere
import org.ja13.eau.misc.Utils.plotVolt
import org.ja13.eau.misc.UtilsClient.disableCulling
import org.ja13.eau.misc.UtilsClient.enableCulling
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.misc.VoltageTierHelpers.Companion.setGLColor
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.six.SixNode
import org.ja13.eau.node.six.SixNodeDescriptor
import org.ja13.eau.node.six.SixNodeElement
import org.ja13.eau.node.six.SixNodeElementRender
import org.ja13.eau.node.six.SixNodeEntity
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.NodeElectricalGateInputHysteresisProcess
import org.ja13.eau.sim.mna.component.Resistor
import org.ja13.eau.sim.nbt.NbtElectricalGateInput
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import org.ja13.eau.sixnode.electricalrelay.ElectricRelayDescriptor.Companion.DEFAULT_OPEN_NAME
import org.ja13.eau.sixnode.electricalrelay.ElectricRelayDescriptor.Companion.FRONT
import org.ja13.eau.sixnode.electricalrelay.ElectricRelayDescriptor.Companion.SWITCH_STATE_NAME
import org.ja13.eau.sound.SoundCommand
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper
import org.lwjgl.opengl.GL11
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*

class ElectricRelayDescriptor(name: String, val obj: org.ja13.eau.misc.Obj3D, val cable: org.ja13.eau.cable.CableRenderDescriptor):
    org.ja13.eau.node.six.SixNodeDescriptor(name, ElectricRelayElement::class.java, ElectricRelayRender::class.java) {

    companion object {
        const val TOGGLE_SIGNAL: Int = 0
        const val SWITCH_STATE_NAME = "switchState"
        const val DEFAULT_OPEN_NAME = "defaultOpen"
        const val FRONT = "front"
    }

    val main: Obj3DPart = obj.getPart("main")
    val relay0: Obj3DPart = obj.getPart("relay0")
    val relay1: Obj3DPart = obj.getPart("relay1")
    val backplate: Obj3DPart = obj.getPart("backplate")

    val r0rOff: Double
    val r0rOn: Double
    val r1rOff: Double
    val r1rOn: Double
    val speed: Double

    init {
        r0rOff = relay0.getFloat("rOff").toDouble()
        r0rOn = relay0.getFloat("rOn").toDouble()
        speed = relay0.getFloat("speed").toDouble()
        r1rOff = relay1.getFloat("rOff").toDouble()
        r1rOn = relay1.getFloat("rOn").toDouble()
        voltageTier = VoltageTier.NEUTRAL
    }

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        list.add(org.ja13.eau.i18n.I18N.TR("A relay is an electrical"))
        list.add(org.ja13.eau.i18n.I18N.TR("contact that conducts"))
        list.add(org.ja13.eau.i18n.I18N.TR("current when a signal"))
        list.add(org.ja13.eau.i18n.I18N.TR("voltage is applied."))
        list.add(org.ja13.eau.i18n.I18N.TR("The relay's input behaves"))
        list.add(org.ja13.eau.i18n.I18N.TR("like a Schmitt Trigger."))
    }

    override fun shouldUseRenderHelper(type: ItemRenderType, item: ItemStack?, helper: ItemRendererHelper?) = type != ItemRenderType.INVENTORY
    override fun shouldUseRenderHelperEln(type: ItemRenderType, item: ItemStack?, helper: ItemRendererHelper?) = type != ItemRenderType.INVENTORY

    override fun handleRenderType(item: ItemStack?, type: ItemRenderType?) = true

    override fun renderItem(type: ItemRenderType, item: ItemStack?, vararg data: Any?) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, *data)
        } else {
            draw(0f)
        }
    }

    fun draw(factor: Float) {
        disableCulling()
        GL11.glScalef(0.5f, 0.5f, 0.5f)
        main.draw()
        relay0.draw((factor * (r0rOn - r0rOff) + r0rOff).toFloat(), 0f, 0f, 1f)
        relay1.draw((factor * (r1rOn - r1rOff) + r1rOff).toFloat(), 0f, 0f, 1f)
        GL11.glPushMatrix()
        setGLColor(voltageTier)
        backplate.draw()
        GL11.glPopMatrix()
        enableCulling()
    }
}

class ElectricRelayElement(sixNode: org.ja13.eau.node.six.SixNode, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor): org.ja13.eau.node.six.SixNodeElement(sixNode, side, descriptor) {

    val descriptor = descriptor as ElectricRelayDescriptor

    val aLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("aLoad")
    val bLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("bLoad")
    val switchResistor = org.ja13.eau.sim.mna.component.Resistor(aLoad, bLoad)
    val controlInput = org.ja13.eau.sim.nbt.NbtElectricalGateInput("input")
    val switchProcess = ElectricRelayGateProcess(this, controlInput)

    var switchState = false
        set(x) {
            val tick = field != x
            field = x
            if (tick) {
                play(org.ja13.eau.sound.SoundCommand("random.click").mulVolume(0.1f, 2.0f).smallRange())
            }
            refreshSwitchResistor()
            needPublish()
        }
    var defaultOpen = true

    init {
        electricalLoadList.add(aLoad)
        electricalLoadList.add(bLoad)
        electricalComponentList.add(switchResistor)
        electricalProcessList.add(switchProcess)
        electricalLoadList.add(controlInput)
        electricalComponentList.add(org.ja13.eau.sim.mna.component.Resistor(aLoad, null).pullDown())
        electricalComponentList.add(org.ja13.eau.sim.mna.component.Resistor(bLoad, null).pullDown())
    }

    fun canBePlacedOnSide(side: Direction?, type: Int) = true

    override fun readFromNBT(nbt: NBTTagCompound) {
        front.dir = nbt.getInteger(FRONT)
        switchState = nbt.getBoolean(SWITCH_STATE_NAME)
        defaultOpen = nbt.getBoolean(DEFAULT_OPEN_NAME)
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        nbt.setInteger(FRONT, front.dir)
        nbt.setBoolean(SWITCH_STATE_NAME, switchState)
        nbt.setBoolean(DEFAULT_OPEN_NAME, defaultOpen)
    }

    override fun getElectricalLoad(lrdu: LRDU, mask: Int): org.ja13.eau.sim.ElectricalLoad? {
        if (front.left() === lrdu) return aLoad
        if (front.right() === lrdu) return bLoad
        return if (front === lrdu) controlInput else null
    }

    override fun getThermalLoad(lrdu: LRDU?, mask: Int) = null

    override fun getConnectionMask(lrdu: LRDU): Int {
        if (front.left() === lrdu) return org.ja13.eau.node.NodeBase.MASK_ELECTRIC
        if (front.right() === lrdu) return org.ja13.eau.node.NodeBase.MASK_ELECTRIC
        return if (front === lrdu) org.ja13.eau.node.NodeBase.MASK_ELECTRIC else 0
    }

    override fun multiMeterString(): String? {
        return "${plotVolt(aLoad.u, "Ua:")} ${plotVolt(bLoad.u, "Ub:")} ${plotAmpere(aLoad.current)}"
    }

    override fun getWaila(): Map<String, String>? {
        val info: MutableMap<String, String> = HashMap()
        info[org.ja13.eau.i18n.I18N.tr("Position")] = if (switchState) org.ja13.eau.i18n.I18N.tr("Closed") else org.ja13.eau.i18n.I18N.tr("Open")
        info[org.ja13.eau.i18n.I18N.tr("Current")] = plotAmpere(aLoad.current)
        if (org.ja13.eau.EAU.wailaEasyMode) {
            info[org.ja13.eau.i18n.I18N.tr("Default position")] = if (defaultOpen) org.ja13.eau.i18n.I18N.tr("Open") else org.ja13.eau.i18n.I18N.tr("Closed")
            info[org.ja13.eau.i18n.I18N.tr("Voltage")] = plotVolt(aLoad.u) + plotVolt(bLoad.u)
        }
        try {
            val subSystemSize = switchResistor.subSystem.component.size
            val textColor = when {
                subSystemSize <= 8 -> "§a"
                subSystemSize <= 15 -> "§6"
                else -> "§c"
            }
            info[org.ja13.eau.i18n.I18N.tr("Subsystem Matrix Size: ")] = textColor + subSystemSize
        } catch (e: Exception) {
            info[org.ja13.eau.i18n.I18N.tr("Subsystem Matrix Size: ")] = "§cNot part of a subsystem!?"
        }
        return info
    }

    override fun thermoMeterString() = ""

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        try {
            stream.writeBoolean(switchState)
            stream.writeBoolean(defaultOpen)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun refreshSwitchResistor() {
        if (!switchState) {
            switchResistor.ultraImpedance()
        } else {
            org.ja13.eau.EAU.applySmallRs(switchResistor)
        }
    }

    override fun initialize() {
        computeElectricalLoad()
        refreshSwitchResistor()
    }

    override fun inventoryChanged() {
        computeElectricalLoad()
    }

    fun computeElectricalLoad() {
        org.ja13.eau.EAU.applySmallRs(aLoad)
        org.ja13.eau.EAU.applySmallRs(bLoad)
        refreshSwitchResistor()
    }

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        try {
            when (stream.readByte()) {
                org.ja13.eau.sixnode.electricalrelay.ElectricalRelayElement.toogleOutputDefaultId -> {
                    defaultOpen = !defaultOpen
                    needPublish()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun hasGui() = true

    fun readConfigTool(compound: NBTTagCompound, invoker: EntityPlayer?) {
        if (compound.hasKey("no")) {
            defaultOpen = compound.getBoolean("no")
        }
        if (compound.hasKey("nc")) {
            defaultOpen = !compound.getBoolean("nc")
        }
    }

    fun writeConfigTool(compound: NBTTagCompound, invoker: EntityPlayer?) {
        compound.setBoolean("no", defaultOpen)
    }
}

class ElectricRelayRender(entity: org.ja13.eau.node.six.SixNodeEntity, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor): org.ja13.eau.node.six.SixNodeElementRender(entity, side, descriptor) {
    val descriptor = descriptor as ElectricRelayDescriptor
    val interpolator = RcInterpolator(this.descriptor.speed)

    var switchState = false
    var defaultOpen = true
    var boot = true

    override fun draw() {
        super.draw()
        drawSignalPin(front, floatArrayOf(2.5f, 2.5f, 2.5f, 2.5f))
        front.glRotateOnX()
        descriptor.draw(interpolator.get().toFloat())
    }

    override fun refresh(deltaT: Float) {
        interpolator.step(deltaT.toDouble())
    }

    override fun publishUnserialize(stream: DataInputStream) {
        super.publishUnserialize(stream)
        try {
            switchState = stream.readBoolean()
            defaultOpen = stream.readBoolean()
            interpolator.target = if (switchState) 1.0 else 0.0
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (boot) {
            interpolator.setValueFromTarget()
        }
        boot = false
    }

    fun clientToggleDefaultOutput() {
        clientSend(ElectricRelayDescriptor.TOGGLE_SIGNAL)
    }

    override fun newGuiDraw(side: Direction?, player: EntityPlayer?): GuiScreen? {
        return ElectricRelayGui(this)
    }

    override fun getCableRender(lrdu: LRDU): org.ja13.eau.cable.CableRenderDescriptor? {
        if (lrdu === front) return org.ja13.eau.EAU.smallInsulationLowCurrentRender
        return if (lrdu === front.left() || lrdu === front.right()) descriptor.cable else null
    }
}

class ElectricRelayGateProcess(val element: ElectricRelayElement, controlInput: org.ja13.eau.sim.nbt.NbtElectricalGateInput): org.ja13.eau.sim.NodeElectricalGateInputHysteresisProcess("switch", controlInput) {
    override fun setOutput(value: Boolean) {
        element.switchState = value xor !element.defaultOpen
    }
}

class ElectricRelayGui(val render: ElectricRelayRender): org.ja13.eau.gui.GuiScreenEln() {
    var toggleDefaultOutput: GuiButton? = null

    override fun initGui() {
        super.initGui()
        toggleDefaultOutput = newGuiButton(6, 32 / 2 - 10, 115, org.ja13.eau.i18n.I18N.tr("Toggle switch"))
    }

    override fun guiObjectEvent(`object`: org.ja13.eau.gui.IGuiObject) {
        super.guiObjectEvent(`object`)
        if (`object` === toggleDefaultOutput) {
            render.clientToggleDefaultOutput()
        }
    }

    override fun preDraw(f: Float, x: Int, y: Int) {
        super.preDraw(f, x, y)
        if (render.defaultOpen) toggleDefaultOutput!!.displayString = org.ja13.eau.i18n.I18N.tr("Normally open") else toggleDefaultOutput!!.displayString = org.ja13.eau.i18n.I18N.tr("Normally closed")
    }

    override fun newHelper(): org.ja13.eau.gui.GuiHelper? {
        return org.ja13.eau.gui.GuiHelper(this, 128, 32)
    }
}
