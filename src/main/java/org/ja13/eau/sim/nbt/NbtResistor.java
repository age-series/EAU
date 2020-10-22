package org.ja13.eau.sim.nbt;

import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.mna.state.State;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.mna.state.State;

public class NbtResistor extends Resistor implements INBTTReady {

    String name;

    public NbtResistor(String name, State aPin, State bPin) {
        super(aPin, bPin);
        this.name = name;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        name += str;
        setR(nbt.getDouble(str + "R"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
        name += str;
        nbt.setDouble(str + "R", getR());
    }
}
