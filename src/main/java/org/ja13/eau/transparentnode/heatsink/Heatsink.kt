package org.ja13.eau.transparentnode.heatsink

import org.ja13.eau.EAU
import org.ja13.eau.i18n.I18N
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.Utils.plotCelsius
import org.ja13.eau.misc.Utils.plotPower
import org.ja13.eau.misc.Utils.plotValue
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeElement
import org.ja13.eau.node.transparent.TransparentNodeElementRender
import org.ja13.eau.node.transparent.TransparentNodeEntity
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.nbt.NbtThermalLoad
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog
import org.ja13.eau.sim.process.destruct.WorldExplosion
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper
import java.util.*

class HeatsinkDescriptor(name: String, val obj: org.ja13.eau.misc.Obj3D, val nominalP: Double = 250.0): org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, HeatsinkElement::class.java, HeatsinkRender::class.java) {
    val main = obj.getPart("main")
    val maxTemp = 800.0
    val thermalC = nominalP * (1.0/3.0)
    val thermalRp = 30.0 / nominalP
    val thermalRs = 1.0 / nominalP

    /*
    nonminalP = 200, nominalT = 30, nominalTao = 10, nominalConnectionDrop = 1, nominalElectricalCooling = 800
    thermalC = (nominalP + nominalElectricalCoolingPower) * nominalTao / nominalT;
    thermalRp = nominalT / nominalP;
    thermalRs = nominalConnectionDrop / (nominalP + nominalElectricalCoolingPower);
     */

    init {
        voltageTier = VoltageTier.NEUTRAL
    }

    fun draw() {
        main.draw()
    }

    fun applyTo(load: org.ja13.eau.sim.ThermalLoad) {
        load.set(thermalRs, thermalRp, thermalC)
    }

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)
        list.add(org.ja13.eau.i18n.I18N.tr("Used to cool down turbines."))
        list.add(org.ja13.eau.i18n.I18N.tr("Max. temperature: %1$°C", plotValue(maxTemp)))
        list.add(org.ja13.eau.i18n.I18N.tr("Nominal usage:"))
        list.add("  " + org.ja13.eau.i18n.I18N.tr("Cooling power: %1\$W", plotValue(nominalP)))
    }

    override fun handleRenderType(item: ItemStack?, type: ItemRenderType?): Boolean {
        return true
    }

    override fun shouldUseRenderHelper(type: ItemRenderType, item: ItemStack?,
                                       helper: ItemRendererHelper?): Boolean {
        return type != ItemRenderType.INVENTORY
    }

    override fun renderItem(type: ItemRenderType, item: ItemStack?, vararg data: Any?) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, *data)
        } else {
            draw()
        }
    }
}

class HeatsinkElement(node: org.ja13.eau.node.transparent.TransparentNode, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElement(node, descriptor) {
    val descriptor = descriptor as HeatsinkDescriptor

    val thermalLoad = org.ja13.eau.sim.nbt.NbtThermalLoad("thermalLoad")
    val thermalWatchdog = org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog()

    init {
        thermalLoadList.add(thermalLoad)
        slowProcessList.add(thermalWatchdog)

        thermalWatchdog
            .set(thermalLoad)
            .setTMax(this.descriptor.maxTemp)
            .set(org.ja13.eau.sim.process.destruct.WorldExplosion(this).machineExplosion())
    }

    override fun getElectricalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ElectricalLoad? {
        return null
    }

    override fun getThermalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ThermalLoad? {
        return if (side === Direction.YN || side === Direction.YP || lrdu !== LRDU.Down) null else thermalLoad
    }

    override fun getConnectionMask(side: Direction, lrdu: LRDU): Int {
        return if (side === Direction.YN || side === Direction.YP || lrdu !== LRDU.Down) 0 else org.ja13.eau.node.NodeBase.MASK_THERMAL
    }

    override fun multiMeterString(side: Direction?): String? {
        return ""
    }

    override fun thermoMeterString(side: Direction?): String? {
        return plotCelsius(thermalLoad.Tc, "") + plotPower(thermalLoad.power, "")
    }

    override fun initialize() {
        descriptor.applyTo(thermalLoad)
        connect()
    }

    override fun onBlockActivated(entityPlayer: EntityPlayer, side: Direction?, vx: Float, vy: Float, vz: Float): Boolean {
        val stack = entityPlayer.currentEquippedItem ?: return false
        if (stack.item === Items.water_bucket) {
            thermalLoad.Tc *= 0.5
            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, ItemStack(Items.bucket))
            return true
        }
        if (stack.item === Item.getItemFromBlock(Blocks.ice)) {
            thermalLoad.Tc *= 0.2
            if (stack.stackSize != 0) stack.stackSize-- else entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null)
            return true
        }
        return false
    }

    override fun getWaila(): Map<String, String>? {
        val info: MutableMap<String, String> = HashMap()
        info[org.ja13.eau.i18n.I18N.tr("Temperature")] = plotCelsius(thermalLoad.Tc, "")
        if (org.ja13.eau.EAU.wailaEasyMode) {
            info[org.ja13.eau.i18n.I18N.tr("Thermal power")] = plotPower(thermalLoad.power, "")
        }
        return info
    }
}

class HeatsinkRender(entity: org.ja13.eau.node.transparent.TransparentNodeEntity, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElementRender(entity, descriptor) {
    val descriptor = descriptor as HeatsinkDescriptor

    override fun draw() {
        front.glRotateXnRef()
        descriptor.draw()
    }
}
