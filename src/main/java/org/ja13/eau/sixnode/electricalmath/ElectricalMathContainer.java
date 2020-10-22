package org.ja13.eau.sixnode.electricalmath;

import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.gui.ItemStackFilter;
import org.ja13.eau.gui.SlotFilter;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.NodeBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.gui.ItemStackFilter;
import org.ja13.eau.gui.SlotFilter;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.NodeBase;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalMathContainer extends BasicContainer {

    NodeBase node = null;
    public static final int restoneSlotId = 0;

    public ElectricalMathContainer(NodeBase node, EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
            new SlotFilter(inventory, restoneSlotId, 125 + 27 + 44 / 2, 25, 64,
                new ItemStackFilter[]{new ItemStackFilter(Items.redstone)}, ISlotSkin.SlotSkin.medium, new String[]{I18N.tr("Redstone slot")})
        });
        this.node = node;
    }
}
