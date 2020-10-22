package org.ja13.eau.transparentnode.eggincubator;

import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.gui.ItemStackFilter;
import org.ja13.eau.gui.SlotFilter;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.INodeContainer;
import org.ja13.eau.node.Node;
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
import org.ja13.eau.node.INodeContainer;
import org.ja13.eau.node.Node;
import org.ja13.eau.node.NodeBase;

import static org.ja13.eau.i18n.I18N.tr;

public class EggIncubatorContainer extends BasicContainer implements INodeContainer {

    public static final int EggSlotId = 0;
    private final Node node;

    public EggIncubatorContainer(EntityPlayer player, IInventory inventory, Node node) {
        super(player, inventory, new Slot[]{
            new SlotFilter(inventory, EggSlotId, 176 / 2 - 8, 7, 64,
                new ItemStackFilter[]{new ItemStackFilter(Items.egg)},
                ISlotSkin.SlotSkin.medium, new String[]{I18N.tr("Egg slot")})
            //	new SlotFilter(inventory, 1, 62 + 18, 17, 1, new ItemStackFilter[]{new ItemStackFilter(Eln.sixNodeBlock, 0xFF, Eln.electricalCableId)})
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
