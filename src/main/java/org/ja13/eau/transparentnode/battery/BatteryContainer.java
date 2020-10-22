package org.ja13.eau.transparentnode.battery;

import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.item.OverHeatingProtectionDescriptor;
import org.ja13.eau.item.OverVoltageProtectionDescriptor;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.INodeContainer;
import org.ja13.eau.node.NodeBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.OverHeatingProtectionDescriptor;
import org.ja13.eau.item.OverVoltageProtectionDescriptor;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.INodeContainer;
import org.ja13.eau.node.NodeBase;

import static org.ja13.eau.i18n.I18N.tr;

public class BatteryContainer extends BasicContainer implements INodeContainer {

    NodeBase node;

    public BatteryContainer(NodeBase node, EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
            new GenericItemUsingDamageSlot(inventory, 0, 130, 40, 1,
                OverVoltageProtectionDescriptor.class, ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Overvoltage protection")}),
            new GenericItemUsingDamageSlot(inventory, 1, 130, 60, 1,
                OverHeatingProtectionDescriptor.class, ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Overheating protection")}),
        });
        this.node = node;
    }

    @Override
    public NodeBase getNode() {
        return node;
    }

    @Override
    public int getRefreshRateDivider() {
        return 1;
    }
}
