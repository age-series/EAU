package org.ja13.eau.transparentnode.thermaldissipatoractive;

import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.NodePeriodicPublishProcess;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog;
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.NodePeriodicPublishProcess;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog;
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ThermalDissipatorActiveElement extends TransparentNodeElement {
    ThermalDissipatorActiveDescriptor descriptor;
    NbtThermalLoad thermalLoad = new NbtThermalLoad("thermalLoad");
    NbtElectricalLoad positiveLoad = new NbtElectricalLoad("positiveLoad");
    ThermalDissipatorActiveSlowProcess slowProcess = new ThermalDissipatorActiveSlowProcess(this);
    Resistor powerResistor = new Resistor(positiveLoad, null);


    public ThermalDissipatorActiveElement(TransparentNode transparentNode,
                                          TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
        thermalLoadList.add(thermalLoad);
        electricalLoadList.add(positiveLoad);
        electricalComponentList.add(powerResistor);

        slowProcessList.add(slowProcess);
        this.descriptor = (ThermalDissipatorActiveDescriptor) descriptor;
        slowProcessList.add(new NodePeriodicPublishProcess(node, 4f, 2f));


        slowProcessList.add(thermalWatchdog);

        thermalWatchdog
            .set(thermalLoad)
            .setTMax(this.descriptor.warmLimit)
            .set(new WorldExplosion(this).machineExplosion());

        WorldExplosion exp = new WorldExplosion(this).machineExplosion();
        slowProcessList.add(voltageWatchdog.set(positiveLoad).setUNominal(this.descriptor.nominalElectricalU).set(exp));

    }

    VoltageStateWatchDog voltageWatchdog = new VoltageStateWatchDog();
    ThermalLoadWatchDog thermalWatchdog = new ThermalLoadWatchDog();

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        if (side == front || side == front.getInverse()) return positiveLoad;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {

        if (side == Direction.YN || side == Direction.YP || lrdu != LRDU.Down) return null;
        if (side == front || side == front.getInverse()) return null;
        return thermalLoad;
    }

    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {

        if (side == Direction.YN || side == Direction.YP || lrdu != LRDU.Down) return 0;
        if (side == front || side == front.getInverse()) return NodeBase.MASK_ELECTRIC;
        return NodeBase.MASK_THERMAL;
    }

    @Override
    public String multiMeterString(Direction side) {

        return Utils.plotVolt(positiveLoad.getU(), "") + Utils.plotAmpere(positiveLoad.getCurrent(), "");
    }

    @Override
    public String thermoMeterString(Direction side) {

        return Utils.plotCelsius(thermalLoad.Tc, "") + Utils.plotPower(thermalLoad.getPower(), "");
    }

    @Override
    public void initialize() {
        descriptor.applyTo(thermalLoad);
        descriptor.applyTo(positiveLoad, powerResistor);
        connect();
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side,
                                    float vx, float vy, float vz) {

        return false;
    }


    @Override
    public void networkSerialize(DataOutputStream stream) {

        super.networkSerialize(stream);
        try {
            stream.writeFloat(lastPowerFactor = (float) (powerResistor.getP() / descriptor.electricalNominalP));
        } catch (IOException e) {

            e.printStackTrace();
        }
        //Utils.println("DISIP");
    }

    public float lastPowerFactor;

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Temperature"), Utils.plotCelsius(thermalLoad.Tc, ""));
        if (EAU.wailaEasyMode) {
            info.put(I18N.tr("Thermal power"), Utils.plotPower(thermalLoad.getPower(), ""));
        }
        return info;
    }


}
