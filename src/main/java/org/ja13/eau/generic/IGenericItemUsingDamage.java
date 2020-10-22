package org.ja13.eau.generic;

import net.minecraft.item.ItemStack;

public interface IGenericItemUsingDamage {

    GenericItemUsingDamageDescriptor getDescriptor(int damage);

    GenericItemUsingDamageDescriptor getDescriptor(ItemStack itemStack);
}
