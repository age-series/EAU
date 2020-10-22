package org.ja13.eau.transparentnode.solarpanel;

import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.item.SolarTrackerDescriptor;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.INodeContainer;
import org.ja13.eau.node.NodeBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.SolarTrackerDescriptor;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.INodeContainer;
import org.ja13.eau.node.NodeBase;

import static org.ja13.eau.i18n.I18N.tr;

public class SolarPanelContainer extends BasicContainer implements INodeContainer {

    NodeBase node = null;
    static final int trackerSlotId = 0;

    public SolarPanelContainer(NodeBase node, EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
            new GenericItemUsingDamageSlot(inventory, trackerSlotId, 176 / 2 - 20, 35, 1,
                SolarTrackerDescriptor.class, ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Solar tracker slot")})

        });
        this.node = node;

    }

    @Override
    public NodeBase getNode() {
        return node;
    }

    @Override
    public int getRefreshRateDivider() {
        return 0;
    }
}
