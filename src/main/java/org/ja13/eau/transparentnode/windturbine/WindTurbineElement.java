package org.ja13.eau.transparentnode.windturbine;

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
import org.ja13.eau.sim.mna.component.PowerSource;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
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
import org.ja13.eau.sim.mna.component.PowerSource;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WindTurbineElement extends TransparentNodeElement {
    private final NbtElectricalLoad positiveLoad = new NbtElectricalLoad("positiveLoad");
    final PowerSource powerSource = new PowerSource("powerSource", positiveLoad);
    private final WindTurbineSlowProcess slowProcess = new WindTurbineSlowProcess("slowProcess", this);
    final WindTurbineDescriptor descriptor;
    private Direction cableFront = Direction.ZP;

    public WindTurbineElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);

        this.descriptor = (WindTurbineDescriptor) descriptor;

        electricalLoadList.add(positiveLoad);
        electricalComponentList.add(powerSource);
        slowProcessList.add(new NodePeriodicPublishProcess(transparentNode, 4, 4));
        slowProcessList.add(slowProcess);
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        if (lrdu != LRDU.Down) return null;
        if (side == cableFront.left()) return positiveLoad;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {
        if (lrdu != LRDU.Down) return 0;
        if (side == cableFront.left()) return NodeBase.MASK_ELECTRIC;
        if (side == cableFront.right() && !grounded) return NodeBase.MASK_ELECTRIC;
        return 0;
    }

    @Override
    public String multiMeterString(Direction side) {
        return null;
    }

    @Override
    public String thermoMeterString(Direction side) {
        return null;
    }

    @Override
    public void initialize() {
        setPhysicalValue();
        powerSource.setImax(descriptor.nominalPower * 5 / descriptor.maxVoltage);
        connect();
    }

    private void setPhysicalValue() {
        descriptor.cable.applyTo(positiveLoad);
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        if (Utils.isPlayerUsingWrench(entityPlayer)) {
            cableFront = cableFront.right();
            reconnect();
        }
        return false;
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeFloat((float) slowProcess.getWind());
            stream.writeFloat((float) (powerSource.getP() / descriptor.nominalPower));
            node.lrduCubeMask.getTranslate(Direction.YN).serialize(stream);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        cableFront.writeToNBT(nbt, "cableFront");
        Utils.println(cableFront);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        cableFront = Direction.readFromNBT(nbt, "cableFront");
        Utils.println(cableFront);
    }

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> wailaList = new HashMap<String, String>();
        wailaList.put(I18N.tr("Generating"), slowProcess.getWind() > 0 ? I18N.tr("Yes") : I18N.tr("No"));
        wailaList.put(I18N.tr("Produced power"), Utils.plotPower(powerSource.getEffectiveP(), ""));
        if (EAU.wailaEasyMode) {
            wailaList.put("Voltage", Utils.plotVolt(powerSource.getU(), ""));
        }
        return wailaList;
    }
}
