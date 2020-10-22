package org.ja13.eau.sixnode.electricalgatesource;

import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.nbt.NbtElectricalGateOutputProcess;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sound.SoundCommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.nbt.NbtElectricalGateOutputProcess;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sound.SoundCommand;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ElectricalGateSourceElement extends SixNodeElement {

    public ElectricalGateSourceDescriptor descriptor;
    public NbtElectricalLoad outputGate = new NbtElectricalLoad("outputGate");

    public NbtElectricalGateOutputProcess outputGateProcess = new NbtElectricalGateOutputProcess("outputGateProcess", outputGate);

    public AutoResetProcess autoResetProcess;

    public static final byte setVoltagerId = 1;

    public ElectricalGateSourceElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);
        this.descriptor = (ElectricalGateSourceDescriptor) descriptor;

        electricalLoadList.add(outputGate);
        electricalComponentList.add(outputGateProcess);

        if (this.descriptor.autoReset) {
            slowProcessList.add(autoResetProcess = new AutoResetProcess());
            autoResetProcess.reset();
        }
    }

    class AutoResetProcess implements IProcess {
        double timeout = 0;
        double timeoutDelay = 0.21;

        @Override
        public void process(double time) {
            if (timeout > 0) {
                if (timeout - time < 0) {
                    outputGateProcess.setOutputNormalized(0);
                    needPublish();
                }
                timeout -= time;
            }
        }

        void reset() {
            timeout = timeoutDelay;
        }
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
        nbt.setByte("front", (byte) ((front.toInt() << 0)));
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        if (front == lrdu) return outputGate;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        if (front == lrdu) return NodeBase.MASK_ELECTRIC;
        return 0;
    }

    @Override
    public String multiMeterString() {
        return Utils.plotUIP(outputGate.getU(), outputGate.getCurrent());
    }

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        if (descriptor.onOffOnly && !descriptor.autoReset) {
            boolean isOn = outputGateProcess.getOutputOnOff();
            if (isOn) {
                info.put(I18N.tr("State"), "§a" + I18N.tr("On"));
            }else{
                info.put(I18N.tr("State"), "§c" + I18N.tr("Off"));
            }
        }
        info.put(I18N.tr("Output voltage"), Utils.plotVolt(outputGate.getU(), ""));
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
            stream.writeByte(front.toInt() << 4);
            stream.writeFloat((float) outputGateProcess.getU());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        EAU.smallInsulationLowCurrentCopperCable.applyTo(outputGate);
        computeElectricalLoad();
    }

    @Override
    protected void inventoryChanged() {
        computeElectricalLoad();
    }

    public void computeElectricalLoad() {
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        ItemStack currentItemStack = entityPlayer.getCurrentEquippedItem();

        if (onBlockActivatedRotate(entityPlayer)) {
            return true;
        } else if (!Utils.playerHasMeter(entityPlayer) && descriptor.onOffOnly) {
            outputGateProcess.state(!outputGateProcess.getOutputOnOff());
            play(new SoundCommand("random.click").mulVolume(0.3F, 0.6F).smallRange());
            if (autoResetProcess != null)
                autoResetProcess.reset();
            needPublish();
            return true;
        }
        // front = LRDU.fromInt((front.toInt() + 1)&3);
        return false;
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            switch (stream.readByte()) {
                case setVoltagerId:
                    outputGateProcess.setU(stream.readFloat());
                    needPublish();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasGui() {
        return !descriptor.onOffOnly;
    }
}
