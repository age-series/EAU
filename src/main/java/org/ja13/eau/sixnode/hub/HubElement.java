package org.ja13.eau.sixnode.hub;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Component;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Component;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class HubElement extends SixNodeElement {

    NbtElectricalLoad[] electricalLoad = new NbtElectricalLoad[4];
    boolean[] connectionGrid = new boolean[]{false, false, false, false, true, true};

    SixNodeElementInventory inventory = new SixNodeElementInventory(4, 64, this);

    public static final byte clientConnectionGridToggle = 1;

    public HubElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);

        for (int idx = 0; idx < 4; idx++) {
            electricalLoad[idx] = new NbtElectricalLoad("electricalLoad" + idx);
            electricalLoadList.add(electricalLoad[idx]);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        for (int idx = 0; idx < 6; idx++) {
            connectionGrid[idx] = nbt.getBoolean("connectionGrid" + idx);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        for (int idx = 0; idx < 6; idx++) {
            nbt.setBoolean("connectionGrid" + idx, connectionGrid[idx]);
        }
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        if (inventory.getStackInSlot(HubContainer.cableSlotId + lrdu.toInt()) != null)
            return electricalLoad[lrdu.toInt()];
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        if (getElectricalLoad(lrdu, 0) != null)
            return NodeBase.maskElectricalAll;

        return 0;
    }

    @Override
    public String multiMeterString() {
        return "";// Utils.plotVolt("U:", electricalLoad.Uc) +
        // Utils.plotAmpere("I:", electricalLoad.getCurrent());
    }

    @Override
    public Map<String, String> getWaila() {
        return null;
    }

    @Override
    public String thermoMeterString() {
        return "";
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            for (int idx = 0; idx < 4; idx++) {
                Utils.serialiseItemStack(stream, inventory.getStackInSlot(HubContainer.cableSlotId + idx));
            }

            for (int idx = 0; idx < 6; idx++) {
                stream.writeBoolean(connectionGrid[idx]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        setup();
        for (int idx = 0; idx < 4; idx++) {
            EAU.applySmallRs(electricalLoad[idx]);
        }
    }

    @Override
    protected void inventoryChanged() {
        super.inventoryChanged();
        sixNode.disconnect();
        setup();
        sixNode.connect();
    }

    void setup() {
        slowProcessList.clear();
        WorldExplosion exp = new WorldExplosion(this);
        exp.cableExplosion();

        for (Component c : electricalComponentList) {
            Resistor r = (Resistor) c;
            r.breakConnection();
        }

        electricalComponentList.clear();

        for (LRDU lrdu : LRDU.values()) {
            GenericCableDescriptor d = getCableDescriptorFromLrdu(lrdu);
            if (d == null) continue;

            VoltageStateWatchDog watchdog = new VoltageStateWatchDog();
            slowProcessList.add(watchdog);
            watchdog
                .setUNominal(d.electricalNominalVoltage)
                .set(electricalLoad[lrdu.toInt()])
                .set(exp);
        }

        for (int idx = 0; idx < 6; idx++) {
            if (connectionGrid[idx]) {
                LRDU[] lrdu = connectionIdToSide(idx);

                if (inventory.getStackInSlot(HubContainer.cableSlotId + lrdu[0].toInt()) != null && inventory.getStackInSlot(HubContainer.cableSlotId + lrdu[1].toInt()) != null) {
                    Resistor r = new Resistor(electricalLoad[lrdu[0].toInt()], electricalLoad[lrdu[1].toInt()]);
                    r.setR(getCableDescriptorFromLrdu(lrdu[0]).electricalRs + getCableDescriptorFromLrdu(lrdu[1]).electricalRs);
                    electricalComponentList.add(r);

                    //ResistorCurrentWatchdog watchdog = new ResistorCurrentWatchdog();
                    //slowProcessList.add(watchdog);
                    /*watchdog
						.set(r)
						.setIAbsMax(Math.min(getCableDescriptorFromLrdu(lrdu[0]).electricalMaximalCurrent, getCableDescriptorFromLrdu(lrdu[1]).electricalMaximalCurrent))
						.set(exp);*/
                }
            }
        }
    }

    GenericCableDescriptor getCableDescriptorFromLrdu(LRDU lrdu) {
        GenericCableDescriptor cableDescriptor;
        ItemStack cable;
        cable = inventory.getStackInSlot(HubContainer.cableSlotId + lrdu.toInt());
        cableDescriptor = (GenericCableDescriptor) EAU.sixNodeItem.getDescriptor(cable);
        return cableDescriptor;
    }

    static LRDU[] connectionIdToSide(int id) {
        switch (id) {
            case 0:
                return new LRDU[]{LRDU.Left, LRDU.Down};
            case 1:
                return new LRDU[]{LRDU.Right, LRDU.Up};
            case 2:
                return new LRDU[]{LRDU.Down, LRDU.Right};
            case 3:
                return new LRDU[]{LRDU.Up, LRDU.Left};
            case 4:
                return new LRDU[]{LRDU.Left, LRDU.Right};
            case 5:
                return new LRDU[]{LRDU.Down, LRDU.Up};
        }

        return null;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public Container newContainer(Direction side, EntityPlayer player) {
        return new HubContainer(player, inventory);
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        return false;
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            switch (stream.readByte()) {
                case clientConnectionGridToggle:
                    int id = stream.readByte();
                    connectionGrid[id] = !connectionGrid[id];
                    sixNode.disconnect();
                    setup();
                    sixNode.connect();
                    needPublish();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
