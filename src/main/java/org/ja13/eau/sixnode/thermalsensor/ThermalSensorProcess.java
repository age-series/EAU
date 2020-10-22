package org.ja13.eau.sixnode.thermalsensor;

import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.sim.IProcess;

public class ThermalSensorProcess implements IProcess {

    ThermalSensorElement sensor;

    public ThermalSensorProcess(ThermalSensorElement sensor) {
        this.sensor = sensor;
    }

    @Override
    public void process(double time) {
        if (sensor.typeOfSensor == ThermalSensorElement.temperatureType) {
            setOutput(sensor.thermalLoad.Tc);
        } else if (sensor.typeOfSensor == ThermalSensorElement.powerType) {
            setOutput(sensor.thermalLoad.getPower());
        }
    }

    void setOutput(double physical) {
        double U = (physical - sensor.lowValue) / (sensor.highValue - sensor.lowValue) * VoltageTier.TTL.getVoltage();
        if (U > VoltageTier.TTL.getVoltage()) U = VoltageTier.TTL.getVoltage();
        if (U < 0) U = 0;
        sensor.outputGateProcess.setU(U);
    }
}
