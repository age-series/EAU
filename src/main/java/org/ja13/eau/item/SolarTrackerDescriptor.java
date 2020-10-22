package org.ja13.eau.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;

import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class SolarTrackerDescriptor extends GenericItemUsingDamageDescriptorUpgrade {

    public SolarTrackerDescriptor(String name) {
        super(name);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        list.add(I18N.tr("Solar panel upgrade"));
    }
}
