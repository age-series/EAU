package org.ja13.eau.sim.process.heater;

import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalLoad;

public class ElectricalLoadHeatThermalLoad implements IProcess {

    ElectricalLoad r;
    ThermalLoad load;

    public ElectricalLoadHeatThermalLoad(ElectricalLoad r, ThermalLoad load) {
        this.r = r;
        this.load = load;
    }

    @Override
    public void process(double time) {
        if (r.isNotSimulated()) return;
        double I = r.getI();
        load.movePowerTo(I * I * r.getRs() * 2);
    }

	/*double powerMax = 100000;
    public void setDeltaTPerSecondMax(double deltaTPerSecondMax) {
		powerMax = deltaTPerSecondMax*load.C;
	}*/
}
