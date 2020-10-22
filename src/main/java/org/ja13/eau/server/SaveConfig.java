package org.ja13.eau.server;

import org.ja13.eau.EAU;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import org.ja13.eau.EAU;

public class SaveConfig extends WorldSavedData {

    public static SaveConfig instance;

    public boolean heatFurnaceFuel = true;
    public boolean electricalLampAging = true;
    public boolean batteryAging = true;
    public boolean infinitePortableBattery = false;
    public boolean reGenOre = false;
    public double cableRsFactor_lastUsed = 1.0;

    public SaveConfig(String par1Str) {
        super(par1Str);
        instance = this;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        heatFurnaceFuel = nbt.getBoolean("heatFurnaceFuel");
        electricalLampAging = nbt.getBoolean("electricalLampAging");
        batteryAging = nbt.getBoolean("batteryAging");
        infinitePortableBattery = nbt.getBoolean("infinitPortableBattery");
        reGenOre = nbt.getBoolean("reGenOre");
        cableRsFactor_lastUsed = nbt.getDouble("cableRsFactor_lastUsed");

        EAU.wind.readFromNBT(nbt, "wind");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setBoolean("heatFurnaceFuel", heatFurnaceFuel);
        nbt.setBoolean("electricalLampAging", electricalLampAging);
        nbt.setBoolean("batteryAging", batteryAging);
        nbt.setBoolean("infinitPortableBattery", infinitePortableBattery);
        nbt.setBoolean("reGenOre", reGenOre);

        EAU.wind.writeToNBT(nbt, "wind");
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}
