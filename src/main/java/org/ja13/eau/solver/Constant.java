package org.ja13.eau.solver;

public class Constant implements IValue {

    private final double value;

    Constant(double value) {
        this.value = value;
    }

    @Override
    public double getValue() {
        return value;
    }
}
