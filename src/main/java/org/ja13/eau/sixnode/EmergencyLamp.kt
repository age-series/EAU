package org.ja13.eau.sixnode

import org.ja13.eau.cable.CableRenderDescriptor
import org.ja13.eau.gui.*
import org.ja13.eau.i18n.I18N.tr
import org.ja13.eau.misc.*
import org.ja13.eau.node.Node
import org.ja13.eau.node.NodePeriodicPublishProcess
import org.ja13.eau.node.published
import org.ja13.eau.node.six.*
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.mna.component.ResistorSwitch
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog
import org.ja13.eau.sim.process.destruct.WorldExplosion
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor
import org.ja13.eau.sixnode.lampsupply.LampSupplyElement
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.client.IItemRenderer
import org.lwjgl.opengl.GL11
import java.io.DataInputStream
import java.io.DataOutputStream

class EmergencyLampDescriptor(name: String, val cable: org.ja13.eau.sixnode.genericcable.GenericCableDescriptor, val batteryCapacity: Double,
                              val chargePower: Double, val consumption: Double, val lightLevel: Int, model: org.ja13.eau.misc.Obj3D)
    : org.ja13.eau.node.six.SixNodeDescriptor(name, EmergencyLampElement::class.java, EmergencyLampRender::class.java) {

    val mainCeiling: org.ja13.eau.misc.Obj3D.Obj3DPart = model.getPart("coreCeil")
    val panelCeiling: org.ja13.eau.misc.Obj3D.Obj3DPart = model.getPart("panelCeil")
    val lightCeiling: org.ja13.eau.misc.Obj3D.Obj3DPart = model.getPart("lightCeil")
    val mainWall: org.ja13.eau.misc.Obj3D.Obj3DPart = model.getPart("coreWall")
    val mainWallR: org.ja13.eau.misc.Obj3D.Obj3DPart = model.getPart("coreWallR")
    val mainWallL: org.ja13.eau.misc.Obj3D.Obj3DPart = model.getPart("coreWallL")
    val lightWall: org.ja13.eau.misc.Obj3D.Obj3DPart = model.getPart("lightWall")

    init {
        voltageTier = VoltageTier.NEUTRAL
        setDefaultIcon("emergencylamp")
    }

    fun draw(onCeiling: Boolean = false, on: Boolean = false, mirrorSign: Boolean = false) {
        if (onCeiling) {
            mainCeiling.draw()

            if (on) {
                preserveMatrix {
                    UtilsClient.drawLight(panelCeiling)
                    GL11.glColor3f(0.3f, 0.3f, 0.3f)
                    UtilsClient.drawLight(lightCeiling)
                }
            } else {
                panelCeiling.draw()
            }
        } else {
            if (on) {
                preserveMatrix {
                    UtilsClient.drawLight(mainWall)
                    UtilsClient.drawLight(if (mirrorSign) mainWallL else mainWallR)
                    GL11.glColor3f(0.3f, 0.3f, 0.3f)
                    UtilsClient.drawLight(lightWall)
                }
            } else {
                preserveMatrix {
                    mainWall.draw()
                    (if (mirrorSign) mainWallL else mainWallR).draw()
                }
            }
        }
    }

    override fun getFrontFromPlace(side: Direction?, player: EntityPlayer?)
        = super.getFrontFromPlace(side, player).inverse()

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        with(list) {
            add("As long as power is provided, the internal battery")
            add("is charged and the lamp is off. On a power failure,")
            add("the lamp turns on and runs on batteries.")
            add(Utils.plotVolt(cable.electricalNominalVoltage, "Nominal voltage:"))
            add(Utils.plotEnergy(batteryCapacity, "Battery capacity:"))
        }
    }

    override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper?) = type != IItemRenderer.ItemRenderType.INVENTORY
    override fun shouldUseRenderHelperEln(type: IItemRenderer.ItemRenderType, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper?) = type != IItemRenderer.ItemRenderType.INVENTORY

    override fun handleRenderType(item: ItemStack?, type: IItemRenderer.ItemRenderType?) = true

    override fun renderItem(type: IItemRenderer.ItemRenderType, item: ItemStack?, vararg data: Any?) {
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            super.renderItem(type, item, *data)
        } else {
            draw()
        }
    }
}

