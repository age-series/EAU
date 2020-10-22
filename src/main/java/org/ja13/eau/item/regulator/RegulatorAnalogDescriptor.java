package org.ja13.eau.item.regulator;

import org.ja13.eau.sim.RegulatorProcess;
import org.ja13.eau.sim.RegulatorProcess;

import static org.ja13.eau.item.regulator.IRegulatorDescriptor.RegulatorType.Analog;

public class RegulatorAnalogDescriptor extends IRegulatorDescriptor {

    public RegulatorAnalogDescriptor(String name, String iconName) {
        super(name);
        setDefaultIcon(iconName);
    }

    @Override
    public RegulatorType getType() {
        return RegulatorType.Analog;
    }

    @Override
    public void applyTo(RegulatorProcess regulator, double workingPoint, double P, double I, double D) {
        regulator.setAnalog(P, I, D, workingPoint);
    }
}
