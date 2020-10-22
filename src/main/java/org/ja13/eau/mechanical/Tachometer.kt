package org.ja13.eau.mechanical

import org.ja13.eau.EAU
import org.ja13.eau.cable.CableRenderDescriptor
import org.ja13.eau.cable.CableRenderType
import org.ja13.eau.gui.GuiHelper
import org.ja13.eau.gui.GuiScreenEln
import org.ja13.eau.gui.GuiTextFieldEln
import org.ja13.eau.gui.IGuiObject
import org.ja13.eau.i18n.I18N
import org.ja13.eau.item.IConfigurable
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.LRDUMask
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.transparent.EntityMetaTag
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeEntity
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.nbt.NbtElectricalGateOutput
import org.ja13.eau.sim.nbt.NbtElectricalGateOutputProcess
import org.ja13.eau.sixnode.electricaldatalogger.DataLogs
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.text.NumberFormat
import java.text.ParseException

class TachometerDescriptor(baseName: String, obj: org.ja13.eau.misc.Obj3D) : SimpleShaftDescriptor(baseName,
    TachometerElement::class, TachometerRender::class, org.ja13.eau.node.transparent.EntityMetaTag.Basic) {
    override val obj = obj
    override val static = arrayOf(obj.getPart("Stand"), obj.getPart("Cowl"))
    override val rotating = arrayOf(obj.getPart("Shaft"))
}

open class TachometerElement(node: org.ja13.eau.node.transparent.TransparentNode, desc_: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : SimpleShaftElement(node, desc_), org.ja13.eau.item.IConfigurable {
    companion object {
        val SetRangeEventId = 1

        val DefaultMinRads = 0.0f
        val DefaultMaxRads = 1000f
    }

    override val shaftMass = 0.5
    private val outputGate = org.ja13.eau.sim.nbt.NbtElectricalGateOutput("rpmOutput")
    private val outputGateProcess = org.ja13.eau.sim.nbt.NbtElectricalGateOutputProcess("rpmOutputProcess", outputGate)
    private var minRads = DefaultMinRads
    private var maxRads = DefaultMaxRads
    private val outputGateSlowProcess = org.ja13.eau.sim.IProcess {
        outputGateProcess.setOutputNormalizedSafe((this.shaft.rads - minRads) / (maxRads - minRads))
    }

    init {
        electricalLoadList.add(outputGate)
        electricalComponentList.add(outputGateProcess)
        slowProcessList.add(outputGateSlowProcess)
    }

    override fun getElectricalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ElectricalLoad? = outputGate

    override fun getThermalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ThermalLoad? = null

    override fun getConnectionMask(side: Direction?, lrdu: LRDU?): Int = if (side == front || side == front.inverse) {
        org.ja13.eau.node.NodeBase.MASK_ELECTRIC
    } else {
        0
    }

    override fun thermoMeterString(side: Direction?): String? = null

    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float,
                                  vz: Float): Boolean = false

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        node.lrduCubeMask.getTranslate(Direction.YN).serialize(stream)
        stream.writeFloat(minRads)
        stream.writeFloat(maxRads)
    }

    override fun hasGui(): Boolean = true

    override fun networkUnserialize(stream: DataInputStream?): Byte {
        val type = super.networkUnserialize(stream)
        when (type.toInt()) {
            SetRangeEventId -> {
                minRads = stream?.readFloat() ?: DefaultMinRads
                maxRads = stream?.readFloat() ?: DefaultMaxRads
                needPublish()
                return unserializeNulldId
            }
        }
        return type
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        minRads = nbt.getFloat("minRads")
        maxRads = nbt.getFloat("maxRads")
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        nbt.setFloat("minRads", minRads)
        nbt.setFloat("maxRads", maxRads)
    }

    override fun getWaila(): Map<String, String> {
        return mapOf()
    }

    override fun readConfigTool(compound: NBTTagCompound, invoker: EntityPlayer) {
        if(compound.hasKey("min"))
            minRads = compound.getFloat("min")
        if(compound.hasKey("max"))
            maxRads = compound.getFloat("max")
        needPublish()
    }

    override fun writeConfigTool(compound: NBTTagCompound, invoker: EntityPlayer) {
        compound.setFloat("min", minRads)
        compound.setFloat("max", maxRads)
        compound.setByte("unit", org.ja13.eau.sixnode.electricaldatalogger.DataLogs.noType)
    }
}

class TachometerRender(entity: org.ja13.eau.node.transparent.TransparentNodeEntity, desc: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : ShaftRender(entity, desc) {
    override val cableRender: org.ja13.eau.cable.CableRenderDescriptor? = null
    private var renderPreProcess: org.ja13.eau.cable.CableRenderType? = null
    private val connections = LRDUMask()
    internal var minRads = TachometerElement.DefaultMinRads
    internal var maxRads = TachometerElement.DefaultMaxRads

    override fun draw() {
        renderPreProcess = drawCable(Direction.YN, org.ja13.eau.EAU.smallInsulationLowCurrentRender, connections, renderPreProcess)
        super.draw()
    }

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        connections.deserialize(stream)
        minRads = stream.readFloat()
        maxRads = stream.readFloat()
    }

    override fun newGuiDraw(side: Direction?, player: EntityPlayer?): GuiScreen? = TachometerGui(this)
}

class TachometerGui(val render: TachometerRender) : org.ja13.eau.gui.GuiScreenEln() {
    val validate: GuiButton by lazy { newGuiButton(82, 12, 80, org.ja13.eau.i18n.I18N.tr("Validate")) }
    val lowValue: org.ja13.eau.gui.GuiTextFieldEln by lazy { newGuiTextField(8, 24, 70) }
    val highValue: org.ja13.eau.gui.GuiTextFieldEln by lazy { newGuiTextField(8, 8, 70) }

    override fun newHelper(): org.ja13.eau.gui.GuiHelper? = org.ja13.eau.gui.GuiHelper(this, 169, 44)

    override fun initGui() {
        super.initGui()
        validate.enabled = true
        lowValue.setComment(org.ja13.eau.i18n.I18N.tr("Rads/s corresponding\nto 0% output").split("\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray())
        highValue.setComment(org.ja13.eau.i18n.I18N.tr("Rads/s corresponding\nto 100% output").split("\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray())
        lowValue.setText(render.minRads)
        highValue.setText(render.maxRads)
    }

    override fun guiObjectEvent(`object`: org.ja13.eau.gui.IGuiObject?) {
        super.guiObjectEvent(`object`)
        if (`object` === validate) {

            try {
                val minRads = NumberFormat.getInstance().parse(lowValue.text).toFloat()
                val maxRads = NumberFormat.getInstance().parse(highValue.text).toFloat()

                try {
                    val bos = ByteArrayOutputStream()
                    val stream = DataOutputStream(bos)

                    render.preparePacketForServer(stream)

                    stream.writeByte(TachometerElement.SetRangeEventId)
                    stream.writeFloat(minRads)
                    stream.writeFloat(maxRads)

                    render.sendPacketToServer(bos)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } catch (e: ParseException) {
            }
        }
    }
}
