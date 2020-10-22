package org.ja13.eau.generic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;

import java.util.Collections;
import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class GenericItemUsingDamageDescriptorWithComment extends GenericItemUsingDamageDescriptor {

    String[] description;

    public GenericItemUsingDamageDescriptorWithComment(String name, String[] description) {
        super(name);
        this.description = description;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        for (String str : description) {
            Collections.addAll(list, I18N.tr(str).split("\n"));
        }
    }
}
