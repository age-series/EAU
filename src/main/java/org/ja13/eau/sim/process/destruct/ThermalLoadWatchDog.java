package org.ja13.eau.sim.process.destruct;

import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.ThermalLoadInitializer;
import org.ja13.eau.sim.ThermalLoadInitializerByPowerDrop;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.ThermalLoadInitializer;
import org.ja13.eau.sim.ThermalLoadInitializerByPowerDrop;

public class ThermalLoadWatchDog extends ValueWatchdog {

    ThermalLoad state;

    @Override
    double getValue() {
        return state.getT();
    }

    public ThermalLoadWatchDog set(ThermalLoad state) {
        this.state = state;
        return this;
    }

    public ThermalLoadWatchDog setTMax(double tMax) {
        this.max = tMax;
        this.min = -40;
        this.timeoutReset = tMax * 0.1 * 10;
        return this;
    }

    public ThermalLoadWatchDog set(ThermalLoadInitializer t) {
        this.max = t.warmLimit;
        this.min = t.coolLimit;
        this.timeoutReset = max * 0.1 * 10;
        return this;
    }

    public ThermalLoadWatchDog setLimit(double thermalWarmLimit, double thermalCoolLimit) {
        this.max = thermalWarmLimit;
        this.min = thermalCoolLimit;
        this.timeoutReset = max * 0.1 * 10;
        return this;
    }

    public ThermalLoadWatchDog setLimit(ThermalLoadInitializerByPowerDrop t) {
        this.max = t.warmLimit;
        this.min = t.coolLimit;
        this.timeoutReset = max * 0.1 * 10;
        return this;
    }
}
