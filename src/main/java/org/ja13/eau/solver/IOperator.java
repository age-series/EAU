package org.ja13.eau.solver;

public interface IOperator extends IValue {

    void setOperator(IValue[] values);

    int getRedstoneCost();
}
