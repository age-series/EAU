package org.ja13.eau.sim.process.heater;

import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;

public class ResistorHeatThermalLoad implements IProcess {

    Resistor r;
    ThermalLoad load;

    public ResistorHeatThermalLoad(Resistor r, ThermalLoad load) {
        this.r = r;
        this.load = load;
    }

    @Override
    public void process(double time) {
        load.movePowerTo(r.getP());
    }
}
