package org.ja13.eau.item.regulator;

import org.ja13.eau.item.GenericItemUsingDamageDescriptorUpgrade;
import org.ja13.eau.sim.RegulatorProcess;
import org.ja13.eau.item.GenericItemUsingDamageDescriptorUpgrade;
import org.ja13.eau.sim.RegulatorProcess;

public abstract class IRegulatorDescriptor extends GenericItemUsingDamageDescriptorUpgrade {

    public IRegulatorDescriptor(String name) {
        super(name);
    }

    public enum RegulatorType {Manual, None, OnOff, Analog}

    public abstract RegulatorType getType();

    public abstract void applyTo(RegulatorProcess regulator, double workingPoint, double P, double I, double D);
}
