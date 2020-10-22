package org.ja13.eau.solver;

public class ConstSymbole implements ISymbole {

    private final double value;
    private final String name;

    public ConstSymbole(String name, double value) {
        this.value = value;
        this.name = name;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }
}
