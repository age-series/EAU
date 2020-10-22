package org.ja13.eau.sixnode.thermalsensor;

import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.ConfigCopyToolDescriptor;
import org.ja13.eau.item.IConfigurable;
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
import org.ja13.eau.sim.nbt.NbtElectricalGateOutputProcess;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import org.ja13.eau.sixnode.electricaldatalogger.DataLogs;
import org.ja13.eau.sixnode.electriccable.ElectricCableDescriptor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.ja13.eau.sixnode.thermalcable.ThermalCableDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.ConfigCopyToolDescriptor;
import org.ja13.eau.item.IConfigurable;
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
import org.ja13.eau.sim.nbt.NbtElectricalGateOutputProcess;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import org.ja13.eau.sixnode.electriccable.ElectricCableDescriptor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.ja13.eau.sixnode.thermalcable.ThermalCableDescriptor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ThermalSensorElement extends SixNodeElement implements IConfigurable {

    public ThermalSensorDescriptor descriptor;
    public NbtThermalLoad thermalLoad = new NbtThermalLoad("thermalLoad");
    public NbtElectricalLoad outputGate = new NbtElectricalLoad("outputGate");

    public NbtElectricalGateOutputProcess outputGateProcess = new NbtElectricalGateOutputProcess("outputGateProcess", outputGate);
    public ThermalSensorProcess slowProcess = new ThermalSensorProcess(this);

    AutoAcceptInventoryProxy inventory;

    static final byte powerType = 0, temperatureType = 1;
    int typeOfSensor = temperatureType;
    float lowValue = 0, highValue = 50;

    public static final byte setTypeOfSensorId = 1;
    public static final byte setValueId = 2;

    public ThermalSensorElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);
        thermalLoadList.add(thermalLoad);
        electricalLoadList.add(outputGate);
        electricalComponentList.add(outputGateProcess);
        slowProcessList.add(slowProcess);

        this.descriptor = (ThermalSensorDescriptor) descriptor;

        if (this.descriptor.temperatureOnly) {
            inventory = (new AutoAcceptInventoryProxy(new SixNodeElementInventory(1, 64, this)))
                .acceptIfEmpty(0, ThermalCableDescriptor.class, GenericCableDescriptor.class);
        } else {
            inventory = (new AutoAcceptInventoryProxy(new SixNodeElementInventory(1, 64, this)))
                .acceptIfEmpty(0, ThermalCableDescriptor.class);
        }
    }

    public IInventory getInventory() {
        if (inventory != null)
            return inventory.getInventory();
        else
            return null;
    }

    public static boolean canBePlacedOnSide(Direction side, int type) {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        byte value = nbt.getByte("front");
        front = LRDU.fromInt((value >> 0) & 0x3);
        typeOfSensor = nbt.getByte("typeOfSensor");
        lowValue = nbt.getFloat("lowValue");
        highValue = nbt.getFloat("highValue");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("front", (byte) ((front.toInt() << 0)));
        nbt.setByte("typeOfSensor", (byte) typeOfSensor);
        nbt.setFloat("lowValue", lowValue);
        nbt.setFloat("highValue", highValue);
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        if (front == lrdu) return outputGate;

        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        if (!descriptor.temperatureOnly) {
            if (getInventory().getStackInSlot(ThermalSensorContainer.cableSlotId) != null) {
                if (front.left() == lrdu) return thermalLoad;
                if (front.right() == lrdu) return thermalLoad;
            }
        } else {
            if (front.inverse() == lrdu) return thermalLoad;
        }
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        if (!descriptor.temperatureOnly) {
            if (getInventory().getStackInSlot(ThermalSensorContainer.cableSlotId) != null) {
                if (front.left() == lrdu) return NodeBase.MASK_THERMAL;
                if (front.right() == lrdu) return NodeBase.MASK_THERMAL;
            }
            if (front == lrdu) return NodeBase.MASK_ELECTRIC;
        } else {
            if (isItemThermalCable()) {
                if (front.inverse() == lrdu) return NodeBase.MASK_THERMAL;
            } else if (isItemElectricalCable()) {
                if (front.inverse() == lrdu) return NodeBase.maskElectricalAll;
            }
            if (front == lrdu) return NodeBase.MASK_ELECTRIC;
        }
        return 0;
    }

    @Override
    public String multiMeterString() {
        return "";
    }

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Output voltage"), Utils.plotVolt(outputGate.getU(), ""));
        if (EAU.wailaEasyMode) {
            switch (typeOfSensor) {
                case temperatureType:
                    info.put(I18N.tr("Measured temperature"), Utils.plotCelsius(thermalLoad.getT(), ""));
                    break;

                case powerType:
                    info.put(I18N.tr("Measured thermal power"), Utils.plotPower(thermalLoad.getPower(), ""));
                    break;
            }
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
            stream.writeByte((front.toInt() << 4) + typeOfSensor);
            stream.writeFloat(lowValue);
            stream.writeFloat(highValue);
            Utils.serialiseItemStack(stream, getInventory().getStackInSlot(ThermalSensorContainer.cableSlotId));
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
        sixNode.disconnect();
        computeElectricalLoad();
        sixNode.connect();
    }

    public void computeElectricalLoad() {
        ItemStack cable = getInventory().getStackInSlot(ThermalSensorContainer.cableSlotId);

        SixNodeDescriptor descriptor = EAU.sixNodeItem.getDescriptor(cable);
        if (descriptor == null) return;
        if (descriptor.getClass() == ThermalCableDescriptor.class) {
            ThermalCableDescriptor cableDescriptor = (ThermalCableDescriptor) EAU.sixNodeItem.getDescriptor(cable);
            cableDescriptor.setThermalLoad(thermalLoad);
            thermalLoad.setAsFast();
        } else if (descriptor.getClass() == ElectricCableDescriptor.class) {
            GenericCableDescriptor cableDescriptor = (GenericCableDescriptor) EAU.sixNodeItem.getDescriptor(cable);
            cableDescriptor.applyTo(thermalLoad);
            thermalLoad.Rp = 1000000000.0;
            thermalLoad.setAsSlow();
        } else {
            thermalLoad.setHighImpedance();
        }
    }

    boolean isItemThermalCable() {
        SixNodeDescriptor descriptor = EAU.sixNodeItem.getDescriptor(getInventory().getStackInSlot(ThermalSensorContainer.cableSlotId));
        return descriptor != null && descriptor.getClass() == ThermalCableDescriptor.class;
    }

    boolean isItemElectricalCable() {
        SixNodeDescriptor descriptor = EAU.sixNodeItem.getDescriptor(getInventory().getStackInSlot(ThermalSensorContainer.cableSlotId));
        return descriptor != null && descriptor.getClass() == ElectricCableDescriptor.class;
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        if (onBlockActivatedRotate(entityPlayer)) return true;
        ItemStack currentItemStack = entityPlayer.getCurrentEquippedItem();

        if (EAU.multiMeterElement.checkSameItemStack(currentItemStack)) {
            return false;
        }
        if (EAU.thermometerElement.checkSameItemStack(currentItemStack)) {
            return false;
        }
        if (EAU.allMeterElement.checkSameItemStack(currentItemStack)) {
            return false;
        }
        return inventory.take(currentItemStack, this, false, true);
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            switch (stream.readByte()) {
                case setTypeOfSensorId:
                    typeOfSensor = stream.readByte();
                    needPublish();
                    break;
                case setValueId:
                    lowValue = stream.readFloat();
                    highValue = stream.readFloat();
                    if (lowValue == highValue) highValue += 0.0001;
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
    public Container newContainer(Direction side, EntityPlayer player) {
        return new ThermalSensorContainer(player, inventory.getInventory(), descriptor.temperatureOnly);
    }

    @Override
    public void readConfigTool(NBTTagCompound compound, EntityPlayer invoker) {
        if(compound.hasKey("min"))
            lowValue = compound.getFloat("min");
        if(compound.hasKey("max"))
            highValue = compound.getFloat("max");
        if(compound.hasKey("unit")) {
            switch(compound.getByte("unit")) {
                case DataLogs.powerType:
                    typeOfSensor = powerType;
                    break;
                case DataLogs.celsiusType:
                    typeOfSensor = temperatureType;
                    break;
            }
        }
        ConfigCopyToolDescriptor.readCableType(compound, getInventory(), 0, invoker);
        reconnect();
    }

    @Override
    public void writeConfigTool(NBTTagCompound compound, EntityPlayer invoker) {
        compound.setFloat("min", lowValue);
        compound.setFloat("max", highValue);
        switch(typeOfSensor) {
            case powerType:
                compound.setByte("unit", DataLogs.powerType);
                break;
            case temperatureType:
                compound.setByte("unit", DataLogs.celsiusType);
                break;
        }
        ConfigCopyToolDescriptor.writeCableType(compound, getInventory().getStackInSlot(0));
    }
}
