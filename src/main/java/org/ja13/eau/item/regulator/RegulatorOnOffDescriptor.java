package org.ja13.eau.item.regulator;

import org.ja13.eau.sim.RegulatorProcess;
import org.ja13.eau.sim.RegulatorProcess;

import static org.ja13.eau.item.regulator.IRegulatorDescriptor.RegulatorType.OnOff;

public class RegulatorOnOffDescriptor extends IRegulatorDescriptor {

    private final double hysteresis;

    public RegulatorOnOffDescriptor(String name, String iconName, double hysteresis) {
        super(name);
        setDefaultIcon(iconName);
        this.hysteresis = hysteresis;
    }

    @Override
    public RegulatorType getType() {
        return RegulatorType.OnOff;
    }

    @Override
    public void applyTo(RegulatorProcess regulator, double workingPoint, double P, double I, double D) {
        regulator.setOnOff(hysteresis, workingPoint);
    }
}
