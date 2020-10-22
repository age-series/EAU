package org.ja13.eau.item;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public interface IInteract {
    void interact(EntityPlayerMP playerMP, ItemStack itemStack, byte param);
}
