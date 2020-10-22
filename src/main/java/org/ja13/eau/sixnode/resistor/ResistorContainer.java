package org.ja13.eau.sixnode.resistor;

import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.gui.ItemStackFilter;
import org.ja13.eau.gui.SlotFilter;
import org.ja13.eau.misc.BasicContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.gui.ItemStackFilter;
import org.ja13.eau.gui.SlotFilter;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.BasicContainer;

import static org.ja13.eau.i18n.I18N.tr;

/**
 * Created by svein on 05/08/15.
 */
public class ResistorContainer extends BasicContainer {
    public static final int coreId = 0;

    public ResistorContainer(EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
            new SlotFilter(inventory, coreId, 132, 8, 64, ItemStackFilter.OreDict("dustCoal"),
                ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Coal dust slot"), I18N.tr("(Sets resistance)")})
        });
    }
}
