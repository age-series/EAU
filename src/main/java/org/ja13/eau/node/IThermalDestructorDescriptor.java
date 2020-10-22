package org.ja13.eau.node;

public interface IThermalDestructorDescriptor {
    double getThermalDestructionMax();

    double getThermalDestructionStart();

    double getThermalDestructionPerOverflow();

    double getThermalDestructionProbabilityPerOverflow();
}
