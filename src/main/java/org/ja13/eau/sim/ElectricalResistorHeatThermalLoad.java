package org.ja13.eau.sim;

import org.ja13.eau.sim.mna.component.Resistor;

public class ElectricalResistorHeatThermalLoad implements IProcess {

    Resistor electricalResistor;
    ThermalLoad thermalLoad;

    public ElectricalResistorHeatThermalLoad(Resistor electricalResistor, ThermalLoad thermalLoad) {
        this.electricalResistor = electricalResistor;
        this.thermalLoad = thermalLoad;
    }

    @Override
    public void process(double time) {
        thermalLoad.PcTemp += electricalResistor.getP();
    }
}
