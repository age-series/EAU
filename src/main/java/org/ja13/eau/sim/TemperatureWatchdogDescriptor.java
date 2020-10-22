package org.ja13.eau.sim;

public interface TemperatureWatchdogDescriptor {
    double getUmax();

    double getUmin();

    double getBreakPropPerVoltOverflow();
}
