package org.ja13.eau.sixnode.diode;

import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.sim.DiodeProcess;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.ResistorSwitch;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import org.ja13.eau.sim.process.heater.DiodeHeatThermalLoad;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.sim.DiodeProcess;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.ResistorSwitch;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import org.ja13.eau.sim.process.heater.DiodeHeatThermalLoad;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiodeElement extends SixNodeElement {

    public DiodeDescriptor descriptor;
    public NbtElectricalLoad anodeLoad = new NbtElectricalLoad("anodeLoad");
    public NbtElectricalLoad catodeLoad = new NbtElectricalLoad("catodeLoad");
    public ResistorSwitch resistorSwitch = new ResistorSwitch("resistorSwitch", anodeLoad, catodeLoad);
    public NbtThermalLoad thermalLoad = new NbtThermalLoad("thermalLoad");
    public DiodeHeatThermalLoad heater = new DiodeHeatThermalLoad(resistorSwitch, thermalLoad);
    public ThermalLoadWatchDog thermalWatchdog = new ThermalLoadWatchDog();
    public DiodeProcess diodeProcess = new DiodeProcess(resistorSwitch);

    public DiodeElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);

        this.descriptor = (DiodeDescriptor) descriptor;
        thermalLoad.setAsSlow();

        electricalLoadList.add(anodeLoad);
        electricalLoadList.add(catodeLoad);
        thermalLoadList.add(thermalLoad);
        electricalComponentList.add(resistorSwitch);
        electricalProcessList.add(diodeProcess);
        slowProcessList.add(thermalWatchdog.set(thermalLoad).set(this.descriptor.thermal).set(new WorldExplosion(this).cableExplosion()));
        thermalSlowProcessList.add(heater);
    }

    public static boolean canBePlacedOnSide(Direction side, int type) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        byte value = nbt.getByte("front");
        front = LRDU.fromInt((value >> 0) & 0x3);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("front", (byte) (front.toInt() << 0));
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        if (front == lrdu) return anodeLoad;
        if (front.inverse() == lrdu) return catodeLoad;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return thermalLoad;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        if (front == lrdu) return descriptor.cable.getNodeMask();
        if (front.inverse() == lrdu) return descriptor.cable.getNodeMask();
        return 0;
    }

    @Override
    public String multiMeterString() {
        return Utils.plotVolt(anodeLoad.getU(), "U+:") + Utils.plotVolt(catodeLoad.getU(), "U-:") + Utils.plotAmpere(anodeLoad.getCurrent(), "");
    }

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Current"), Utils.plotAmpere(anodeLoad.getCurrent(), ""));
        if (EAU.wailaEasyMode) {
            info.put(I18N.tr("Forward Voltage"), Utils.plotVolt(anodeLoad.getU() - catodeLoad.getU(), ""));
            info.put(I18N.tr("Temperature"), Utils.plotCelsius(thermalLoad.getT(), ""));
        }
        return info;
    }

    @Override
    public String thermoMeterString() {
        return Utils.plotCelsius(thermalLoad.Tc, "");
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeByte(front.toInt() << 4);
            stream.writeShort((short) ((anodeLoad.getU()) * NodeBase.networkSerializeUFactor));
            stream.writeShort((short) ((catodeLoad.getU()) * NodeBase.networkSerializeUFactor));
            stream.writeShort((short) (anodeLoad.getCurrent() * NodeBase.networkSerializeIFactor));
            stream.writeShort((short) (thermalLoad.Tc * NodeBase.networkSerializeTFactor));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        descriptor.applyTo(catodeLoad);
        descriptor.applyTo(anodeLoad);
        descriptor.applyTo(thermalLoad);
        descriptor.applyTo(resistorSwitch);
    }
}
