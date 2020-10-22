package org.ja13.eau.sixnode.electricalrelay;

import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.IConfigurable;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.nbt.NbtElectricalGateInput;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.ja13.eau.sound.SoundCommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.IConfigurable;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.nbt.NbtElectricalGateInput;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.ja13.eau.sound.SoundCommand;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ElectricalRelayElement extends SixNodeElement implements IConfigurable {

    public ElectricalRelayDescriptor descriptor;
    public NbtElectricalLoad aLoad = new NbtElectricalLoad("aLoad");
    public NbtElectricalLoad bLoad = new NbtElectricalLoad("bLoad");
    public Resistor switchResistor = new Resistor(aLoad, bLoad);
    public NbtElectricalGateInput gate = new NbtElectricalGateInput("gate");
    public ElectricalRelayGateProcess gateProcess = new ElectricalRelayGateProcess(this, "GP", gate);

    VoltageStateWatchDog voltageWatchDogA = new VoltageStateWatchDog();
    VoltageStateWatchDog voltageWatchDogB = new VoltageStateWatchDog();
    //ResistorCurrentWatchdog currentWatchDog = new ResistorCurrentWatchdog();

    boolean switchState = false, defaultOutput = false;

    public GenericCableDescriptor cableDescriptor = null;

    public static final byte toogleOutputDefaultId = 3;

    public ElectricalRelayElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);

        this.descriptor = (ElectricalRelayDescriptor) descriptor;

        electricalLoadList.add(aLoad);
        electricalLoadList.add(bLoad);
        electricalComponentList.add(switchResistor);
        electricalProcessList.add(gateProcess);
        electricalLoadList.add(gate);

        electricalComponentList.add(new Resistor(bLoad, null).pullDown());
        electricalComponentList.add(new Resistor(aLoad, null).pullDown());

        //slowProcessList.add(currentWatchDog);
        slowProcessList.add(voltageWatchDogA);
        slowProcessList.add(voltageWatchDogB);

        WorldExplosion exp = new WorldExplosion(this).cableExplosion();

        //currentWatchDog.set(switchResistor).setIAbsMax(this.descriptor.cable.electricalMaximalCurrent).set(exp);
        voltageWatchDogA.set(aLoad).setUNominal(this.descriptor.cable.electricalNominalVoltage).set(exp);
        voltageWatchDogB.set(bLoad).setUNominal(this.descriptor.cable.electricalNominalVoltage).set(exp);
    }

    public static boolean canBePlacedOnSide(Direction side, int type) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        byte value = nbt.getByte("front");
        front = LRDU.fromInt((value >> 0) & 0x3);
        switchState = nbt.getBoolean("switchState");
        defaultOutput = nbt.getBoolean("defaultOutput");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("front", (byte) (front.toInt() << 0));
        nbt.setBoolean("switchState", switchState);
        nbt.setBoolean("defaultOutput", defaultOutput);
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        if (front.left() == lrdu) return aLoad;
        if (front.right() == lrdu) return bLoad;
        if (front == lrdu) return gate;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        if (front.left() == lrdu) return descriptor.cable.getNodeMask();
        if (front.right() == lrdu) return descriptor.cable.getNodeMask();
        if (front == lrdu) return NodeBase.MASK_ELECTRIC;
        return 0;
    }

    @Override
    public String multiMeterString() {
        return Utils.plotVolt(aLoad.getU(), "Ua:") + Utils.plotVolt(bLoad.getU(), "Ub:") + Utils.plotAmpere(aLoad.getCurrent(), "");
    }

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Position"), switchState ? I18N.tr("Closed") : I18N.tr("Open"));
        info.put(I18N.tr("Current"), Utils.plotAmpere(aLoad.getCurrent(), ""));
        if (EAU.wailaEasyMode) {
            info.put(I18N.tr("Default position"), defaultOutput ? I18N.tr("Closed") : I18N.tr("Open"));
            info.put(I18N.tr("Voltages"), Utils.plotVolt(aLoad.getU(), "") + Utils.plotVolt(bLoad.getU(), ""));
        }

        try {
            int subSystemSize = switchResistor.getSubSystem().component.size();
            String textColor = "";
            if (subSystemSize <= 8) {
                textColor = "§a";
            } else if (subSystemSize <= 15) {
                textColor = "§6";
            } else {
                textColor = "§c";
            }
            info.put(I18N.tr("Subsystem Matrix Size: "), textColor + subSystemSize);


        } catch (Exception e) {
            info.put(I18N.tr("Subsystem Matrix Size: "), "§cNot part of a subsystem!?");
        }

        return info;
    }

    @Override
    public String thermoMeterString() {
        return "";
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeBoolean(switchState);
            stream.writeBoolean(defaultOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSwitchState(boolean state) {
        if (state == switchState) return;
        switchState = state;
        refreshSwitchResistor();
        play(new SoundCommand("random.click").mulVolume(0.1F, 2.0F).smallRange());
        needPublish();
    }

    public void refreshSwitchResistor() {
        if (!switchState) {
            switchResistor.ultraImpedance();
        } else {
            descriptor.applyTo(switchResistor);
        }
    }

    public boolean getSwitchState() {
        return switchState;
    }

    @Override
    public void initialize() {
        computeElectricalLoad();

        setSwitchState(switchState);
        refreshSwitchResistor();
    }

    @Override
    protected void inventoryChanged() {
        computeElectricalLoad();
    }

    public void computeElectricalLoad() {
        descriptor.applyTo(aLoad);
        descriptor.applyTo(bLoad);
        refreshSwitchResistor();
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            switch (stream.readByte()) {
                case toogleOutputDefaultId:
                    defaultOutput = !defaultOutput;
                    needPublish();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void readConfigTool(NBTTagCompound compound, EntityPlayer invoker) {
        if(compound.hasKey("nc")) {
            defaultOutput = compound.getBoolean("nc");
        }
    }

    @Override
    public void writeConfigTool(NBTTagCompound compound, EntityPlayer invoker) {
        compound.setBoolean("nc", defaultOutput);
    }
}
