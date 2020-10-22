package org.ja13.eau.sixnode.electricalsensor;

import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.sim.IProcess;

public class ElectricalSensorProcess implements IProcess {

    ElectricalSensorElement sensor;

    public ElectricalSensorProcess(ElectricalSensorElement sensor) {
        this.sensor = sensor;
    }

    @Override
    public void process(double time) {
        if (sensor.typeOfSensor == ElectricalSensorElement.voltageType) {
            setOutput(sensor.aLoad.getU());
        } else if (sensor.typeOfSensor == ElectricalSensorElement.currantType) {
            double output = 0;
            switch (sensor.dirType) {
                case ElectricalSensorElement.dirNone:
                    output = Math.abs(sensor.resistor.getCurrent());
                    break;
                case ElectricalSensorElement.dirAB:
                    output = (sensor.resistor.getCurrent());
                    break;
                case ElectricalSensorElement.dirBA:
                    output = (-sensor.resistor.getCurrent());
                    break;
            }

            setOutput(output);
        } else if (sensor.typeOfSensor == ElectricalSensorElement.powerType) {
            double output = 0;
            switch (sensor.dirType) {
                case ElectricalSensorElement.dirNone:
                    output = Math.abs(sensor.resistor.getCurrent() * sensor.aLoad.getU());
                    break;
                case ElectricalSensorElement.dirAB:
                    output = (sensor.resistor.getCurrent() * sensor.aLoad.getU());
                    break;
                case ElectricalSensorElement.dirBA:
                    output = (-sensor.resistor.getCurrent() * sensor.aLoad.getU());
                    break;
            }

            setOutput(output);
        }
    }

    void setOutput(double physical) {
        double U = (physical - sensor.lowValue) / (sensor.highValue - sensor.lowValue) * VoltageTier.TTL.getVoltage();
        if (U > VoltageTier.TTL.getVoltage()) U = VoltageTier.TTL.getVoltage();
        if (U < 0) U = 0;
        sensor.outputGateProcess.setU(U);
    }
}
