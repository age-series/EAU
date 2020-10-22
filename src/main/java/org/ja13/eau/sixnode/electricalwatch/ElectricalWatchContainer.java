package org.ja13.eau.sixnode.electricalwatch;

import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.item.electricalitem.BatteryItem;
import org.ja13.eau.misc.BasicContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.electricalitem.BatteryItem;
import org.ja13.eau.misc.BasicContainer;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalWatchContainer extends BasicContainer {

    public static final int batteryId = 0;

    public ElectricalWatchContainer(EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
            new GenericItemUsingDamageSlot(inventory, batteryId, 184 / 2 - 12, 8, 1, BatteryItem.class, ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Portable battery slot")})});
    }
}
/*				new SlotFilter(inventory, 0, 62 + 0, 17, new ItemStackFilter[]{new ItemStackFilter(Block.wood, 0, 0)}),
                new SlotFilter(inventory, 1, 62 + 18, 17, new ItemStackFilter[]{new ItemStackFilter(Item.coal, 0, 0)})
*/
