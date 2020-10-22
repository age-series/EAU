package org.ja13.eau.ore

import cpw.mods.fml.common.registry.GameRegistry
import org.ja13.eau.generic.GenericItemBlockUsingDamage
import net.minecraft.block.Block
import javax.management.Descriptor

class OreItem(b: Block?) : GenericItemBlockUsingDamage<OreDescriptor?>(b) {
    override fun getMetadata(par1: Int): Int {
        return par1
    }

    fun addDescriptor(damage: Int, descriptor: OreDescriptor) {
        super.addDescriptor(damage, descriptor)
        GameRegistry.registerWorldGenerator(descriptor, 0)
    }
}
