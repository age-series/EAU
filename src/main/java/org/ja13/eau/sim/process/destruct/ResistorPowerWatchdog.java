package org.ja13.eau.sim.process.destruct;

import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.mna.component.Resistor;

public class ResistorPowerWatchdog extends ValueWatchdog {

    Resistor resistor;

    public ResistorPowerWatchdog set(Resistor resistor) {
        this.resistor = resistor;
        return this;
    }

    public ResistorPowerWatchdog setPmax(double Pmax) {
        this.max = Pmax;
        this.min = -1;
        this.timeoutReset = Pmax * 0.20 * 5;

        return this;
    }

    @Override
    double getValue() {
        return resistor.getP();
    }
}
