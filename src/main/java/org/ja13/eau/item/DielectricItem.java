package org.ja13.eau.item;

import org.ja13.eau.generic.GenericItemUsingDamageDescriptor;

public class DielectricItem extends GenericItemUsingDamageDescriptor {

    public double uNominal;

    public DielectricItem(String name, double uNominal) {
        super(name);
        this.uNominal = uNominal;
    }
}
