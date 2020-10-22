package org.ja13.eau.sim.nbt;

import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.sim.FurnaceProcess;
import org.ja13.eau.sim.ThermalLoad;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.sim.FurnaceProcess;
import org.ja13.eau.sim.ThermalLoad;

public class NbtFurnaceProcess extends FurnaceProcess implements INBTTReady {

    String name;

    public NbtFurnaceProcess(String name, ThermalLoad load) {
        super(load);
        this.name = name;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound, String str) {
        combustibleEnergy = nbttagcompound.getFloat(str + name + "Q");
        setGain(nbttagcompound.getDouble(str + name + "gain"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound, String str) {
        nbttagcompound.setFloat(str + name + "Q", (float) combustibleEnergy);
        nbttagcompound.setDouble(str + name + "gain", getGain());
    }
}
