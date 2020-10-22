package org.ja13.eau.sixnode.batterycharger;

import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.gui.SlotWithSkinAndComment;
import org.ja13.eau.item.MachineBoosterDescriptor;
import org.ja13.eau.item.electricalinterface.IItemEnergyBattery;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.misc.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.gui.SlotWithSkinAndComment;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.MachineBoosterDescriptor;
import org.ja13.eau.item.electricalinterface.IItemEnergyBattery;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.misc.Utils;

import static org.ja13.eau.i18n.I18N.tr;

public class BatteryChargerContainer extends BasicContainer {

    public static final int boosterSlotId = 4;

    static class BatterySlot extends SlotWithSkinAndComment {

        public BatterySlot(IInventory par1iInventory, int slot, int x, int y) {
            super(par1iInventory, slot, x, y, SlotSkin.medium, new String[]{I18N.tr("Battery slot")});
        }

        public boolean isItemValid(ItemStack itemStack) {
            Object d = Utils.getItemObject(itemStack);
            return d instanceof IItemEnergyBattery;
        }

        @Override
        public int getSlotStackLimit() {
            return 1;
        }
    }

    public BatteryChargerContainer(EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
            new BatterySlot(inventory, 0, 26 - 18 + 0, 6 + 0),
            new BatterySlot(inventory, 1, 26 - 18 + 18, 6 + 0),
            new BatterySlot(inventory, 2, 26 - 18 + 0, 6 + 18),
            new BatterySlot(inventory, 3, 26 - 18 + 18, 6 + 18),
            new GenericItemUsingDamageSlot(inventory, boosterSlotId, 80 - 18, 6 + 18 / 2, 5,
                MachineBoosterDescriptor.class,
                ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Booster slot")})
        });
    }
}
