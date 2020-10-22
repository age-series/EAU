package org.ja13.eau.node;

public interface IVoltageDestructorDescriptor {
    double getVoltageDestructionMax();

    double getVoltageDestructionStart();

    double getVoltageDestructionPerOverflow();

    double getVoltageDestructionProbabilityPerOverflow();
}
