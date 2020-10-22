package org.ja13.eau.transparentnode.electricalfurnace;

import org.ja13.eau.misc.Direction;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import net.minecraft.item.ItemStack;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;

public class ElectricalFurnaceInventory extends TransparentNodeElementInventory {

    public ElectricalFurnaceInventory(int size, int stackLimit, TransparentNodeElement TransparentNodeElement) {
        super(size, stackLimit, TransparentNodeElement);
    }

    public ElectricalFurnaceInventory(int size, int stackLimit, TransparentNodeElementRender TransparentnodeRender) {
        super(size, stackLimit, TransparentnodeRender);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        switch (Direction.fromIntMinecraftSide(side)) {
            case YP:
                return new int[]{ElectricalFurnaceElement.inSlotId};
            default:
                return new int[]{ElectricalFurnaceElement.outSlotId};
        }

    }

    @Override
    public boolean canInsertItem(int var1, ItemStack var2, int side) {
        switch (Direction.fromIntMinecraftSide(side)) {
            case YP:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean canExtractItem(int var1, ItemStack var2, int side) {
        switch (Direction.fromIntMinecraftSide(side)) {
            case YP:
                return false;
            default:
                return true;
        }
    }
}
