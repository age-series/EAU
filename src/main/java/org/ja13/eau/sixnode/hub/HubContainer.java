package org.ja13.eau.sixnode.hub;

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

public class HubContainer extends BasicContainer {

    public static final int cableSlotId = 0;

    public HubContainer(EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
            new SixNodeItemSlot(inventory, cableSlotId + 0, 176 / 2 + 80 / 2 + 2, 134 / 2 - 9, 1,
                new Class[]{ElectricCableDescriptor.class}, ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Electrical cable slot")}),
            new SixNodeItemSlot(inventory, cableSlotId + 1, 176 / 2 - 80 / 2 - 20, 134 / 2 - 9, 1,
                new Class[]{ElectricCableDescriptor.class}, ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Electrical cable slot")}),
            new SixNodeItemSlot(inventory, cableSlotId + 2, 176 / 2 - 9, 134 / 2 + 80 / 2 + 2, 1,
                new Class[]{ElectricCableDescriptor.class}, ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Electrical cable slot")}),
            new SixNodeItemSlot(inventory, cableSlotId + 3, 176 / 2 - 9, 134 / 2 - 80 / 2 - 20, 1,
                new Class[]{ElectricCableDescriptor.class}, ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Electrical cable slot")}),
            //		new SixNodeItemSlot(inventory, cableSlotId + 4, 176 / 4 + 18 * 4 - 8, 8, 1, new Class[]{ElectricalCableDescriptor.class}, SlotSkin.medium, new String[]{"Electrical Cable Slot"}),
            //		new SixNodeItemSlot(inventory, cableSlotId + 5, 176 / 4 + 18 * 5 - 8, 8, 1, new Class[]{ElectricalCableDescriptor.class}, SlotSkin.medium, new String[]{"Electrical Cable Slot"})
        });
    }
}
