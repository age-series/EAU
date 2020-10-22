package org.ja13.eau.transparentnode.teleporter;

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

public class TeleporterPowerNode extends GhostNode {

    @Override
    public void initializeFromThat(Direction front,
                                   EntityLivingBase entityLiving, ItemStack itemStack) {
        connect();

    }

    @Override
    public int getSideConnectionMask(Direction directionA, LRDU lrduA) {
        if (e == null) return 0;
        if (directionA == Direction.YP || directionA == Direction.YN) return 0;
        if (lrduA != LRDU.Down) return 0;
        return MASK_ELECTRIC;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction directionA, LRDU lrduA, int mask) {

        return null;
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction directionB, LRDU lrduB, int mask) {
        if (e == null) return null;
        return e.powerLoad;
    }

    @Override
    public void initializeFromNBT() {


    }

    void setElement(TeleporterElement e) {
        this.e = e;
        //reconnect();
    }

    TeleporterElement e;
}
