package org.ja13.eau.sim.nbt;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.mna.SubSystem;
import org.ja13.eau.sim.mna.component.Capacitor;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.mna.SubSystem;
import org.ja13.eau.sim.mna.component.Capacitor;

public class NbtElectricalGateOutputProcess extends Capacitor implements INBTTReady {

    double U;
    String name;

    boolean highImpedance = false;

    public NbtElectricalGateOutputProcess(String name, ElectricalLoad positiveLoad) {
        super(positiveLoad, null);
        this.name = name;
        setHighImpedance(false);
    }

    public void setHighImpedance(boolean enable) {
        this.highImpedance = enable;
        double baseC = EAU.gateOutputCurrent / EAU.electricalFrequency / VoltageTier.TTL.getVoltage();
        if (enable) {
            setC(baseC / 1000);
        } else {
            setC(baseC);
        }
    }

    @Override
    public void simProcessI(SubSystem s) {
        if (!highImpedance)
            aPin.state = U;
        super.simProcessI(s);
    }

    public boolean isHighImpedance() {
        return highImpedance;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        setHighImpedance(nbt.getBoolean(str + name + "highImpedance"));
        U = nbt.getDouble(str + name + "U");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
        nbt.setBoolean(str + name + "highImpedance", highImpedance);
        nbt.setDouble(str + name + "U", U);
    }

    public void setOutputNormalized(double value) {
        setOutputNormalizedSafe(value);
    }

    public void state(boolean value) {
        if (value)
            U = VoltageTier.TTL.getVoltage();
        else
            U = 0.0;
    }

    public double getOutputNormalized() {
        return U / VoltageTier.TTL.getVoltage();
    }

    public boolean getOutputOnOff() {
        return U >= VoltageTier.TTL.getVoltage() / 2;
    }

    public void setOutputNormalizedSafe(double value) {
        if (value > 1.0) value = 1.0;
        if (value < 0.0) value = 0.0;
        if (Double.isNaN(value)) value = 0.0;
        U = value * VoltageTier.TTL.getVoltage();
    }

    public void setU(double U) {
        this.U = U;
    }

    public void setUSafe(double value) {
        value = Utils.limit(value, 0, VoltageTier.TTL.getVoltage());
        if (Double.isNaN(value)) value = 0.0;
        U = value;
    }

    public double getU() {
        return U;
    }
}
