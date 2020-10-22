package org.ja13.eau.ghost;

import org.ja13.eau.EAU;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import org.ja13.eau.EAU;

public class GhostManagerNbt extends WorldSavedData {
    public GhostManagerNbt(String par1Str) {
        super(par1Str);
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        EAU.ghostManager.loadFromNBT(nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        //Eln.ghostManager.saveToNbt(nbt, Integer.MIN_VALUE);
    }
}
