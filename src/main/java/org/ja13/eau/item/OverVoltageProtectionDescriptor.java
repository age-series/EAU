package org.ja13.eau.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;

import java.util.Collections;
import java.util.List;

public class OverVoltageProtectionDescriptor extends GenericItemUsingDamageDescriptorUpgrade {

    public OverVoltageProtectionDescriptor(String name) {
        super(name);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        Collections.addAll(list, I18N.tr("Useful to prevent over-voltage\nof Batteries").split("\n"));
    }
}
