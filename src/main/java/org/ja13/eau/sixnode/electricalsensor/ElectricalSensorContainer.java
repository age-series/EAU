package org.ja13.eau.sixnode.electricalsensor;

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
import org.ja13.eau.sixnode.electriccable.ElectricCableDescriptor;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalSensorContainer extends BasicContainer {

    public static final int cableSlotId = 0;

    public ElectricalSensorContainer(EntityPlayer player, IInventory inventory, ElectricalSensorDescriptor d) {
        super(player, inventory, new Slot[]{
            new SixNodeItemSlot(inventory,
                cableSlotId,
                152,
                d.voltageOnly ? 14 : 62,
                1,
                new Class[]{ElectricCableDescriptor.class},
                ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Electrical cable slot")})
        });
    }
}
