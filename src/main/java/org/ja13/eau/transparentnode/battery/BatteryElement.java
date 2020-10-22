package org.ja13.eau.transparentnode.battery;

import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.OverHeatingProtectionDescriptor;
import org.ja13.eau.item.OverVoltageProtectionDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.AutoAcceptInventoryProxy;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.NodeVoltageState;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.mna.component.ResistorSwitch;
import org.ja13.eau.sim.mna.component.VoltageSource;
import org.ja13.eau.sim.nbt.NbtBatteryProcess;
import org.ja13.eau.sim.nbt.NbtBatterySlowProcess;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import org.ja13.eau.sim.process.heater.ElectricalLoadHeatThermalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.OverHeatingProtectionDescriptor;
import org.ja13.eau.item.OverVoltageProtectionDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.AutoAcceptInventoryProxy;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.NodeVoltageState;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.mna.component.ResistorSwitch;
import org.ja13.eau.sim.mna.component.VoltageSource;
import org.ja13.eau.sim.nbt.NbtBatteryProcess;
import org.ja13.eau.sim.nbt.NbtBatterySlowProcess;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import org.ja13.eau.sim.process.heater.ElectricalLoadHeatThermalLoad;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BatteryElement extends TransparentNodeElement {

    public NbtElectricalLoad cutLoad = new NbtElectricalLoad("cutLoad");
    public NodeVoltageState positiveLoad = new NodeVoltageState("positiveLoad");
    public NbtElectricalLoad negativeLoad = new NbtElectricalLoad("negativeLoad");
    public VoltageSource voltageSource = new VoltageSource("volSrc", positiveLoad, negativeLoad);

    public NbtThermalLoad thermalLoad = new NbtThermalLoad("thermalLoad");
    public ElectricalLoadHeatThermalLoad negativeETProcess = new ElectricalLoadHeatThermalLoad(negativeLoad, thermalLoad);
    public ThermalLoadWatchDog thermalWatchdog = new ThermalLoadWatchDog();

    public NbtBatteryProcess batteryProcess = new NbtBatteryProcess(positiveLoad, negativeLoad, null, 0, voltageSource, thermalLoad);

    public Resistor dischargeResistor = new Resistor(positiveLoad, negativeLoad);
    public ResistorSwitch cutSwitch = new ResistorSwitch("cutSwitch", cutLoad, positiveLoad);

    public BatteryInventoryProcess inventoryProcess = new BatteryInventoryProcess(this);

    double syncronizedPositiveUc, syncronizedNegativeUc, syncronizedCurrent, syncronizedTc;

    NbtBatterySlowProcess batterySlowProcess = new NbtBatterySlowProcess(node, batteryProcess, thermalLoad);

    AutoAcceptInventoryProxy inventory =
        (new AutoAcceptInventoryProxy(new TransparentNodeElementInventory(2, 64, this)))
            .acceptIfEmpty(0, OverVoltageProtectionDescriptor.class)
            .acceptIfEmpty(1, OverHeatingProtectionDescriptor.class);

    boolean fromNBT = false;

    public BatteryDescriptor descriptor;

    //static int UUIDCounter = 0;
    //int UUID = 0;
    boolean fromItemStack = false;
    double fromItemStack_charge;
    double fromItemStack_life;

    public BatteryElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
        this.descriptor = (BatteryDescriptor) descriptor;

        electricalLoadList.add(cutLoad);
        electricalLoadList.add(positiveLoad);
        electricalLoadList.add(negativeLoad);

        electricalComponentList.add(new Resistor(positiveLoad, null));
        electricalComponentList.add(new Resistor(negativeLoad, null));
        //electricalComponentList.add(new Resistor(cutLoad, null).setR(1000));
        //	electricalComponentList.add(new Resistor(positiveLoad, null).setR(1000));
        //electricalComponentList.add(new Resistor(negativeLoad, null).setR(1000));
        electricalComponentList.add(dischargeResistor);
        electricalComponentList.add(voltageSource);
        electricalComponentList.add(cutSwitch);
        thermalLoadList.add(thermalLoad);
        electricalProcessList.add(batteryProcess);
        thermalFastProcessList.add(negativeETProcess);

        slowProcessList.add(batterySlowProcess);
        slowProcessList.add(inventoryProcess);

        grounded = false;
        batteryProcess.setIMax(this.descriptor.IMax);

        slowProcessList.add(thermalWatchdog);

        thermalWatchdog
            .set(thermalLoad)
            .setTMax(this.descriptor.thermalWarmLimit)
            .set(new WorldExplosion(this).machineExplosion());
    }

    @Override
    public IInventory getInventory() {
        return inventory.getInventory();
    }

    public boolean hasOverVoltageProtection() {
        return getInventory().getStackInSlot(0) != null;
    }

    public boolean hasOverHeatingProtection() {
        return getInventory().getStackInSlot(1) != null;
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        if (lrdu != LRDU.Down) return null;
        if (side == front.left()) return cutLoad;
        if (side == front.right() && !grounded) return negativeLoad;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        if (lrdu != LRDU.Down) return null;
    /*	if(side == front.left()) return thermalLoad;
		if(side == front.right() && ! grounded) return thermalLoad;*/
        return null;
    }



    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {
        if (lrdu != LRDU.Down) return 0;
        if (side == front.left()) return NodeBase.MASK_ELECTRIC;
        if (side == front.right() && !grounded) return NodeBase.MASK_ELECTRIC;
        return 0;
    }

    @Override
    public String multiMeterString(Direction side) {
        String str = "";
        str += Utils.plotVolt(batteryProcess.getU(), "");
        str += Utils.plotAmpere(batteryProcess.getDischargeCurrent(), "");
        str += Utils.plotPercent(batteryProcess.getCharge(), "Charge:");
        // batteryProcess.life is a percentage from 1.0 to 0.0.
        str += Utils.plotPercent(batteryProcess.life, "Life:");
        return str;
    }

    @Override
    public String thermoMeterString(Direction side) {
        return Utils.plotCelsius(thermalLoad.Tc, "");
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            double U = batteryProcess.getU();//(positiveLoad.Uc - negativeLoad.Uc);
            stream.writeFloat((float) (U * batteryProcess.getDischargeCurrent()));
            stream.writeFloat((float) batteryProcess.getEnergy());
            stream.writeShort((short) (batteryProcess.life * 1000));

            node.lrduCubeMask.getTranslate(Direction.YN).serialize(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        initPhysicalValue();
        connect();
    }

    public void initPhysicalValue() {
        descriptor.applyTo(batteryProcess);
        descriptor.applyTo(thermalLoad);
        descriptor.applyTo(dischargeResistor);
        descriptor.applyTo(batterySlowProcess);
        cutSwitch.setR(descriptor.electricalRs / 2);
        cutLoad.setRs(descriptor.electricalRs / 2);
        negativeLoad.setRs(descriptor.electricalRs);
        if (fromItemStack) {
            batteryProcess.life = fromItemStack_life;
            batteryProcess.setCharge(fromItemStack_charge);
            fromItemStack = false;
        }
    }

    @Override
    public void inventoryChange(IInventory inventory) {
        //	initPhysicalValue();
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        return inventory.take(entityPlayer.getCurrentEquippedItem());
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        //inventory.writeToNBT(nbt, str + "inv");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        fromNBT = true;
        //inventory.readFromNBT(nbt, str + "inv");
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public Container newContainer(Direction side, EntityPlayer player) {
        return new BatteryContainer(this.node, player, getInventory());
    }

    @Override
    public void onGroundedChangedByClient() {
        super.onGroundedChangedByClient();

        disconnect();
        initPhysicalValue();
        reconnect();
    }

    @Override
    public void readItemStackNBT(NBTTagCompound nbt) {
        super.readItemStackNBT(nbt);

        if (nbt == null) nbt = descriptor.getDefaultNBT();
        fromItemStack_charge = nbt.getDouble("charge");
        fromItemStack_life = nbt.getDouble("life");

        fromItemStack = true;
    }

    @Override
    public NBTTagCompound getItemStackNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setDouble("charge", batteryProcess.getCharge());
        nbt.setDouble("life", batteryProcess.life);
        return nbt;
    }

	/*
	public static NBTTagCompound newItemStackNBT() {
		NBTTagCompound nbt = new NBTTagCompound("itemStackNBT");
		nbt.setDouble("charge", 0.5);
		nbt.setDouble("life", 1.0);
		return nbt;
	}*/

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Charge"), Utils.plotPercent(batteryProcess.getCharge(), ""));
        info.put(I18N.tr("Energy"), Utils.plotEnergy(batteryProcess.getEnergy(), ""));
        info.put(I18N.tr("Life"), Utils.plotPercent(batteryProcess.life, ""));
        if (EAU.wailaEasyMode) {
            info.put(I18N.tr("Voltage"), Utils.plotVolt(batteryProcess.getU(), ""));
            info.put(I18N.tr("Current"), Utils.plotAmpere(batteryProcess.getDischargeCurrent(), ""));
            info.put(I18N.tr("Temperature"), Utils.plotCelsius(thermalLoad.Tc, ""));
        }
        int subSystemSize = positiveLoad.getSubSystem().component.size();
        String textColor = "";
        if (subSystemSize <= 8) {
            textColor = "§a";
        } else if (subSystemSize <= 15) {
            textColor = "§6";
        } else {
            textColor = "§c";
        }
        info.put(I18N.tr("Subsystem Matrix Size"), textColor + subSystemSize);
        return info;
    }
}
