package org.ja13.eau.transparentnode.electricalantennarx;

import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.PowerSource;
import org.ja13.eau.sim.nbt.NbtElectricalGateInput;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.PowerSource;
import org.ja13.eau.sim.nbt.NbtElectricalGateInput;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;

import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ElectricalAntennaRxElement extends TransparentNodeElement {

    ElectricalAntennaRxSlowProcess slowProcess = new ElectricalAntennaRxSlowProcess(this);

    NbtElectricalLoad powerOut = new NbtElectricalLoad("powerOut");
    NbtElectricalGateInput signalIn = new NbtElectricalGateInput("signalIn");

    PowerSource powerSrc = new PowerSource("powerSrc", powerOut);

    LRDU rot = LRDU.Up;
    Coordonate rxCoord = null;
    ElectricalAntennaRxDescriptor descriptor;

    public double getSignal() {
        return signalIn.getVoltage();
    }

    public void setPowerOut(double power) {
        powerSrc.setP(power);
    }

    public void rxDisconnect() {
        powerSrc.setP(0.0);
    }

    public ElectricalAntennaRxElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
        slowProcessList.add(slowProcess);

        electricalLoadList.add(powerOut);
        electricalLoadList.add(signalIn);
        electricalComponentList.add(powerSrc);

        this.descriptor = (ElectricalAntennaRxDescriptor) descriptor;
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        if (front.getInverse() != side.applyLRDU(lrdu)) return null;

        if (side == front.applyLRDU(rot.left())) return powerOut;
        if (side == front.applyLRDU(rot.right())) return signalIn;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {
        if (front.getInverse() != side.applyLRDU(lrdu)) return 0;

        if (side == front.applyLRDU(rot.left())) return NodeBase.MASK_ELECTRIC;
        if (side == front.applyLRDU(rot.right())) return NodeBase.MASK_ELECTRIC;

        return 0;
    }

    @Override
    public String multiMeterString(Direction side) {
        return "";
    }

    @Override
    public String thermoMeterString(Direction side) {
        return "";
    }

    @Override
    public void initialize() {
        descriptor.cable.applyTo(powerOut);
        powerSrc.setUmax(descriptor.electricalMaximalVoltage * 2);
        powerSrc.setImax(descriptor.electricalMaximalVoltage * descriptor.electricalMaximalPower * 2);
        connect();
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        if (Utils.isPlayerUsingWrench(entityPlayer)) {
            rot = rot.getNextClockwise();
            node.reconnect();
            return true;
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        rot = LRDU.readFromNBT(nbt, "rot");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        rot.writeToNBT(nbt, "rot");
    }

    public boolean mustHaveFloor() {
        return false;
    }

    public boolean mustHaveCeiling() {
        return false;
    }

    public boolean mustHaveWall() {
        return false;
    }

    public boolean mustHaveWallFrontInverse() {
        return true;
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        rot.serialize(stream);
        node.lrduCubeMask.getTranslate(front.getInverse()).serialize(stream);
    }

    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Receiving"), powerSrc.getP() != 0 ? "Yes" : "No");
        if (EAU.wailaEasyMode) {
            info.put(I18N.tr("Power received"), Utils.plotPower(powerSrc.getP(), ""));
            info.put(I18N.tr("Effective power"), Utils.plotPower(powerSrc.getEffectiveP(), ""));
        }
        return info;
    }
}
