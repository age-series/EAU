package org.ja13.eau.sixnode.groundcable

import org.ja13.eau.EAU
import org.ja13.eau.cable.CableRenderDescriptor
import org.ja13.eau.i18n.I18N
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.Obj3D.Obj3DPart
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.six.SixNode
import org.ja13.eau.node.six.SixNodeDescriptor
import org.ja13.eau.node.six.SixNodeElement
import org.ja13.eau.node.six.SixNodeElementRender
import org.ja13.eau.node.six.SixNodeEntity
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.mna.component.VoltageSource
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer

class GroundCableDescriptor(name: String, obj3D: org.ja13.eau.misc.Obj3D): org.ja13.eau.node.six.SixNodeDescriptor(name, GroundCableElement::class.java, GroundCableRender::class.java) {
    var main: Obj3DPart = obj3D.getPart("main")

    init {
        voltageTier = VoltageTier.NEUTRAL
    }

    fun draw() {
        main.draw()
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

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)
        list.add(org.ja13.eau.i18n.I18N.tr("Provides a zero volt reference."))
        list.add(org.ja13.eau.i18n.I18N.tr("Can be used to set point on an"))
        list.add(org.ja13.eau.i18n.I18N.tr("electrical network to 0V potential."))
        list.add(org.ja13.eau.i18n.I18N.tr("Internal resistance: %1$\u2126", Utils.plotValue(org.ja13.eau.EAU.getSmallRs())))
    }
}

class GroundCableElement(sixNode: org.ja13.eau.node.six.SixNode, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor): org.ja13.eau.node.six.SixNodeElement(sixNode, side, descriptor) {
    val electricalLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("electricalLoad")
    val ground = org.ja13.eau.sim.mna.component.VoltageSource("ground", electricalLoad, null)

    init {
        ground.u = 0.0
        electricalLoadList.add(electricalLoad)
        electricalComponentList.add(ground)
    }

    override fun getElectricalLoad(lrdu: LRDU?, mask: Int): org.ja13.eau.sim.ElectricalLoad? {
        return electricalLoad
    }

    override fun getThermalLoad(lrdu: LRDU?, mask: Int): org.ja13.eau.sim.ThermalLoad? {
        return null
    }

    override fun getConnectionMask(lrdu: LRDU?): Int {
        return org.ja13.eau.node.NodeBase.MASK_ELECTRIC
    }

    override fun multiMeterString(): String? {
        return Utils.plotAmpere(electricalLoad.current)
    }

    override fun getWaila(): Map<String, String>? {
        val info: MutableMap<String, String> = HashMap()
        info[org.ja13.eau.i18n.I18N.tr("Current")] = Utils.plotAmpere(electricalLoad.i)
        return info
    }

    override fun thermoMeterString(): String? {
        return ""
    }

    override fun initialize() {
        org.ja13.eau.EAU.applySmallRs(electricalLoad)
    }
}

class GroundCableRender(tileEntity: org.ja13.eau.node.six.SixNodeEntity, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor): org.ja13.eau.node.six.SixNodeElementRender(tileEntity, side, descriptor) {
    var descriptor: GroundCableDescriptor = descriptor as GroundCableDescriptor

    override fun draw() {
        super.draw()
        //if (side.isY) {
        //    front.glRotateOnX()
        //}
        descriptor.draw()
    }

    override fun getCableRender(lrdu: LRDU?): org.ja13.eau.cable.CableRenderDescriptor {
        return org.ja13.eau.EAU.uninsulatedHighCurrentRender
    }
}
