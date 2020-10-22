package org.ja13.eau.ore

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.World
import org.ja13.eau.EAU
import java.util.*

class OreBlock : Block(Material.rock) {
    override fun damageDropped(i: Int): Int {
        return i
    }

    override fun getSubBlocks(i: Item, tab: CreativeTabs?, l: MutableList<*>?) { //Puts all sub blocks into the creative inventory
        super.getSubBlocks(i, tab, l)
        EAU.oreItem.getSubItems(i, tab, l)
    }

    @SideOnly(Side.CLIENT)
    override fun getIcon(par1: Int, par2: Int): IIcon? {
        val desc = EAU.oreItem.getDescriptor(par2) ?: return null
        return desc.getBlockIconId(par1, par2)
    }

    fun getBlockDropped(w: World?, x: Int, y: Int, z: Int, meta: Int, fortune: Int): ArrayList<ItemStack> {
        val desc = EAU.oreItem.getDescriptor(meta) ?: return ArrayList()
        return desc.getBlockDropped(fortune)
    }

    init {
        setHardness(3.0f) //The block hardness
        setResistance(5.0f) //The explosion resistance
    }
}
