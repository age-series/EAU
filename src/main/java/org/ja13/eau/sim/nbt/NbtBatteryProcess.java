package org.ja13.eau.sim.nbt;

import org.ja13.eau.misc.FunctionTable;
import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.sim.BatteryProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.VoltageSource;
import org.ja13.eau.sim.mna.state.VoltageState;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.sim.BatteryProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.VoltageSource;
import org.ja13.eau.sim.mna.state.VoltageState;

public class NbtBatteryProcess extends BatteryProcess implements INBTTReady {

    public NbtBatteryProcess(VoltageState positiveLoad, VoltageState negativeLoad, FunctionTable voltageFunction, double IMax, VoltageSource voltageSource) {
        super(positiveLoad, negativeLoad, voltageFunction, IMax, voltageSource);
    }

    public NbtBatteryProcess(VoltageState positiveLoad, VoltageState negativeLoad, FunctionTable voltageFunction, double IMax, VoltageSource voltageSource, ThermalLoad thermalLoad) {
        super(positiveLoad, negativeLoad, voltageFunction, IMax, voltageSource, thermalLoad);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound, String str) {
        Q = nbttagcompound.getDouble(str + "NBP" + "Q");
        if (Double.isNaN(Q)) Q = 0;
        life = nbttagcompound.getDouble(str + "NBP" + "life");
        if (Double.isNaN(life)) life = 1;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound, String str) {
        nbttagcompound.setDouble(str + "NBP" + "Q", Q);
        nbttagcompound.setDouble(str + "NBP" + "life", life);
    }

    public void setIMax(double iMax) {
        this.IMax = iMax;
    }
}
