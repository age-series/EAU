package org.ja13.eau.item.electricalitem;

import org.ja13.eau.misc.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.xml.crypto.Data;

public class ElectricalAxe extends ElectricalTool {

    public ElectricalAxe(String name, float strengthOn, float strengthOff,
                         double energyStorage, double energyPerBlock, double chargePower) {
        super(name, strengthOn, strengthOff, energyStorage, energyPerBlock, chargePower);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, Block block) {
        float value = block != null && (block.getMaterial() == Material.wood || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine) ? getStrength(stack) : super.getStrVsBlock(stack, block);
        Utils.println(value);
        return value;
    }
}
