package org.ja13.eau.node.simple;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.DescriptorManager;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalConnection;
import org.ja13.eau.sim.mna.component.Component;
import org.ja13.eau.sim.mna.state.State;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.misc.DescriptorManager;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sim.ThermalConnection;
import org.ja13.eau.sim.mna.component.Component;
import org.ja13.eau.sim.mna.state.State;
import org.ja13.eau.sim.nbt.NbtThermalLoad;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public abstract class SimpleNode extends NodeBase {

    public EntityPlayerMP removedByPlayer;
    String descriptorKey = "";

    protected void setDescriptorKey(String key) {
        descriptorKey = key;
    }

    protected Object getDescriptor() {
        return DescriptorManager.get(descriptorKey);
    }

    private Direction front;

    public Direction getFront() {
        return front;
    }

    public void setFront(Direction front) {
        this.front = front;
        if (applayFrontToMetadata()) {
            coordonate.setMetadata(front.getInt());
        }
    }

    protected boolean applayFrontToMetadata() {
        return false;
    }

    @Override
    public void initializeFromThat(Direction front, EntityLivingBase entityLiving, ItemStack itemStack) {
        setFront(front);
        initialize();
    }

    @Override
    public void initializeFromNBT() {
        initialize();
    }

    public abstract void initialize();


    @Override
    public void publishSerialize(DataOutputStream stream) {
        super.publishSerialize(stream);
        try {
            stream.writeByte(front.getInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<IProcess> slowProcessList = new ArrayList<>(4);
    public ArrayList<IProcess> electricalProcessList = new ArrayList<>(4);
    public ArrayList<Component> electricalComponentList = new ArrayList<>(4);
    public ArrayList<State> electricalLoadList = new ArrayList<>(4);
    public ArrayList<IProcess> thermalFastProcessList = new ArrayList<>(4);
    public ArrayList<IProcess> thermalSlowProcessList = new ArrayList<>(4);
    public ArrayList<ThermalConnection> thermalConnectionList = new ArrayList<>(4);
    public ArrayList<NbtThermalLoad> thermalLoadList = new ArrayList<>(4);

    @Override
    public void connectJob() {
        super.connectJob();

        EAU.simulator.addAllSlowProcess(slowProcessList);

        EAU.simulator.addAllElectricalComponent(electricalComponentList);
        for (State load : electricalLoadList)
            EAU.simulator.addElectricalLoad(load);
        EAU.simulator.addAllElectricalProcess(electricalProcessList);

        EAU.simulator.addAllThermalConnection(thermalConnectionList);
        for (NbtThermalLoad load : thermalLoadList)
            EAU.simulator.addThermalLoad(load);
        EAU.simulator.addAllThermalFastProcess(thermalFastProcessList);
        EAU.simulator.addAllThermalSlowProcess(thermalSlowProcessList);
    }

    @Override
    public void disconnectJob() {
        super.disconnectJob();

        EAU.simulator.removeAllSlowProcess(slowProcessList);

        EAU.simulator.removeAllElectricalComponent(electricalComponentList);
        for (State load : electricalLoadList)
            EAU.simulator.removeElectricalLoad(load);
        EAU.simulator.removeAllElectricalProcess(electricalProcessList);

        EAU.simulator.removeAllThermalConnection(thermalConnectionList);
        for (NbtThermalLoad load : thermalLoadList)
            EAU.simulator.removeThermalLoad(load);
        EAU.simulator.removeAllThermalFastProcess(thermalFastProcessList);
        EAU.simulator.removeAllThermalSlowProcess(thermalSlowProcessList);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        front = Direction.readFromNBT(nbt, "SNfront");

        setDescriptorKey(nbt.getString("SNdescriptorKey"));

        for (State electricalLoad : electricalLoadList) {
            if (electricalLoad instanceof INBTTReady) ((INBTTReady) electricalLoad).readFromNBT(nbt, "");
        }

        for (NbtThermalLoad thermalLoad : thermalLoadList) {
            thermalLoad.readFromNBT(nbt, "");
        }

        for (Component c : electricalComponentList)
            if (c instanceof INBTTReady)
                ((INBTTReady) c).readFromNBT(nbt, "");

        for (IProcess process : slowProcessList) {
            if (process instanceof INBTTReady) ((INBTTReady) process).readFromNBT(nbt, "");
        }
        for (IProcess process : electricalProcessList) {
            if (process instanceof INBTTReady) ((INBTTReady) process).readFromNBT(nbt, "");
        }
        for (IProcess process : thermalFastProcessList) {
            if (process instanceof INBTTReady) ((INBTTReady) process).readFromNBT(nbt, "");
        }
        for (IProcess process : thermalSlowProcessList) {
            if (process instanceof INBTTReady) ((INBTTReady) process).readFromNBT(nbt, "");
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        front.writeToNBT(nbt, "SNfront");

        nbt.setString("SNdescriptorKey", descriptorKey == null ? "" : descriptorKey);

        for (State electricalLoad : electricalLoadList) {
            if (electricalLoad instanceof INBTTReady) ((INBTTReady) electricalLoad).writeToNBT(nbt, "");
        }

        for (NbtThermalLoad thermalLoad : thermalLoadList) {
            thermalLoad.writeToNBT(nbt, "");
        }

        for (Component c : electricalComponentList)
            if (c instanceof INBTTReady)
                ((INBTTReady) c).writeToNBT(nbt, "");

        for (IProcess process : slowProcessList) {
            if (process instanceof INBTTReady) ((INBTTReady) process).writeToNBT(nbt, "");
        }
        for (IProcess process : electricalProcessList) {
            if (process instanceof INBTTReady) ((INBTTReady) process).writeToNBT(nbt, "");
        }
        for (IProcess process : thermalFastProcessList) {
            if (process instanceof INBTTReady) ((INBTTReady) process).writeToNBT(nbt, "");
        }
        for (IProcess process : thermalSlowProcessList) {
            if (process instanceof INBTTReady) ((INBTTReady) process).writeToNBT(nbt, "");
        }

    }
}