class EmergencyLampElement(sixNode: org.ja13.eau.node.six.SixNode, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor)
    : org.ja13.eau.node.six.SixNodeElement(sixNode, side, descriptor) {

    enum class Event(val value: Byte) {
        TOGGLE_POWERED_BY_CABLE(1),
        SET_CHANNEL(2)
    }

    val desc = descriptor as EmergencyLampDescriptor
    val load = org.ja13.eau.sim.nbt.NbtElectricalLoad("load")
    val chargingResistor = org.ja13.eau.sim.mna.component.ResistorSwitch("chargingResistor", load, null)
    var on by published(false, {
        sixNode.lightValue = if (it) desc.lightLevel else 0
    })
    var charge = desc.batteryCapacity / 2
    var poweredByCable by published(false, {
        if (it) isConnectedToLampSupply = false
    }, triggerReconnect = true)
    var channel by published("Default channel")
    var isConnectedToLampSupply by published(false)

    val process = org.ja13.eau.sim.IProcess { deltaT ->
        if (!poweredByCable) {
            var closestPowerSupply: org.ja13.eau.sixnode.lampsupply.LampSupplyElement.PowerSupplyChannelHandle? = null
            var closestDistance = 10000f

            org.ja13.eau.sixnode.lampsupply.LampSupplyElement.channelMap[channel]?.forEach {
                val distance = it.element.sixNode.coordonate.trueDistanceTo(sixNode.coordonate).toFloat()
                if (distance < closestDistance && distance <= it.element.range) {
                    closestDistance = distance
                    closestPowerSupply = it
                }
            }

            if (closestPowerSupply != null) {
                isConnectedToLampSupply = true
                if (closestPowerSupply!!.element.getChannelState(closestPowerSupply!!.id)) {
                    closestPowerSupply!!.element.addToRp(chargingResistor.r)
                    load.state = closestPowerSupply!!.element.powerLoad.state
                } else {
                    load.state = 0.0
                }
            } else {
                isConnectedToLampSupply = false
                load.state = 0.0
            }
        }

        if (chargingResistor.u > 0.5 * desc.cable.electricalNominalVoltage) {
            on = false
            if (charge < desc.batteryCapacity) {
                chargingResistor.state = true
                charge = Math.min(charge + chargingResistor.p * deltaT, desc.batteryCapacity)
            } else {
                chargingResistor.state = false
            }
        } else {
            chargingResistor.state = false
            if (charge > 0) {
                on = true
                charge = Math.max(charge - desc.consumption * deltaT, 0.0)
            } else {
                on = false
            }
        }
    }

    override fun initialize() {
        chargingResistor.r =
            desc.cable.electricalNominalVoltage * desc.cable.electricalNominalVoltage / desc.chargePower
        desc.cable.applyTo(load)

        electricalLoadList.add(load)
        electricalComponentList.add(chargingResistor)
        slowProcessList.add(process)
        slowProcessList.add(org.ja13.eau.node.NodePeriodicPublishProcess(sixNode, 2.0, 0.5))
        slowProcessList.add(org.ja13.eau.sim.process.destruct.VoltageStateWatchDog().set(load).setUNominal(desc.cable.electricalNominalVoltage)
            .set(org.ja13.eau.sim.process.destruct.WorldExplosion(this).cableExplosion()))
    }

    override fun getConnectionMask(lrdu: LRDU) = when {
        poweredByCable && side == Direction.YP -> org.ja13.eau.node.Node.MASK_ELECTRIC
        poweredByCable && (lrdu == front.left() || lrdu == front.right()) -> org.ja13.eau.node.Node.MASK_ELECTRIC
        else -> 0
    }

    override fun getElectricalLoad(lrdu: LRDU, mask: Int): org.ja13.eau.sim.ElectricalLoad? = load
    override fun getThermalLoad(lrdu: LRDU, mask: Int): org.ja13.eau.sim.ThermalLoad? = null
    override fun multiMeterString() = buildString {
        append(Utils.plotVolt(load.u))
        append(Utils.plotAmpere(load.i))
        append(Utils.plotPercent(charge / (sixNodeElementDescriptor as EmergencyLampDescriptor).batteryCapacity, "Charge:"))
    }
    override fun thermoMeterString() = ""
    override fun getWaila() = mapOf(
        "State" to when {
            on -> "On"
            chargingResistor.state -> "Charging..."
            charge <= 0.0 -> "Batteries empty"
            else -> "Fully charged"
        },
        "Charge" to Utils.plotPercent(charge / (sixNodeElementDescriptor as EmergencyLampDescriptor).batteryCapacity)
    )

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        stream.writeFloat(charge.toFloat() / desc.batteryCapacity.toFloat())
        stream.writeBoolean(on)
        stream.writeBoolean(poweredByCable)
        stream.writeUTF(channel)
        stream.writeBoolean(isConnectedToLampSupply)
    }

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        when (stream.readByte()) {
            Event.TOGGLE_POWERED_BY_CABLE.value -> poweredByCable = !poweredByCable
            Event.SET_CHANNEL.value -> channel = stream.readUTF()
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        on = nbt.getBoolean("on")
        charge = nbt.getDouble("charge")
        poweredByCable = nbt.getBoolean("poweredByCable")
        channel = nbt.getString("channel")
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        nbt.setBoolean("on", on)
        nbt.setDouble("charge", charge)
        nbt.setBoolean("poweredByCable", poweredByCable)
        nbt.setString("channel", channel)
    }

    override fun hasGui() = true
}

