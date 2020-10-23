package org.ja13.eau.sixnode.diode

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper
import org.ja13.eau.EAU
import org.ja13.eau.i18n.I18N.tr
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.KotlinHelper
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.LRDU.Companion.fromInt
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.Utils.plotAmpere
import org.ja13.eau.misc.Utils.plotCelsius
import org.ja13.eau.misc.Utils.plotVolt
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.six.SixNode
import org.ja13.eau.node.six.SixNodeDescriptor
import org.ja13.eau.node.six.SixNodeElement
import org.ja13.eau.node.six.SixNodeElementRender
import org.ja13.eau.node.six.SixNodeEntity
import org.ja13.eau.sim.DiodeProcess
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.ThermalLoadInitializer
import org.ja13.eau.sim.mna.component.ResistorSwitch
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import org.ja13.eau.sim.nbt.NbtThermalLoad
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog
import org.ja13.eau.sim.process.destruct.WorldExplosion
import org.ja13.eau.sim.process.heater.DiodeHeatThermalLoad
import org.lwjgl.opengl.GL11
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*

class DiodeDescriptor(
    name: String,
    var stdU: Double,
    var stdI: Double,
    var thermal: ThermalLoadInitializer,
    obj: Obj3D
) : SixNodeDescriptor(name, DiodeElement::class.java, DiodeRender::class.java) {

    private val base = obj.getPart("Base")
    private val diodeCables = obj.getPart("DiodeCables")
    private val diodeCore = obj.getPart("DiodeCore")

    override fun shouldUseRenderHelper(type: ItemRenderType, item: ItemStack, helper: ItemRendererHelper?): Boolean {
        return type != ItemRenderType.INVENTORY
    }

    override fun handleRenderType(item: ItemStack, type: ItemRenderType): Boolean {
        return true
    }

    override fun shouldUseRenderHelperEln(type: ItemRenderType, item: ItemStack, helper: ItemRendererHelper?): Boolean {
        return type != ItemRenderType.INVENTORY
    }

    override fun renderItem(type: ItemRenderType, item: ItemStack, vararg data: Any) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, *data)
        } else {
            GL11.glTranslatef(0.0f, 0.0f, -0.2f)
            GL11.glScalef(1.25f, 1.25f, 1.25f)
            GL11.glRotatef(-90f, 0f, 1f, 0f)
            draw()
        }
    }

    fun applyTo(load: ThermalLoad) {
        thermal.applyTo(load)
    }

    fun applyTo(load: ElectricalLoad) {
        EAU.applySmallRs(load)
    }

    fun applyTo(resistorSwitch: ResistorSwitch) {
        resistorSwitch.r = stdU / stdI
    }

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)
        Collections.addAll(list, *tr("Electrical current can only\nflow through the diode\nfrom anode to cathode").split("\n".toRegex()).toTypedArray())
    }

    fun draw() {
        base.draw()
        diodeCables.draw()
        diodeCore.draw()
    }

    init {
        thermal.setMaximalPower(stdU * stdI * 1.2)
        voltageTier = VoltageTier.NEUTRAL
    }
}

class DiodeElement(sixNode: SixNode?, side: Direction?, descriptor: SixNodeDescriptor) : SixNodeElement(sixNode, side, descriptor) {
    var descriptor: DiodeDescriptor = descriptor as DiodeDescriptor
    var anodeLoad = NbtElectricalLoad("anodeLoad")
    var cathodeLoad = NbtElectricalLoad("cathodeLoad")
    var resistorSwitch = ResistorSwitch("resistorSwitch", anodeLoad, cathodeLoad)
    var thermalLoad = NbtThermalLoad("thermalLoad")
    var heater = DiodeHeatThermalLoad(resistorSwitch, thermalLoad)
    var thermalWatchdog = ThermalLoadWatchDog()
    var diodeProcess = DiodeProcess(resistorSwitch)

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        val value = nbt.getByte("front")
        front = fromInt(KotlinHelper.shr(value, 0) and 0x3)
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        nbt.setByte("front", (front.toInt() shl 0).toByte())
    }

    override fun getElectricalLoad(lrdu: LRDU, mask: Int): ElectricalLoad? {
        if (front === lrdu) return anodeLoad
        return if (front.inverse() === lrdu) cathodeLoad else null
    }

    override fun getThermalLoad(lrdu: LRDU, mask: Int): ThermalLoad? {
        return thermalLoad
    }

    override fun getConnectionMask(lrdu: LRDU): Int {
        if (front === lrdu) return NodeBase.MASK_ELECTRIC
        return if (front.inverse() === lrdu) NodeBase.MASK_ELECTRIC else 0
    }

    override fun multiMeterString(): String {
        return plotVolt(anodeLoad.u, "U+:") + plotVolt(cathodeLoad.u, "U-:") + plotAmpere(anodeLoad.current, "")
    }

    override fun getWaila(): Map<String, String>? {
        val info: MutableMap<String, String> = HashMap()
        info[tr("Current")] = plotAmpere(anodeLoad.current, "")
        if (EAU.wailaEasyMode) {
            info[tr("Forward Voltage")] = plotVolt(anodeLoad.u - cathodeLoad.u, "")
            info[tr("Temperature")] = plotCelsius(thermalLoad.t, "")
        }
        return info
    }

    override fun thermoMeterString(): String {
        return plotCelsius(thermalLoad.Tc, "")
    }

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        stream.writeByte(front.toInt() shl 4)
    }

    override fun initialize() {
        descriptor.applyTo(cathodeLoad)
        descriptor.applyTo(anodeLoad)
        descriptor.applyTo(thermalLoad)
        descriptor.applyTo(resistorSwitch)
    }

    init {
        thermalLoad.setAsSlow()
        electricalLoadList.add(anodeLoad)
        electricalLoadList.add(cathodeLoad)
        thermalLoadList.add(thermalLoad)
        electricalComponentList.add(resistorSwitch)
        electricalProcessList.add(diodeProcess)
        slowProcessList.add(thermalWatchdog.set(thermalLoad).set(this.descriptor.thermal).set(WorldExplosion(this).cableExplosion()))
        thermalSlowProcessList.add(heater)
    }
}

class DiodeRender(tileEntity: SixNodeEntity?, side: Direction?, descriptor: SixNodeDescriptor) : SixNodeElementRender(tileEntity, side, descriptor) {
    private val descriptor = descriptor as DiodeDescriptor

    override fun draw() {
        front.glRotateOnX()
        descriptor.draw()
    }

    override fun publishUnserialize(stream: DataInputStream) {
        super.publishUnserialize(stream)
        try {
            val b: Byte
            b = stream.readByte()
            front = fromInt(KotlinHelper.shr(b, 4) and 3)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
