package org.ja13.eau.sixnode.electricalswitch;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.mna.component.ResistorSwitch;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import org.ja13.eau.sound.SoundCommand;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ElectricalSwitchElement extends SixNodeElement {

    public ElectricalSwitchDescriptor descriptor;
    public NbtElectricalLoad aLoad = new NbtElectricalLoad("aLoad");
    public NbtElectricalLoad bLoad = new NbtElectricalLoad("bLoad");
    public ResistorSwitch switchResistor = new ResistorSwitch("switchRes", aLoad, bLoad);

    VoltageStateWatchDog voltageWatchDogA = new VoltageStateWatchDog();
    VoltageStateWatchDog voltageWatchDogB = new VoltageStateWatchDog();

    boolean switchState = false;

    public ElectricalSwitchElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);

        switchResistor.mustUseUltraImpedance();
        electricalLoadList.add(aLoad);
        electricalLoadList.add(bLoad);
        electricalComponentList.add(switchResistor);
        electricalComponentList.add(new Resistor(bLoad, null).pullDown());
        electricalComponentList.add(new Resistor(aLoad, null).pullDown());

        this.descriptor = (ElectricalSwitchDescriptor) descriptor;

        WorldExplosion exp = new WorldExplosion(this).cableExplosion();

        slowProcessList.add(voltageWatchDogA);
        slowProcessList.add(voltageWatchDogB);

        voltageWatchDogA.set(aLoad).setUNominalMirror(this.descriptor.nominalVoltage).set(exp);
        voltageWatchDogB.set(bLoad).setUNominalMirror(this.descriptor.nominalVoltage).set(exp);
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
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("front", (byte) (front.toInt() << 0));
        nbt.setBoolean("switchState", switchState);
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        if (front == lrdu) return aLoad;
        if (front.inverse() == lrdu) return bLoad;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        if (front == lrdu) return descriptor.getNodeMask();
        if (front.inverse() == lrdu) return descriptor.getNodeMask();

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSwitchState(boolean state) {
        switchState = state;
        switchResistor.setState(state);
        needPublish();
    }

    @Override
    public void initialize() {
        descriptor.applyTo(aLoad);
        descriptor.applyTo(bLoad);
        switchResistor.setR(descriptor.electricalRs);
        setSwitchState(switchState);
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        if (onBlockActivatedRotate(entityPlayer)) return true;
        if (EAU.multiMeterElement.checkSameItemStack(entityPlayer.getCurrentEquippedItem())) {
            return false;
        }
        if (EAU.thermometerElement.checkSameItemStack(entityPlayer.getCurrentEquippedItem())) {
            return false;
        }
        if (EAU.allMeterElement.checkSameItemStack(entityPlayer.getCurrentEquippedItem())) {
            return false;
        } else {
            setSwitchState(!switchState);
            play(new SoundCommand("random.click").mulVolume(0.3F, 0.6f).smallRange());
            return true;
        }
    }
}
