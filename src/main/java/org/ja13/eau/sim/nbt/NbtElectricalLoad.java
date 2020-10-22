package org.ja13.eau.sim.nbt;

import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.sim.ElectricalLoad;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.sim.ElectricalLoad;

public class NbtElectricalLoad extends ElectricalLoad implements INBTTReady {

    String name;

    public NbtElectricalLoad(String name) {
        super();
        this.name = name;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound, String str) {
        setU(nbttagcompound.getFloat(str + name + "Uc"));
        if (Double.isNaN(getU())) setU(0);
        if (getU() == Float.NEGATIVE_INFINITY) setU(0);
        if (getU() == Float.POSITIVE_INFINITY) setU(0);
    }

    public void writeToNBT(NBTTagCompound nbttagcompound, String str) {
        nbttagcompound.setFloat(str + name + "Uc", (float) getU());
    }
}
