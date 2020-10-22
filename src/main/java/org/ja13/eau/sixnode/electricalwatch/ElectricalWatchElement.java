package org.ja13.eau.sixnode.electricalwatch;

import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.electricalitem.BatteryItem;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.AutoAcceptInventoryProxy;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.electricalitem.BatteryItem;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.AutoAcceptInventoryProxy;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ElectricalWatchElement extends SixNodeElement {

    ElectricalWatchDescriptor descriptor;

    public ElectricalWatchSlowProcess slowProcess = new ElectricalWatchSlowProcess(this);

    private final AutoAcceptInventoryProxy inventory = (new AutoAcceptInventoryProxy(new SixNodeElementInventory(1, 64, this)))
        .acceptIfEmpty(0, BatteryItem.class);

    public ElectricalWatchElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);

        slowProcessList.add(slowProcess);
        this.descriptor = (ElectricalWatchDescriptor) descriptor;
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        return 0;
    }

    @Override
    public String multiMeterString() {
        return "";
    }

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Battery level"), Utils.plotPercent(slowProcess.getBatteryLevel(), ""));
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
        return inventory.take(entityPlayer.getCurrentEquippedItem(), this, true, false);
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public IInventory getInventory() {
        if (inventory != null)
            return inventory.getInventory();
        else
            return null;
    }

    @Override
    public Container newContainer(Direction side, EntityPlayer player) {
        return new ElectricalWatchContainer(player, inventory.getInventory());
    }

    @Override
    protected void inventoryChanged() {
        super.inventoryChanged();
        needPublish();
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeBoolean(slowProcess.upToDate);
            stream.writeLong(slowProcess.oldDate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
