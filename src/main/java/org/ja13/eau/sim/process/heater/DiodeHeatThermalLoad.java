package org.ja13.eau.sim.process.heater;

import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;

public class DiodeHeatThermalLoad implements IProcess {

    Resistor r;
    ThermalLoad load;
    double lastR;

    public DiodeHeatThermalLoad(Resistor r, ThermalLoad load) {
        this.r = r;
        this.load = load;
        lastR = r.getR();
    }

    @Override
    public void process(double time) {
        if (r.getR() == lastR) {
            load.movePowerTo(r.getP());
        } else {
            lastR = r.getR();
        }
    }
}
