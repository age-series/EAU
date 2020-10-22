package org.ja13.eau.simplenode.energyconverter;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.node.simple.SimpleNode;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.nbt.NbtResistor;
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.node.simple.SimpleNode;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.nbt.NbtResistor;
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EnergyConverterElnToOtherNode extends SimpleNode {

    EnergyConverterElnToOtherDescriptor descriptor;

    NbtElectricalLoad load = new NbtElectricalLoad("load");
    NbtResistor powerInResistor = new NbtResistor("powerInResistor", load, null);
    ElectricalProcess electricalProcess = new ElectricalProcess();
    VoltageStateWatchDog watchdog = new VoltageStateWatchDog();

    public double energyBuffer = 0;
    public double energyBufferMax;
    public double inStdVoltage;
    public double inPowerMax;

    public double inPowerFactor = 0.5;

    public static final byte setInPowerFactor = 1;

    @Override
    protected void setDescriptorKey(String key) {
        super.setDescriptorKey(key);
        descriptor = (EnergyConverterElnToOtherDescriptor) getDescriptor();
    }

    @Override
    public int getSideConnectionMask(Direction directionA, LRDU lrduA) {
        if (directionA == getFront()) return MASK_ELECTRIC;
        return 0;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction directionA, LRDU lrduA, int mask) {
        return null;
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction directionB, LRDU lrduB, int mask) {
        return load;
    }

    @Override
    public void initialize() {
        electricalLoadList.add(load);
        electricalComponentList.add(powerInResistor);
        electricalProcessList.add(electricalProcess);
        slowProcessList.add(watchdog);

        EAU.applySmallRs(load);

        load.setAsPrivate();

        descriptor.applyTo(this);

        WorldExplosion exp = new WorldExplosion(this).machineExplosion();
        watchdog.set(load).setUNominal(inStdVoltage).set(exp);

        connect();
    }

    class ElectricalProcess implements IProcess {
        double timeout = 0;

        @Override
        public void process(double time) {
            energyBuffer += powerInResistor.getP() * time;
            timeout -= time;
            if (timeout < 0) {
                timeout = 0.05;
                double energyMiss = energyBufferMax - energyBuffer;
                if (energyMiss <= 0) {
                    powerInResistor.highImpedance();
                } else {
                    double factor = Math.min(1, energyMiss / energyBufferMax * 2);
                    if (factor < 0.005) factor = 0;
                    double inP = factor * inPowerMax * inPowerFactor;
                    powerInResistor.setR(inStdVoltage * inStdVoltage / inP);
                }
            }
        }
    }

    public double getOtherModEnergyBuffer(double conversionRatio) {
        return energyBuffer * conversionRatio;
    }

    public void drawEnergy(double otherModEnergy, double conversionRatio) {
        energyBuffer -= otherModEnergy / conversionRatio;
    }

    public double getOtherModOutMax(double otherOutMax, double conversionRatio) {
        return Math.min(getOtherModEnergyBuffer(conversionRatio), otherOutMax);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setDouble("energyBuffer", energyBuffer);
        nbt.setDouble("inPowerFactor", inPowerFactor);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        energyBuffer = nbt.getDouble("energyBuffer");
        inPowerFactor = nbt.getDouble("inPowerFactor");
    }

    @Override
    public boolean hasGui(Direction side) {
        return true;
    }

    @Override
    public void publishSerialize(DataOutputStream stream) {
        super.publishSerialize(stream);

        try {
            stream.writeFloat((float) inPowerFactor);
            stream.writeFloat((float) inPowerMax);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void networkUnserialize(DataInputStream stream, EntityPlayerMP player) {
        try {
            if (stream.readByte() == setInPowerFactor) {
                // TODO: This may be some bad news.
                inPowerFactor = stream.readFloat();
                needPublish();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNodeUuid() {
        return getNodeUuidStatic();
    }

    public static String getNodeUuidStatic() {
        return "ElnToOther";
    }
}
