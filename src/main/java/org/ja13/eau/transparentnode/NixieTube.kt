package org.ja13.eau.transparentnode

import org.ja13.eau.EAU
import org.ja13.eau.cable.CableRender
import org.ja13.eau.cable.CableRenderDescriptor
import org.ja13.eau.cable.CableRenderType
import org.ja13.eau.i18n.I18N.tr
import org.ja13.eau.misc.*
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.transparent.*
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.nbt.NbtElectricalGateInput
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import org.lwjgl.opengl.GL11
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

class NixieTubeDescriptor(name: String, val obj: org.ja13.eau.misc.Obj3D) : org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, NixieTubeElement::class.java, NixieTubeRender::class.java) {
    val display = obj.getPart("display")
    val base = obj.getPart("base")
    val tube = obj.getPart("tube")

    val pinDistance = Utils.getSixNodePinDistance(base)

    init {
        this.name = name
        voltageTier = VoltageTier.LOW_HOUSEHOLD
    }

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)
        list.add(tr("Displays a single glowing digit."))
    }

    fun draw(_digit: Int, blank: Boolean, _dots: Int) {
        var digit = _digit
        if(digit < 0) digit = 0
        if(digit > 9) digit = 9
        var dots = _dots
        if(dots < 0) dots = 0
        if(dots > 3) dots = 3

        base.draw()

        UtilsClient.enableBlend()
        UtilsClient.disableLight()
        UtilsClient.disableCulling()
        obj.bindTexture("digit_atlas.png")
        GL11.glColor4f(1f, 0.4f, 0.2f, 1.0f)
        if(blank) {
            display.draw(10.0f / 16.0f, 0.0f)
        } else {
            display.draw(digit.toFloat() / 16.0f, 0.0f)
            if(dots != 0) {
                display.draw((11.0f + dots.toFloat()) / 16.0f, 0.0f)
            }
        }
        UtilsClient.enableLight()
        UtilsClient.enableCulling()

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f)
        tube.draw()
        UtilsClient.disableBlend()
    }

    override fun getFrontFromPlace(side: Direction?, entityLiving: EntityLivingBase?): Direction {
        return super.getFrontFromPlace(side, entityLiving).inverse
    }
}

class NixieTubeElement(node: org.ja13.eau.node.transparent.TransparentNode, _descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : org.ja13.eau.node.transparent.TransparentNodeElement(node, _descriptor) {
    val descriptor = _descriptor as NixieTubeDescriptor

    val digitIn = org.ja13.eau.sim.nbt.NbtElectricalGateInput("digitIn")
    val blankIn = org.ja13.eau.sim.nbt.NbtElectricalGateInput("blankIn")
    val dotsIn = org.ja13.eau.sim.nbt.NbtElectricalGateInput("dotsIn")

    var curDigit = 0
    var lastDigit = 0
    var curBlank = false
    var lastBlank = false
    var curDots = 0
    var lastDots = 0

    inner class NixieTubeProcess : org.ja13.eau.sim.IProcess {
        override fun process(time: Double) {
            //Utils.println("NTP.p")
            curDigit = (digitIn.normalized * 10.0).toInt()
            if (curDigit > 9) curDigit = 9
            curBlank = (blankIn.normalized >= 0.5)
            curDots = (dotsIn.normalized * 4.0).toInt()
            if (curDots > 3) curDots = 3
            if (curDigit != lastDigit) {
                lastDigit = curDigit
                needPublish()
            }
            if (curBlank != lastBlank) {
                lastBlank = curBlank
                needPublish()
            }
            if(curDots != lastDots) {
                lastDots = curDots
                needPublish()
            }
        }
    }

    var process: NixieTubeProcess? = null

    override fun initialize() {
        electricalLoadList.add(digitIn)
        electricalLoadList.add(blankIn)
        electricalLoadList.add(dotsIn)
        process = NixieTubeProcess()
        slowProcessList.add(process)
        reconnect()
        Utils.println("NTE.initialize")
    }

    override fun getElectricalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ElectricalLoad? {
        if(lrdu != LRDU.Down) return null
        return when(side) {
            front -> dotsIn
            front.inverse -> digitIn
            else ->  blankIn
        }
    }

    override fun getConnectionMask(side: Direction?, lrdu: LRDU?): Int {
        if(lrdu != LRDU.Down) return 0
        return org.ja13.eau.node.NodeBase.MASK_ELECTRIC
    }

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        //Utils.println("NTE.nS")
        try {
            node.lrduCubeMask.getTranslate(front.down()).serialize(stream)
            stream.writeByte(curDigit)
            stream.writeBoolean(curBlank)
            stream.writeByte(curDots)
        } catch(e: IOException) {
            e.printStackTrace()
        }
    }

    override fun getThermalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ThermalLoad? = null
    override fun thermoMeterString(side: Direction?): String = ""
    override fun multiMeterString(side: Direction?): String =
        Utils.plotVolt(digitIn.voltage, "N:") + " " +
            Utils.plotVolt(blankIn.voltage, "B:") + " " +
            Utils.plotVolt(dotsIn.voltage, "D:")
    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float, vz: Float): Boolean = false

    override fun getWaila(): MutableMap<String, String> {
        var info = HashMap<String, String>()
        info.put("Digit", curDigit.toString())
        info.put("Blank", curBlank.toString())
        info.put("Dots", when(curDots) {
            1 -> "low"
            2 -> "high"
            3 -> "both"
            else -> "none"
        })
        return info
    }
}

class NixieTubeRender(entity: org.ja13.eau.node.transparent.TransparentNodeEntity, _descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : org.ja13.eau.node.transparent.TransparentNodeElementRender(entity, _descriptor) {
    val descriptor = _descriptor as NixieTubeDescriptor

    var digit = 0
    var blank = false
    var dots = 0

    var connTypes: Array<org.ja13.eau.cable.CableRenderType?>? = null
    var connection = LRDUMask()

    override fun draw() {
        preserveMatrix {
            front.glRotateXnRef()
            descriptor.draw(digit, blank, dots)
        }

        preserveMatrix {
            if (connTypes == null) {
                connTypes = arrayOfNulls(4)

                for (lrdu in LRDU.values()) {
                    connTypes!!.set(lrdu.ordinal, org.ja13.eau.cable.CableRender.connectionType(tileEntity, LRDUMask(1.shl(lrdu.ordinal)), front.down()))
                }
            }
            glCableTransforme(front.down())
            for(lrdu in LRDU.values()) {
                val render = getCableRender(front.down(), lrdu)
                if (render != null) {
                    render.bindCableTexture()
                    Utils.setGlColorFromDye(connTypes!!.get(lrdu.ordinal)!!.otherdry[lrdu.toInt()])
                    val mask = LRDUMask(1.shl(lrdu.ordinal))
                    org.ja13.eau.cable.CableRender.drawCable(render, mask, connTypes!!.get(lrdu.ordinal))
                }
            }
        }
    }

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        //Utils.println("NTR.nU")
        try {
            connection.deserialize(stream)
            digit = stream.readByte().toInt()
            blank = stream.readBoolean()
            dots = stream.readByte().toInt()
        } catch(e: IOException) {
            e.printStackTrace()
        }
        connTypes = null  // Force refresh
    }

    override fun getCableRender(side: Direction?, lrdu: LRDU): org.ja13.eau.cable.CableRenderDescriptor? {
        return if (connection.get(lrdu)) { org.ja13.eau.EAU.smallInsulationLowCurrentRender } else { null }
    }
}
