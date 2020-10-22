package org.ja13.eau.sim.nbt;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.EAU;

public class NbtElectricalGateInput extends NbtElectricalLoad {

    public NbtElectricalGateInput(String name) {
        super(name);
        EAU.smallInsulationLowCurrentCopperCable.applyTo(this);
    }

    public String plot(String str) {
        return Utils.plotSignal(getU(), getI()); // str  + " "+ Utils.plotVolt("", getU()) + Utils.plotAmpere("", getCurrent());
    }

    public boolean stateHigh() {
        return getU() > VoltageTier.TTL.getVoltage() * 0.6;
    }

    public boolean stateLow() {
        return getU() < VoltageTier.TTL.getVoltage() * 0.2;
    }

    public double getNormalized() {
        // System.out.println("getU: " + getU());
        double norm = getU() * (1.0 / VoltageTier.TTL.getVoltage());
        if (norm < 0.0) norm = 0.0;
        if (norm > 1.0) norm = 1.0;
        return norm;
    }

    public double getVoltage() {
        double U = this.getU();
        if (U < 0.0) U = 0.0;
        if (U > VoltageTier.TTL.getVoltage()) U = VoltageTier.TTL.getVoltage();
        return U;
    }
}
