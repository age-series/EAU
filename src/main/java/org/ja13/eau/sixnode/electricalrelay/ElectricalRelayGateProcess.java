package org.ja13.eau.sixnode.electricalrelay;

import org.ja13.eau.sim.NodeElectricalGateInputHysteresisProcess;
import org.ja13.eau.sim.nbt.NbtElectricalGateInput;
import org.ja13.eau.sim.NodeElectricalGateInputHysteresisProcess;
import org.ja13.eau.sim.nbt.NbtElectricalGateInput;

public class ElectricalRelayGateProcess extends NodeElectricalGateInputHysteresisProcess {

    ElectricalRelayElement element;

    public ElectricalRelayGateProcess(ElectricalRelayElement element, String name, NbtElectricalGateInput gate) {
        super(name, gate);
        this.element = element;
    }

    @Override
    protected void setOutput(boolean value) {
        element.setSwitchState(value ^ element.defaultOutput);
    }
}
