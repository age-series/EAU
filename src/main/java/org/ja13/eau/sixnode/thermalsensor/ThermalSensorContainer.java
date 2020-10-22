package org.ja13.eau.sixnode.thermalsensor;

import org.ja13.eau.gui.ISlotSkin.SlotSkin;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.six.SixNodeItemSlot;
import org.ja13.eau.sixnode.electriccable.ElectricCableDescriptor;
import org.ja13.eau.sixnode.thermalcable.ThermalCableDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.ja13.eau.gui.ISlotSkin;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.BasicContainer;
import org.ja13.eau.node.six.SixNodeItemSlot;
import org.ja13.eau.sixnode.electriccable.ElectricCableDescriptor;
import org.ja13.eau.sixnode.thermalcable.ThermalCableDescriptor;

import static org.ja13.eau.i18n.I18N.tr;

public class ThermalSensorContainer extends BasicContainer {

    public static final int cableSlotId = 0;

    public ThermalSensorContainer(EntityPlayer player, IInventory inventory, boolean acceptsElectricalCable) {
        super(player, inventory, new Slot[]{
            new SixNodeItemSlot(inventory, cableSlotId, 152, 62, 1, acceptsElectricalCable ?
                new Class[]{ThermalCableDescriptor.class, ElectricCableDescriptor.class} :
                new Class[]{ThermalCableDescriptor.class}, ISlotSkin.SlotSkin.medium,
                new String[]{I18N.tr("Cable slot")})
        });
    }
}
