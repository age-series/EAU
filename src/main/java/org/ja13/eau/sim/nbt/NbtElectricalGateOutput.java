package org.ja13.eau.sim.nbt;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.EAU;

public class NbtElectricalGateOutput extends NbtElectricalLoad {

    public NbtElectricalGateOutput(String name) {
        super(name);
        EAU.smallInsulationLowCurrentCopperCable.applyTo(this);
    }

    public String plot(String str) {
        return Utils.plotSignal(getU(), getCurrent()); //return //str + " " + Utils.plotVolt("", getU()) + Utils.plotAmpere("", getCurrent());
    }
}
