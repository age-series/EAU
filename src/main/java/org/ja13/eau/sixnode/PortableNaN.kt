package org.ja13.eau.sixnode

import org.ja13.eau.EAU
import org.ja13.eau.cable.CableRender
import org.ja13.eau.cable.CableRenderDescriptor
import org.ja13.eau.i18n.I18N.tr
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.UtilsClient
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.six.*
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.mna.component.Resistor
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import org.ja13.eau.sim.nbt.NbtThermalLoad
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.IItemRenderer
import org.lwjgl.opengl.GL11
import java.util.HashMap

class PortableNaNDescriptor(name: String, renderIn: org.ja13.eau.cable.CableRenderDescriptor): org.ja13.eau.sixnode.genericcable.GenericCableDescriptor(name, PortableNaNElement::class.java, PortableNaNRender::class.java) {

    init {
        this.name = name
        this.render = renderIn
    }

    override fun applyTo(electricalLoad: org.ja13.eau.sim.ElectricalLoad, rsFactor: Double) {
        electricalLoad.rs = Double.NaN
    }

    override fun applyTo(electricalLoad: org.ja13.eau.sim.ElectricalLoad) {
        electricalLoad.rs = Double.NaN
    }

    override fun applyTo(resistor: org.ja13.eau.sim.mna.component.Resistor) {
        resistor.r = Double.NaN
    }

    override fun applyTo(resistor: org.ja13.eau.sim.mna.component.Resistor, factor: Double) {
        resistor.r = Double.NaN
    }

    override fun applyTo(thermalLoad: org.ja13.eau.sim.ThermalLoad) = thermalLoad.set(Double.NaN, Double.NaN, Double.NaN)

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)

        list.add(tr("Nominal Ratings:"))
        list.add("  " + tr("Voltage: Yes"))
        list.add("  " + tr("Current: No"))
        list.add("  " + tr("Serial Resistance: OK Ω"))
    }

    override fun getNodeMask(): Int {
        return org.ja13.eau.node.NodeBase.maskElectricalAll
    }

    override fun handleRenderType(item: ItemStack, type: IItemRenderer.ItemRenderType): Boolean {
        return true
    }

    override fun renderItem(type: IItemRenderer.ItemRenderType, item: ItemStack, vararg data: Any) {
        if (icon == null)
            return
        val icon = icon!!.iconName.substring(4)
        UtilsClient.drawIcon(type, ResourceLocation("eau", "textures/blocks/$icon.png"))
    }
}

class PortableNaNElement(sixNode: org.ja13.eau.node.six.SixNode, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor): org.ja13.eau.node.six.SixNodeElement(sixNode, side, descriptor) {
    val electricalLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("Portable NaN")
    val thermalLoad = org.ja13.eau.sim.nbt.NbtThermalLoad("Portable NaN")
    val descriptor: PortableNaNDescriptor
    init {
        this.descriptor = descriptor as PortableNaNDescriptor
        electricalLoadList.add(electricalLoad)
        thermalLoadList.add(thermalLoad)
        thermalLoad.setAsSlow()
    }

    override fun getElectricalLoad(lrdu: LRDU, mask: Int): org.ja13.eau.sim.ElectricalLoad {
        return electricalLoad
    }

    override fun getThermalLoad(lrdu: LRDU, mask: Int): org.ja13.eau.sim.ThermalLoad {
        thermalLoad.movePowerTo(Double.NaN)
        return thermalLoad
    }

    override fun getConnectionMask(lrdu: LRDU): Int {
        return descriptor.nodeMask
    }

    override fun initialize() {
        descriptor.applyTo(electricalLoad)
        descriptor.applyTo(thermalLoad)
    }

    override fun multiMeterString(): String {
        return Utils.plotUIP(electricalLoad.u, electricalLoad.i)
    }

    override fun thermoMeterString(): String {
        return Utils.plotCelsius(thermalLoad.Tc)
    }

    override fun getWaila(): Map<String, String>? {
        val info = HashMap<String, String>()
        info[tr("Current")] = Utils.plotAmpere(electricalLoad.i)
        info[tr("Temperature")] = Utils.plotCelsius(thermalLoad.t)
        if (org.ja13.eau.EAU.wailaEasyMode) {
            info[tr("Voltage")] = Utils.plotVolt(electricalLoad.u)
        }
        try {
            val subSystemSize = electricalLoad.subSystem!!.componentSize()
            val textColor = when {
                subSystemSize <= 8 -> "§a"
                subSystemSize <= 15 -> "§6"
                else -> "§c"
            }
            info[tr("Subsystem Matrix Size: ")] = textColor + subSystemSize
        } catch (e: Exception) {
            info[tr("Subsystem Matrix Size: ")] = "§cNot part of a subsystem!?"
        }
        return info
    }
}

class PortableNaNRender(tileEntity: org.ja13.eau.node.six.SixNodeEntity, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor): org.ja13.eau.node.six.SixNodeElementRender(tileEntity, side, descriptor) {

    val descriptor: PortableNaNDescriptor

    init {
        this.descriptor = descriptor as PortableNaNDescriptor
    }

    override fun drawCableAuto(): Boolean {
        return false
    }

    override fun draw() {
        Minecraft.getMinecraft().mcProfiler.startSection("ACable")

        UtilsClient.bindTexture(descriptor.render?.cableTexture)
        glListCall()

        GL11.glColor3f(1f, 1f, 1f)
        Minecraft.getMinecraft().mcProfiler.endSection()
    }

    override fun glListDraw() {
        org.ja13.eau.cable.CableRender.drawCable(descriptor.render, connectedSide, org.ja13.eau.cable.CableRender.connectionType(this, side))
        org.ja13.eau.cable.CableRender.drawNode(descriptor.render, connectedSide, org.ja13.eau.cable.CableRender.connectionType(this, side))
    }

    override fun glListEnable(): Boolean {
        return true
    }

    override fun getCableRender(lrdu: LRDU): org.ja13.eau.cable.CableRenderDescriptor? {
        return descriptor.render
    }
}
