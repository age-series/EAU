package org.ja13.eau.item;

import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.sixnode.lampsocket.LampSocketType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;

import static org.ja13.eau.i18n.I18N.tr;

public class LampSlot extends GenericItemUsingDamageSlot {

    LampSocketType socket;

    public LampSlot(IInventory inventory, int slot, int x, int y, int stackLimit, LampSocketType socket) {
        super(inventory, slot, x, y, stackLimit, LampDescriptor.class, SlotSkin.medium, new String[]{I18N.tr("Lamp slot")});

        this.socket = socket;
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        if (!super.isItemValid(itemStack)) return false;
        LampDescriptor descriptor = (LampDescriptor) Utils.getItemObject(itemStack);
        return descriptor.socket == socket;
    }
}
