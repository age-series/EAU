package org.ja13.eau.sixnode.electricalfiredetector

import org.ja13.eau.generic.GenericItemUsingDamageSlot
import org.ja13.eau.gui.ISlotSkin.SlotSkin
import org.ja13.eau.i18n.I18N.tr
import org.ja13.eau.item.electricalitem.BatteryItem
import org.ja13.eau.misc.BasicContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory

class ElectricalFireDetectorContainer(player: EntityPlayer, inventory: IInventory) :
    org.ja13.eau.misc.BasicContainer(player, inventory, arrayOf(
            org.ja13.eau.generic.GenericItemUsingDamageSlot(inventory, ElectricalFireDetectorContainer.BatteryId, 184 / 2 - 12, 8, 1,
                    org.ja13.eau.item.electricalitem.BatteryItem::class.java, SlotSkin.medium, arrayOf(tr("Portable battery slot"))))) {
    companion object {
        val BatteryId = 0
    }
}
