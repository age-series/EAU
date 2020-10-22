package org.ja13.eau.sim.nbt;

import org.ja13.eau.node.NodeBase;
import org.ja13.eau.sim.BatteryProcess;
import org.ja13.eau.sim.BatterySlowProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.BatteryProcess;
import org.ja13.eau.sim.BatterySlowProcess;
import org.ja13.eau.sim.ThermalLoad;

public class NbtBatterySlowProcess extends BatterySlowProcess {

    NodeBase node;
    float explosionRadius = 2;

    public NbtBatterySlowProcess(NodeBase node, BatteryProcess batteryProcess, ThermalLoad thermalLoad) {
        super(batteryProcess, thermalLoad);
        this.node = node;
    }

    @Override
    public void destroy() {
        node.physicalSelfDestruction(explosionRadius);
    }
}
