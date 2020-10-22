package org.ja13.eau.sixnode.powerinductorsix;

import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.item.CopperCableDescriptor;
import org.ja13.eau.item.FerromagneticCoreDescriptor;
import org.ja13.eau.misc.BasicContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.CopperCableDescriptor;
import org.ja13.eau.item.FerromagneticCoreDescriptor;
import org.ja13.eau.misc.BasicContainer;

import static org.ja13.eau.i18n.I18N.tr;

public class PowerInductorSixContainer extends BasicContainer {

    static final int cableId = 0;
    static final int coreId = 1;

    public PowerInductorSixContainer(EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
            new GenericItemUsingDamageSlot(inventory, cableId, 132, 8, 19, CopperCableDescriptor.class,
                ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Copper cable slot"), I18N.tr("(Increases inductance)")}),
            new GenericItemUsingDamageSlot(inventory, coreId, 132 + 20, 8, 1, FerromagneticCoreDescriptor.class,
                ISlotSkin.SlotSkin.medium, new String[]{I18N.tr("Ferromagnetic core slot")})
        });
    }
}
/*				new SlotFilter(inventory, 0, 62 + 0, 17, new ItemStackFilter[]{new ItemStackFilter(Block.wood, 0, 0)}),
                new SlotFilter(inventory, 1, 62 + 18, 17, new ItemStackFilter[]{new ItemStackFilter(Item.coal, 0, 0)})
*/
