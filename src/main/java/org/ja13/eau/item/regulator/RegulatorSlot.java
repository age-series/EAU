package org.ja13.eau.item.regulator;

import org.ja13.eau.generic.GenericItemUsingDamageSlot;
import org.ja13.eau.item.regulator.IRegulatorDescriptor.RegulatorType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;

import static org.ja13.eau.i18n.I18N.tr;

public class RegulatorSlot extends GenericItemUsingDamageSlot {

    private final IRegulatorDescriptor.RegulatorType[] type;
    private final static String COMMENT = I18N.tr("Regulator slot");

    public RegulatorSlot(IInventory inventory, int slot, int x, int y, int stackLimit, IRegulatorDescriptor.RegulatorType[] type, SlotSkin
        skin) {
        this(inventory, slot, x, y, stackLimit, type, skin, COMMENT);
    }

    public RegulatorSlot(IInventory inventory, int slot, int x, int y, int stackLimit, IRegulatorDescriptor.RegulatorType[] type, SlotSkin
        skin, final String comment) {
        super(inventory, slot, x, y, stackLimit, IRegulatorDescriptor.class, skin, new String[]{comment});
        this.type = type;
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        if (!super.isItemValid(itemStack)) return false;
        IRegulatorDescriptor element = (IRegulatorDescriptor) IRegulatorDescriptor.getDescriptor(itemStack);
        for (IRegulatorDescriptor.RegulatorType t : type) {
            if (t == element.getType()) return true;
        }
        return false;
    }
}
