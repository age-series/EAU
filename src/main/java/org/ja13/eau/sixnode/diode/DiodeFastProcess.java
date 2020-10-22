package org.ja13.eau.sixnode.diode;

import org.ja13.eau.sim.mna.component.VoltageSource;
import org.ja13.eau.sim.mna.misc.IRootSystemPreStepProcess;
import org.ja13.eau.sim.mna.component.VoltageSource;
import org.ja13.eau.sim.mna.misc.IRootSystemPreStepProcess;

public class DiodeFastProcess implements IRootSystemPreStepProcess {

    VoltageSource source;

    DiodeFastProcess(VoltageSource source) {
        this.source = source;
    }

    @Override
    public void rootSystemPreStepProcess() {
        double u1 = 0, u2 = 0;
        if (source.aPin != null) {
            //source.getSubSystem().getTh(source.aPin, voltageSource)
        }
    }
}
