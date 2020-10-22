package org.ja13.eau.transparentnode.autominer;

import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.node.GhostNode;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.node.GhostNode;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;

public class AutoMinerPowerNode extends GhostNode {
    private Direction front;

    private AutoMinerElement element;

    @Override
    public void initializeFromThat(Direction front, EntityLivingBase entityLiving, ItemStack itemStack) {
        this.front = front;

        connect();
    }

    @Override
    public int getSideConnectionMask(Direction directionA, LRDU lrduA) {
        if (element == null) return 0;
        if (directionA != front) return 0;
        if (lrduA != LRDU.Down) return 0;
        return MASK_ELECTRIC;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction directionA, LRDU lrduA, int mask) {
        return null;
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction directionB, LRDU lrduB, int mask) {
        if (element == null) return null;
        return element.inPowerLoad;
    }

    @Override
    public void initializeFromNBT() {
    }

    void setElement(AutoMinerElement e) {
        this.element = e;
    }

    public void writeToNBT(net.minecraft.nbt.NBTTagCompound nbt, String str) {
        front.writeToNBT(nbt, str + "front");
    }

    public void readFromNBT(net.minecraft.nbt.NBTTagCompound nbt, String str) {
        front = Direction.readFromNBT(nbt, str + "front");
    }
}
