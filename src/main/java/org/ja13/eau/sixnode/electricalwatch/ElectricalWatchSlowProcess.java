package org.ja13.eau.sixnode.electricalwatch;

import org.ja13.eau.item.electricalitem.BatteryItem;
import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.sim.IProcess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.item.electricalitem.BatteryItem;
import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.sim.IProcess;

public class ElectricalWatchSlowProcess implements IProcess, INBTTReady {

    ElectricalWatchElement element;

    boolean upToDate = false;
    long oldDate = 1379;

    public ElectricalWatchSlowProcess(ElectricalWatchElement element) {
        this.element = element;
    }

    double getBatteryLevel() {
        ItemStack batteryStack = element.getInventory().getStackInSlot(ElectricalWatchContainer.batteryId);
        BatteryItem battery = (BatteryItem) BatteryItem.getDescriptor(batteryStack);
        if (battery != null) {
            return battery.getEnergy(batteryStack) / battery.getEnergyMax(batteryStack);
        } else {
            return 0;
        }
    }

    @Override
    public void process(double time) {
        ItemStack batteryStack = element.getInventory().getStackInSlot(ElectricalWatchContainer.batteryId);
        BatteryItem battery = (BatteryItem) BatteryItem.getDescriptor(batteryStack);
        double energy;
        if (battery == null || (energy = battery.getEnergy(batteryStack)) < element.descriptor.powerConsumtion * time * 4) {
            if (upToDate) {
                upToDate = false;
                oldDate = element.sixNode.coordonate.world().getWorldTime();
                if (batteryStack != null) battery.setEnergy(batteryStack, 0);
                element.needPublish();
            }
        } else {
            if (!upToDate) {
                upToDate = true;
                element.needPublish();
            }
            battery.setEnergy(batteryStack, energy - element.descriptor.powerConsumtion * time);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        upToDate = nbt.getBoolean(str + "upToDate");
        oldDate = nbt.getLong(str + "oldDate");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
        nbt.setBoolean(str + "upToDate", upToDate);
        nbt.setLong(str + "oldDate", oldDate);
    }
}
