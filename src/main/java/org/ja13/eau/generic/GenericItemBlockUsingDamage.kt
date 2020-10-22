package org.ja13.eau.generic

import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.UtilsClient
import net.minecraft.block.Block
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.World
import java.util.*

open class GenericItemBlockUsingDamage<Descriptor : GenericItemBlockUsingDamageDescriptor?>(b: Block?) : ItemBlock(b) {
    var subItemList = Hashtable<Int, Descriptor?>()
    var orderList = ArrayList<Int>()
    @JvmField
    var descriptors = ArrayList<Descriptor>()
    var defaultElement: Descriptor? = null

    fun doubleEntry(src: Int, dst: Int) {
        subItemList[dst] = subItemList[src]
    }

    open fun addDescriptor(damage: Int, descriptor: Descriptor) {
        subItemList[damage] = descriptor
        val stack = ItemStack(this, 1, damage)
        stack.tagCompound = descriptor!!.defaultNBT
        //LanguageRegistry.addName(stack, descriptor.name);
        orderList.add(damage)
        descriptors.add(descriptor)
        descriptor.setParent(this, damage)
        GameRegistry.registerCustomItemStack(descriptor.name, descriptor.newItemStack(1))
    }

    fun addWithoutRegistry(damage: Int, descriptor: Descriptor) {
        subItemList[damage] = descriptor
        val stack = ItemStack(this, 1, damage)
        stack.tagCompound = descriptor!!.defaultNBT
        descriptor.setParent(this, damage)
    }

    fun getDescriptor(damage: Int): Descriptor? {
        return subItemList[damage]
    }

    fun getDescriptor(itemStack: ItemStack?): Descriptor? {
        if (itemStack == null) return defaultElement
        return if (itemStack.item !== this) defaultElement else getDescriptor(itemStack.itemDamage)
    }

    override fun getUnlocalizedName(par1ItemStack: ItemStack): String {
        val desc = getDescriptor(par1ItemStack)
        return desc?.name?.replace("\\s+".toRegex(), "_") ?: this.javaClass.name
    }

    override fun getIconFromDamage(damage: Int): IIcon? {
        val desc = getDescriptor(damage) ?: return null
        return desc.icon!!
    }

    @SideOnly(value = Side.CLIENT)
    override fun registerIcons(iconRegister: IIconRegister) {
        for (descriptor in subItemList.values) {
            descriptor!!.updateIcons(iconRegister)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(itemID: Item, tabs: CreativeTabs?, list: MutableList<*>?) {
        // You can also take a more direct approach and do each one individual but I prefer the lazy / right way
        for (id in orderList) {
            val stack = Utils.newItemStack(itemID, 1, id)
            stack.tagCompound = subItemList[id]!!.defaultNBT
            (list as MutableList<ItemStack>).add(stack)
        }
    }

    override fun addInformation(itemStack: ItemStack?, entityPlayer: EntityPlayer?, list: MutableList<Any?>?, par4: Boolean) {
        val desc = getDescriptor(itemStack) ?: return
        val listFromDescriptor = mutableListOf<String>()
        if (itemStack != null && entityPlayer != null)
            desc.addInfo(itemStack, entityPlayer, listFromDescriptor)
        UtilsClient.showItemTooltip(listFromDescriptor, list)
    }

    override fun onEntityItemUpdate(entityItem: EntityItem): Boolean {
        val desc = getDescriptor(entityItem.entityItem)
        return desc?.onEntityItemUpdate(entityItem) ?: false
    }

    override fun onItemUseFirst(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val desc = getDescriptor(stack)
        return desc?.onItemUseFirst(stack, player) ?: false
    }

    init {
        setHasSubtypes(true)
    }
}
