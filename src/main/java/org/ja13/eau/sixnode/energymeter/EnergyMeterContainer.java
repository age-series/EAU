package org.ja13.eau.sixnode.energymeter;

import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.six.SixNodeItemSlot;
import org.ja13.eau.sixnode.electriccable.ElectricCableDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.six.SixNodeItemSlot;

import static org.ja13.eau.i18n.I18N.tr;

public class EnergyMeterContainer extends BasicContainer {

    public static final int cableSlotId = 0;

    public EnergyMeterContainer(EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
            new SixNodeItemSlot(inventory, cableSlotId, 160, 106, 1, new Class[]{ElectricCableDescriptor.class},
                ISlotSkin.SlotSkin.medium, new String[]{I18N.tr("Electrical cable slot")})
        });
    }
}
