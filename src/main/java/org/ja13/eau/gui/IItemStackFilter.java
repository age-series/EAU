package org.ja13.eau.gui;

import net.minecraft.item.ItemStack;

public interface IItemStackFilter {
    boolean tryItemStack(ItemStack itemStack);
}