class EmergencyLampRender(entity: org.ja13.eau.node.six.SixNodeEntity, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor)
    : org.ja13.eau.node.six.SixNodeElementRender(entity, side, descriptor) {

    val desc = descriptor as EmergencyLampDescriptor
    var charge = 0f
    var on = false
    var poweredByCable = false
    var channel = "Default channel"
    var isConnectedToLampSupply = false

    override fun draw() {
        super.draw()
        front.glRotateOnX()
        desc.draw(side == Direction.YP, on, front == LRDU.Up)
    }

    override fun publishUnserialize(stream: DataInputStream) {
        super.publishUnserialize(stream)
        charge = stream.readFloat()
        on = stream.readBoolean()
        poweredByCable = stream.readBoolean()
        channel = stream.readUTF()
        isConnectedToLampSupply = stream.readBoolean()
    }

    override fun newGuiDraw(side: Direction?, player: EntityPlayer?) = EmergencyLampGui(this)

    override fun getCableRender(lrdu: LRDU?): org.ja13.eau.cable.CableRenderDescriptor? = if (poweredByCable) when {
        side == Direction.YP -> desc.cable.render
        lrdu == front.left() || lrdu == front.right() -> desc.cable.render
        else -> null
    } else null
}

class EmergencyLampGui(private var render: EmergencyLampRender)
    : org.ja13.eau.gui.GuiScreenEln() {
    private lateinit var buttonSupplyType: GuiButton
    private lateinit var channel: org.ja13.eau.gui.GuiTextFieldEln
    private lateinit var charge: org.ja13.eau.gui.GuiVerticalProgressBar

    override fun initGui() {
        super.initGui()
        buttonSupplyType = newGuiButton(18, 12, 140, "")
        channel = newGuiTextField(19, 38, 138)
        channel.setComment(0, tr("Specify the supply channel"))
        channel.text = render.channel
        charge = newGuiVerticalProgressBar(166, 12, 16, 39)
        charge.setColor(0.2f, 0.5f, 0.8f)
    }

    override fun guiObjectEvent(`object`: org.ja13.eau.gui.IGuiObject) {
        super.guiObjectEvent(`object`)
        if (`object` === buttonSupplyType) {
            render.clientSend(EmergencyLampElement.Event.TOGGLE_POWERED_BY_CABLE.value.toInt())
        } else if (`object` === channel) {
            render.clientSetString(EmergencyLampElement.Event.SET_CHANNEL.value, channel.text)
        }
    }

    override fun newHelper(): org.ja13.eau.gui.GuiHelperContainer = org.ja13.eau.gui.GuiHelperContainer(this, 196, 64, 8, 84)

    override fun preDraw(f: Float, x: Int, y: Int) {
        super.preDraw(f, x, y)

        if (!render.poweredByCable) {
            buttonSupplyType.displayString = tr("Powered by Lamp Supply")
            channel.visible = true
            if (render.isConnectedToLampSupply)
                channel.setComment(1, "§2" + tr("connected to " + render.channel))
            else
                channel.setComment(1, "§4" + tr("%1$ is not in range!", render.channel))
        } else {
            channel.visible = false
            buttonSupplyType.displayString = tr("Powered by cable")
        }
        charge.setValue(render.charge)
        charge.setComment(0, Utils.plotPercent(render.charge.toDouble(), "Charge:"))
    }
}
