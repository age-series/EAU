package org.ja13.eau.sim.nbt;

import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.sim.ThermalLoad;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.sim.ThermalLoad;

public class NbtThermalLoad extends ThermalLoad implements INBTTReady {

    String name;

    public NbtThermalLoad(String name, double Tc, double Rp, double Rs, double C) {
        super(Tc, Rp, Rs, C);
        this.name = name;
    }

    public NbtThermalLoad(String name) {
        super();
        this.name = name;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound, String str) {
        Tc = nbttagcompound.getFloat(str + name + "Tc");
        if (Double.isNaN(Tc)) Tc = 0;
        if (Tc == Float.NEGATIVE_INFINITY) Tc = 0;
        if (Tc == Float.POSITIVE_INFINITY) Tc = 0;
    }

    public void writeToNBT(NBTTagCompound nbttagcompound, String str) {
        nbttagcompound.setFloat(str + name + "Tc", (float) Tc);
    }
}
