package org.ja13.eau.sim.mna.component;

import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.sim.mna.misc.MnaConst;
import org.ja13.eau.sim.mna.state.State;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.sim.mna.misc.MnaConst;
import org.ja13.eau.sim.mna.state.State;

public class ResistorSwitch extends Resistor implements INBTTReady {

    boolean ultraImpedance = false;
    String name;

    boolean state = false;

    protected double baseR = 1;

    public ResistorSwitch(String name, State aPin, State bPin) {
        super(aPin, bPin);
        this.name = name;
    }

    public void setState(boolean state) {
        this.state = state;
        setR(baseR);
    }

    @Override
    public Resistor setR(double r) {
        baseR = r;
        return super.setR(state ? r : (ultraImpedance ? MnaConst.ultraImpedance : MnaConst.highImpedance));
    }

    public boolean getState() {
        return state;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        str += name;
        setR(nbt.getDouble(str + "R"));
        if (Double.isNaN(baseR) || baseR == 0) {
            if (ultraImpedance) ultraImpedance();
            else highImpedance();
        }
        setState(nbt.getBoolean(str + "State"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
        str += name;
        nbt.setDouble(str + "R", baseR);
        nbt.setBoolean(str + "State", getState());
    }

    public void mustUseUltraImpedance() {
        ultraImpedance = true;
    }
}
