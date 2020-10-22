package org.ja13.eau.sixnode.lampsocket;

import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.item.LampSlot;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.six.SixNodeItemSlot;
import org.ja13.eau.sixnode.electriccable.ElectricCableDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.LampSlot;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.six.SixNodeItemSlot;

import static org.ja13.eau.i18n.I18N.tr;

public class LampSocketContainer extends BasicContainer {

    public static final int lampSlotId = 0;
    public static final int cableSlotId = 1;

    public LampSocketContainer(EntityPlayer player, IInventory inventory, LampSocketDescriptor descriptor) {
        super(player, inventory, new Slot[]{
            new LampSlot(inventory, lampSlotId, 70 + 0, 57, 1, descriptor.socketType),
            //new SixNodeItemSlot(inventory, 0, 1, 62 + 0, 17, new Class[]{ElectricalCableDescriptor.class}),
            new SixNodeItemSlot(inventory, cableSlotId, 70 + 18, 57, 1, new Class[]{ElectricCableDescriptor.class},
                ISlotSkin.SlotSkin.medium, new String[]{I18N.tr("Electrical cable slot")})
        });
    }
}
