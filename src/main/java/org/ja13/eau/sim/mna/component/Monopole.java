package org.ja13.eau.sim.mna.component;

import org.ja13.eau.sim.mna.state.State;
import org.ja13.eau.sim.mna.state.VoltageState;
import org.ja13.eau.sim.mna.state.State;
import org.ja13.eau.sim.mna.state.VoltageState;

public abstract class Monopole extends Component {

    VoltageState pin;

    public Monopole connectTo(VoltageState pin) {
        this.pin = pin;
        if (pin != null) pin.add(this);
        return this;
    }

    @Override
    public State[] getConnectedStates() {
        return new State[]{pin};
    }
}
