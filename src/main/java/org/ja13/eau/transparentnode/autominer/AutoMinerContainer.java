package org.ja13.eau.transparentnode.autominer;

import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.item.ElectricalDrillDescriptor;
import org.ja13.eau.item.MiningPipeDescriptor;
import org.ja13.eau.item.OreScanner;
import org.ja13.eau.misc.BasicContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.ElectricalDrillDescriptor;
import org.ja13.eau.item.MiningPipeDescriptor;
import org.ja13.eau.item.OreScanner;
import org.ja13.eau.misc.BasicContainer;

import static org.ja13.eau.i18n.I18N.tr;

public class AutoMinerContainer extends BasicContainer {

    public static final int electricalDrillSlotId = 0;
    public static final int MiningPipeSlotId = 2;
    public static final int StorageStartId = 3;
    public static final int StorageSize = 0;
    public static final int inventorySize = StorageStartId + StorageSize;

    public AutoMinerContainer(EntityPlayer player, IInventory inventory) {
        super(player, inventory, newSlots(inventory));
    }

    public static Slot[] newSlots(IInventory inventory) {
        Slot[] slots = new Slot[StorageStartId + StorageSize];
        slots[0] = new GenericItemUsingDamageSlot(inventory, electricalDrillSlotId, 134, 8, 1,
            ElectricalDrillDescriptor.class, ISlotSkin.SlotSkin.medium,
            new String[]{I18N.tr("Drill slot")});
        slots[1] = new GenericItemUsingDamageSlot(inventory, 1, 3000, 3000, 1,
            OreScanner.class, ISlotSkin.SlotSkin.medium, new String[]{I18N.tr("Ore scanner slot")});
        slots[2] = new GenericItemUsingDamageSlot(inventory, MiningPipeSlotId, 134 + 18, 8, 64,
            MiningPipeDescriptor.class, ISlotSkin.SlotSkin.medium, new String[]{I18N.tr("Mining pipe slot")});

        return slots;
    }
}
