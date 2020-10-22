package org.ja13.eau.node.six;

import org.ja13.eau.EAU;
import org.ja13.eau.gui.SlotWithSkinAndComment;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.ja13.eau.EAU;
import org.ja13.eau.gui.SlotWithSkinAndComment;

public class SixNodeItemSlot extends SlotWithSkinAndComment {

    public SixNodeItemSlot(
        IInventory inventory, int slot,
        int x, int y,
        int stackLimit, Class[] descriptorClassList, SlotSkin skin, String[] comment
    ) {
        super(inventory, slot, x, y, skin, comment);
        this.stackLimit = stackLimit;
        this.descriptorClassList = descriptorClassList;
    }


    Class[] descriptorClassList;
    int stackLimit;


    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack itemStack) {
        if (itemStack.getItem() != EAU.sixNodeItem) return false;
        SixNodeDescriptor descriptor = EAU.sixNodeItem.getDescriptor(itemStack);

        for (Class classFilter : descriptorClassList) {
            if (descriptor.getClass().equals(classFilter)) return true;
        }
        return false;
    }


    @Override
    public int getSlotStackLimit() {

        //return super.getSlotStackLimit();
        return stackLimit;
    }

}
