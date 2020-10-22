package org.ja13.eau.sixnode.electricaldatalogger;

import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.gui.ItemStackFilter;
import org.ja13.eau.gui.SlotFilter;
import org.ja13.eau.misc.BasicContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.gui.ItemStackFilter;
import org.ja13.eau.gui.SlotFilter;
import org.ja13.eau.misc.BasicContainer;

public class ElectricalDataLoggerContainer extends BasicContainer {

    public static final int paperSlotId = 0;
    public static final int printSlotId = 1;

    public ElectricalDataLoggerContainer(EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
            new SlotFilter(inventory, paperSlotId, 176 / 2 - 44, 148, 64, new ItemStackFilter[]{new ItemStackFilter(Items.paper)}, ISlotSkin.SlotSkin.medium, new String[]{"Paper Slot"}),
            new GenericItemUsingDamageSlot(inventory, printSlotId, 176 / 2 + 45 - 17, 148, 1, DataLogsPrintDescriptor.class, ISlotSkin.SlotSkin.medium, new String[]{})
        });
    }
}
