package org.ja13.eau.transparentnode.electricalmachine;

import org.ja13.eau.misc.Direction;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import net.minecraft.item.ItemStack;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;

public class ElectricalMachineInventory extends TransparentNodeElementInventory {
    private ElectricalMachineElement machineElement;

    public ElectricalMachineInventory(int size, int stackLimit, ElectricalMachineElement machineElement) {
        super(size, stackLimit, machineElement);
        this.machineElement = machineElement;
    }

    public ElectricalMachineInventory(int size, int stackLimit, TransparentNodeElementRender TransparentnodeRender) {
        super(size, stackLimit, TransparentnodeRender);
    }

    ElectricalMachineDescriptor getDescriptor() {
        if (transparentNodeRender != null) return ((ElectricalMachineRender) transparentNodeRender).descriptor;
        if (transparentNodeElement != null) return ((ElectricalMachineElement) transparentNodeElement).descriptor;
        return null;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (transparentNodeElement == null) return new int[0];

        switch (Direction.fromIntMinecraftSide(side)) {
            case YP:
                return new int[]{machineElement.inSlotId};
            default:
                int[] slots = new int[machineElement.descriptor.outStackCount];
                for (int idx = 0; idx < slots.length; idx++) {
                    slots[idx] = idx + machineElement.outSlotId;
                }
                return slots;
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
