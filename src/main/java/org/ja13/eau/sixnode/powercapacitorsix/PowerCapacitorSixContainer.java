package org.ja13.eau.sixnode.powercapacitorsix;

import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.gui.ItemStackFilter;
import org.ja13.eau.gui.SlotFilter;
import org.ja13.eau.item.DielectricItem;
import org.ja13.eau.misc.BasicContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.gui.ItemStackFilter;
import org.ja13.eau.gui.SlotFilter;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.DielectricItem;
import org.ja13.eau.misc.BasicContainer;

import static org.ja13.eau.i18n.I18N.tr;

public class PowerCapacitorSixContainer extends BasicContainer {

    static final int redId = 0;
    static final int dielectricId = 1;

    public PowerCapacitorSixContainer(EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
            new SlotFilter(inventory, redId, 132, 8, 13,
                new ItemStackFilter[]{new ItemStackFilter(Items.redstone)},
                ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Redstone slot"), I18N.tr("(Increases capacity)")}),
            new GenericItemUsingDamageSlot(inventory, dielectricId, 132 + 20, 8, 20, DielectricItem.class,
                ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Dielectric slot"), I18N.tr("(Increases maximum voltage)")})
        });
    }
}
