package org.ja13.eau.transparentnode.solarpanel;

import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.SolarTrackerDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.GhostPowerNode;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.AutoAcceptInventoryProxy;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.sim.DiodeProcess;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.VoltageSource;
import org.ja13.eau.sim.mna.process.PowerSourceBipole;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.SolarTrackerDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.GhostPowerNode;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.AutoAcceptInventoryProxy;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.sim.DiodeProcess;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.VoltageSource;
import org.ja13.eau.sim.mna.process.PowerSourceBipole;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SolarPanelElement extends TransparentNodeElement {

    SolarPanelDescriptor descriptor;
    NbtElectricalLoad positiveLoad = new NbtElectricalLoad("positiveLoad");
    NbtElectricalLoad negativeLoad = new NbtElectricalLoad("negativeLoad");
    VoltageSource positiveSrc = new VoltageSource("posSrc", positiveLoad, null);
    VoltageSource negativeSrc = new VoltageSource("negSrc", negativeLoad, null);

    //ElectricalCurrentSource currentSource;
    DiodeProcess diode;
    PowerSourceBipole powerSource;

    SolarPannelSlowProcess slowProcess = new SolarPannelSlowProcess(this);

    public double panelAlpha = Math.PI / 2;
    private GhostPowerNode groundNode = null;

    public SolarPanelElement(TransparentNode transparentNode,
                             TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
        this.descriptor = (SolarPanelDescriptor) descriptor;

        grounded = false;

		/*if(this.descriptor.basicModel == false)
		{
			currentSource = new ElectricalCurrentSource(positiveLoad,negativeLoad);
			diode = new DiodeProcess(positiveLoad, negativeLoad);
			electricalProcessList.add(currentSource);
			electricalProcessList.add(diode);
		}
		else*/
        {
            powerSource = new PowerSourceBipole(positiveLoad, negativeLoad, positiveSrc, negativeSrc);


        }

        electricalLoadList.add(positiveLoad);
        electricalLoadList.add(negativeLoad);


        electricalComponentList.add(positiveSrc);
        electricalComponentList.add(negativeSrc);

        slowProcessList.add(slowProcess);
    }

    @Override
    public void connectJob() {
        EAU.simulator.mna.addProcess(powerSource);
        super.connectJob();
    }

    @Override
    public void disconnectJob() {
        super.disconnectJob();
        EAU.simulator.mna.removeProcess(powerSource);
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        if (lrdu != LRDU.Down) return null;
        if (groundNode == null) {
            // Single-tile solar panel.
            if (side == front.left()) return positiveLoad;
            if (side == front.right() && !grounded) return negativeLoad;
        } else {
            return positiveLoad;
        }
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {
        if (lrdu != LRDU.Down) return 0;
        if (groundNode == null) {
            // Single-tile solar panel.
            if (side == front.left()) return NodeBase.MASK_ELECTRIC;
            if (side == front.right() && !grounded) return NodeBase.MASK_ELECTRIC;
        } else {
            if (side == front) return NodeBase.MASK_ELECTRIC;
        }
        return 0;
    }

    @Override
    public String multiMeterString(Direction side) {
        return Utils.plotUIP(positiveLoad.getU() - negativeLoad.getU(), positiveLoad.getCurrent());
    }

    @Override
    public String thermoMeterString(Direction side) {
        return "";
    }

    @Override
    public void initialize() {
        powerSource.setUmax(this.descriptor.electricalUmax);
        powerSource.setImax(this.descriptor.electricalPmax / this.descriptor.electricalUmax * 1.5);

        descriptor.applyTo(positiveLoad);
        descriptor.applyTo(negativeLoad);

        if (descriptor.groundCoordinate != null) {
            GhostPowerNode n = new GhostPowerNode(node.coordonate, front, descriptor.groundCoordinate, negativeLoad);
            n.initialize();
            groundNode = n;
        }

        connect();
    }

    @Override
    public void onBreakElement() {
        super.onBreakElement();
        if (groundNode != null)
            groundNode.onBreakBlock();
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        return descriptor.canRotate && inventory.take(entityPlayer.getCurrentEquippedItem(), this, true, false);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        powerSource.writeToNBT(nbt, "powerSource");
        nbt.setDouble("panelAlpha", panelAlpha);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        powerSource.readFromNBT(nbt, "powerSource");
        panelAlpha = nbt.getDouble("panelAlpha");
    }


    public void networkSerialize(java.io.DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeBoolean(getInventory().getStackInSlot(SolarPanelContainer.trackerSlotId) != null);
            stream.writeFloat((float) panelAlpha);
            node.lrduCubeMask.getTranslate(Direction.YN).serialize(stream);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static final byte unserializePannelAlpha = 0;

    public byte networkUnserialize(DataInputStream stream) {

        byte packetType = super.networkUnserialize(stream);
        try {
            switch (packetType) {
                case unserializePannelAlpha:
                    panelAlpha = stream.readFloat();
                    needPublish();
                    break;

                default:
                    return packetType;
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        return unserializeNulldId;
    }

    private final AutoAcceptInventoryProxy inventory =
        (new AutoAcceptInventoryProxy(new TransparentNodeElementInventory(1, 64, this)))
            .acceptIfEmpty(0, SolarTrackerDescriptor.class);

    @Override
    public IInventory getInventory() {
        return inventory.getInventory();
    }

    @Override
    public boolean hasGui() {
        return descriptor.canRotate;
    }

    @Override
    public Container newContainer(Direction side, EntityPlayer player) {
        return new SolarPanelContainer(node, player, inventory.getInventory());
    }

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Sun angle"), Utils.plotValue(((slowProcess.getSolarAlpha()) * (180 / Math.PI)) - 90, "\u00B0", ""));
        info.put(I18N.tr("Panel angle"), Utils.plotValue((panelAlpha * (180 / Math.PI)) - 90, "\u00B0", ""));
        info.put(I18N.tr("Producing energy"), (slowProcess.getSolarLight() != 0 ? "Yes" : "No"));
        if (EAU.wailaEasyMode) {
            info.put(I18N.tr("Produced power"), Utils.plotPower(powerSource.getP(), ""));
        }
        return info;
    }

}
