package org.ja13.eau.transparentnode.heatfurnace;

import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.regulator.IRegulatorDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.NodePeriodicPublishProcess;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.nbt.NbtElectricalGateInput;
import org.ja13.eau.sim.nbt.NbtFurnaceProcess;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.regulator.IRegulatorDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.NodePeriodicPublishProcess;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.nbt.NbtElectricalGateInput;
import org.ja13.eau.sim.nbt.NbtFurnaceProcess;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* 5s/item @ 500 C
 */

public class HeatFurnaceElement extends TransparentNodeElement {

    public NbtElectricalGateInput electricalCmdLoad = new NbtElectricalGateInput("electricalCmdLoad");
    //public SignalRp electricalCmdRp = new SignalRp(electricalCmdLoad);
    public NbtThermalLoad thermalLoad = new NbtThermalLoad("thermalLoad");
    public NbtFurnaceProcess furnaceProcess = new NbtFurnaceProcess("furnaceProcess", thermalLoad);
    public HeatFurnaceInventoryProcess inventoryProcess = new HeatFurnaceInventoryProcess(this);

    TransparentNodeElementInventory inventory = new HeatFurnaceInventory(4, 64, this);

    HeatFurnaceThermalProcess regulator = new HeatFurnaceThermalProcess("regulator", furnaceProcess, this);

    HeatFurnaceDescriptor descriptor;

    ThermalLoadWatchDog thermalWatchdog = new ThermalLoadWatchDog();

    public static final byte unserializeGain = 1;
    public static final byte unserializeTemperatureTarget = 2;
    public static final byte unserializeToogleControlExternalId = 3;
    public static final byte unserializeToogleTakeFuelId = 4;

    public boolean controlExternal = false, takeFuel = false;

    public HeatFurnaceElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
        //this.descriptor.alphaClose = 0;

        this.descriptor = (HeatFurnaceDescriptor) descriptor;

        furnaceProcess.setGainMin(0.1);

        thermalLoadList.add(thermalLoad);
        thermalFastProcessList.add(furnaceProcess);
        slowProcessList.add(inventoryProcess);
        thermalFastProcessList.add(regulator);
        electricalLoadList.add(electricalCmdLoad);
        //electricalComponentList.add(electricalCmdRp);
        slowProcessList.add(new NodePeriodicPublishProcess(transparentNode, 2.0, 1.0));

        slowProcessList.add(thermalWatchdog);

        thermalWatchdog
            .set(thermalLoad)
            .setLimit(this.descriptor.thermal)
            .set(new WorldExplosion(this).machineExplosion());
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        return electricalCmdLoad;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        if (side == front.getInverse() && lrdu == LRDU.Down) return thermalLoad;
        return null;
    }

    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {
        if ((side == front.left() || side == front.right()) && lrdu == LRDU.Down)
            return NodeBase.MASK_ELECTRIC;
        if (side == front.getInverse() && lrdu == LRDU.Down) return NodeBase.MASK_THERMAL;
        return 0;
    }

    @Override
    public String multiMeterString(Direction side) {
        return "";
    }

    @Override
    public String thermoMeterString(Direction side) {
        return Utils.plotCelsius(thermalLoad.Tc, "");
    }

    @Override
    public void initialize() {
        descriptor.applyTo(thermalLoad);
        descriptor.applyTo(furnaceProcess);
        computeInventory();

        connect();

        inventoryProcess.process(1 / 20.0);
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        return false;
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeBoolean(getControlExternal());
            stream.writeBoolean(getTakeFuel());
            stream.writeShort((short) (thermalLoad.Tc * NodeBase.networkSerializeTFactor));
            stream.writeFloat((float) furnaceProcess.getGain());
            stream.writeFloat((float) regulator.getTarget());
            stream.writeShort((int) furnaceProcess.getP());

            serialiseItemStack(stream, inventory.getStackInSlot(HeatFurnaceContainer.combustibleId));
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
        return new HeatFurnaceContainer(node, player, inventory, descriptor);
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public byte networkUnserialize(DataInputStream stream) {
        byte packetType = super.networkUnserialize(stream);
        try {
            switch (packetType) {
                case unserializeGain:
                    if (inventory.getStackInSlot(HeatFurnaceContainer.regulatorId) == null) {
                        furnaceProcess.setGain(stream.readFloat());
                    }
                    needPublish();
                    break;
                case unserializeTemperatureTarget:
                    //if(inventory.getStackInSlot(HeatFurnaceContainer.regulatorId) == null)
                {
                    regulator.setTarget(stream.readFloat());
                }
                needPublish();
                break;
                case unserializeToogleControlExternalId:
                    regulator.setTarget(0);
                    setControlExternal(!getControlExternal());
                    break;
                case unserializeToogleTakeFuelId:
                    setTakeFuel(!getTakeFuel());
                    break;
                default:
                    return packetType;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return unserializeNulldId;
    }

    public boolean getControlExternal() {
        return controlExternal;
    }

    public void setControlExternal(boolean value) {
        if (value != controlExternal) needPublish();
        controlExternal = value;
        computeInventory();
    }

    public boolean getTakeFuel() {
        return takeFuel;
    }

    public void setTakeFuel(boolean value) {
        if (value != takeFuel) needPublish();
        takeFuel = value;
    }

    @Override
    public void inventoryChange(IInventory inventory) {
        super.inventoryChange(inventory);

        computeInventory();
        needPublish();
    }

    void computeInventory() {
        ItemStack regulatorStack = inventory.getStackInSlot(HeatFurnaceContainer.regulatorId);

        if (regulatorStack != null && !controlExternal) {
            IRegulatorDescriptor regulator = (IRegulatorDescriptor) Utils.getItemObject(regulatorStack);

            regulator.applyTo(this.regulator, 500.0, 10.0, 0.1, 0.1);
            //	furnace.regulator.target = 240;
        } else {
            regulator.setManual();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setBoolean("takeFuel", takeFuel);
        nbt.setBoolean("controlExternal", controlExternal);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        takeFuel = nbt.getBoolean("takeFuel");
        controlExternal = nbt.getBoolean("controlExternal");
    }

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Temperature"), Utils.plotCelsius(thermalLoad.Tc, ""));
        info.put(I18N.tr("Set temperature"), Utils.plotCelsius(regulator.getTarget(), ""));
        return info;
    }
}
