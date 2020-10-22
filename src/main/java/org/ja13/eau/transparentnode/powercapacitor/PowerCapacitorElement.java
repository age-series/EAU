package org.ja13.eau.transparentnode.powercapacitor;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Capacitor;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.process.destruct.BipoleVoltageWatchdog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Capacitor;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.process.destruct.BipoleVoltageWatchdog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;

import java.io.DataInputStream;

public class PowerCapacitorElement extends TransparentNodeElement {

    PowerCapacitorDescriptor descriptor;
    NbtElectricalLoad positiveLoad = new NbtElectricalLoad("positiveLoad");
    NbtElectricalLoad negativeLoad = new NbtElectricalLoad("negativeLoad");

    Capacitor capacitor = new Capacitor(positiveLoad, negativeLoad);
    Resistor dischargeResistor = new Resistor(positiveLoad, negativeLoad);
    PunkProcess punkProcess = new PunkProcess();
    BipoleVoltageWatchdog watchdog = new BipoleVoltageWatchdog().set(capacitor);

    public PowerCapacitorElement(TransparentNode transparentNode,
                                 TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
        this.descriptor = (PowerCapacitorDescriptor) descriptor;

        electricalLoadList.add(positiveLoad);
        electricalLoadList.add(negativeLoad);
        electricalComponentList.add(capacitor);
        electricalComponentList.add(dischargeResistor);
        electricalProcessList.add(punkProcess);
        slowProcessList.add(watchdog);

        watchdog.set(new WorldExplosion(this).machineExplosion());
        positiveLoad.setAsMustBeFarFromInterSystem();
    }


    class PunkProcess implements IProcess {
        double eLeft = 0;
        double eLegaliseResistor;

        @Override
        public void process(double time) {
            if (eLeft <= 0) {
                eLeft = 0;
                dischargeResistor.setR(stdDischargeResistor);
            } else {
                eLeft -= dischargeResistor.getP() * time;
                dischargeResistor.setR(eLegaliseResistor);
            }
        }
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        if (lrdu != LRDU.Down) return null;
        if (side == front.left()) return positiveLoad;
        if (side == front.right()) return negativeLoad;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {
        if (lrdu != LRDU.Down) return 0;
        if (side == front.left()) return NodeBase.MASK_ELECTRIC;
        if (side == front.right()) return NodeBase.MASK_ELECTRIC;
        return 0;
    }

    @Override
    public String multiMeterString(Direction side) {
        return Utils.plotAmpere(capacitor.getCurrent(), "");
    }

    @Override
    public String thermoMeterString(Direction side) {
        return null;
    }

    @Override
    public void initialize() {
        EAU.applySmallRs(positiveLoad);
        EAU.applySmallRs(negativeLoad);

        setupPhysical();


        connect();
    }

    @Override
    public void inventoryChange(IInventory inventory) {
        super.inventoryChange(inventory);
        setupPhysical();
    }

    double stdDischargeResistor;

    boolean fromNbt = false;

    public void setupPhysical() {
        double eOld = capacitor.getE();
        capacitor.setC(descriptor.getCValue(inventory));
        stdDischargeResistor = descriptor.dischargeTao / capacitor.getC();

        watchdog.setUNominal(descriptor.getUNominalValue(inventory));
        punkProcess.eLegaliseResistor = Math.pow(descriptor.getUNominalValue(inventory), 2) / 400;

        if (fromNbt) {
            dischargeResistor.setR(stdDischargeResistor);
            fromNbt = false;
        } else {
            double deltaE = capacitor.getE() - eOld;
            punkProcess.eLeft += deltaE;
            if (deltaE < 0) {
                dischargeResistor.setR(stdDischargeResistor);
            } else {
                dischargeResistor.setR(punkProcess.eLegaliseResistor);
            }
        }
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side,
                                    float vx, float vy, float vz) {

        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setDouble("punkELeft", punkProcess.eLeft);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        punkProcess.eLeft = nbt.getDouble("punkELeft");
        if (Double.isNaN(punkProcess.eLeft)) punkProcess.eLeft = 0;
        fromNbt = true;
    }

    public void networkSerialize(java.io.DataOutputStream stream) {
        super.networkSerialize(stream);
        /*
		 * try {
		 * 
		 * 
		 * } catch (IOException e) {
		 * 
		 * e.printStackTrace(); }
		 */
    }

    public static final byte unserializePannelAlpha = 0;

    public byte networkUnserialize(DataInputStream stream) {

        byte packetType = super.networkUnserialize(stream);
		/*
		 * try { switch(packetType) {
		 * 
		 * 
		 * default: return packetType; } } catch (IOException e) {
		 * 
		 * e.printStackTrace(); }
		 */
        return unserializeNulldId;
    }

    TransparentNodeElementInventory inventory = new TransparentNodeElementInventory(2, 64, this);

    @Override
    public IInventory getInventory() {

        return inventory;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public Container newContainer(Direction side, EntityPlayer player) {
        return new PowerCapacitorContainer(player, inventory);
    }

}
