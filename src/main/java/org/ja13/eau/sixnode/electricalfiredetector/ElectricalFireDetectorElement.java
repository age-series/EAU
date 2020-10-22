package org.ja13.eau.sixnode.electricalfiredetector;

import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.electricalitem.BatteryItem;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.AutoAcceptInventoryProxy;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.nbt.NbtElectricalGateOutput;
import org.ja13.eau.sim.nbt.NbtElectricalGateOutputProcess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.electricalitem.BatteryItem;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.AutoAcceptInventoryProxy;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.nbt.NbtElectricalGateOutput;
import org.ja13.eau.sim.nbt.NbtElectricalGateOutputProcess;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalFireDetectorElement extends SixNodeElement {

    ElectricalFireDetectorDescriptor descriptor;

    public NbtElectricalGateOutput outputGate;
    public NbtElectricalGateOutputProcess outputGateProcess;
    public ElectricalFireDetectorSlowProcess slowProcess;

    public boolean powered;
    public boolean firePresent = false;

    private AutoAcceptInventoryProxy inventory;

    public ElectricalFireDetectorElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);

        this.descriptor = (ElectricalFireDetectorDescriptor) descriptor;

        slowProcess = new ElectricalFireDetectorSlowProcess(this);

        if (!this.descriptor.batteryPowered) {
            powered = true;
            outputGate = new NbtElectricalGateOutput("outputGate");
            outputGateProcess = new NbtElectricalGateOutputProcess("outputGateProcess", outputGate);
            electricalLoadList.add(outputGate);
            electricalComponentList.add(outputGateProcess);
        } else {
            powered = false;
            inventory = new AutoAcceptInventoryProxy(new SixNodeElementInventory(1, 64, this))
                .acceptIfEmpty(0, BatteryItem.class);
        }

        slowProcessList.add(slowProcess);
    }

    public static boolean canBePlacedOnSide(Direction side, int type) {
        return true;
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        if (!descriptor.batteryPowered && front == lrdu.left()) return outputGate;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        if (!descriptor.batteryPowered && front == lrdu.left()) return NodeBase.MASK_ELECTRIC;
        return 0;
    }

    @Override
    public String multiMeterString() {
        if (descriptor.batteryPowered) {
            return I18N.tr("Fire detected: ") + firePresent;
        } else {
            return Utils.plotVolt(outputGate.getU(), "") + Utils.plotAmpere(outputGate.getCurrent(), "");
        }
    }

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Fire present"), firePresent ? I18N.tr("Yes") : I18N.tr("No"));
        if (EAU.wailaEasyMode && !descriptor.batteryPowered) {
            info.put(I18N.tr("Output voltage"), Utils.plotVolt(outputGate.getU(), ""));
        }
        if (descriptor.batteryPowered) {
            info.put(I18N.tr("Battery level"), Utils.plotPercent(slowProcess.getBatteryLevel(), ""));
        }
        return info;
    }

    @Override
    public String thermoMeterString() {
        return "";
    }

    @Override
    public void initialize() {
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        if (onBlockActivatedRotate(entityPlayer)) return true;

        return inventory != null && inventory.take(entityPlayer.getCurrentEquippedItem(), this, false, true);
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeBoolean(powered);
            stream.writeBoolean(firePresent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasGui() {
        return descriptor.batteryPowered;
    }

    @Override
    public IInventory getInventory() {
        if (inventory != null)
            return inventory.getInventory();
        else
            return null;
    }

    @Override
    protected void inventoryChanged() {
        super.inventoryChanged();
        needPublish();
    }

    @Override
    public Container newContainer(Direction side, EntityPlayer player) {
        return new ElectricalFireDetectorContainer(player, inventory.getInventory());
    }
}
