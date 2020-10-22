package org.ja13.eau.generic

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.IIcon
import org.ja13.eau.EAU
import org.ja13.eau.misc.ImageList
import java.util.*

open class GenericItemBlockUsingDamageDescriptor @JvmOverloads constructor(name: String, iconName: String = name) {
    var iconName: String = ""
    var icon: IIcon? = null
    @JvmField
    var name: String
    var parentItem: Item? = null
    @JvmField
    var parentItemDamage = 0
    fun setDefaultIcon(name: String) {
        val iconName = name.replace(" ", "").toLowerCase()
        this.iconName = if (EAU.noSymbols && regularIconExists(iconName)) "$iconName-ni" else iconName

        ImageList.blockTextures.add("$iconName.png")
        ImageList.blockTextures.add("$iconName-ni.png")

        if (EAU.instance.isDevelopmentRun) {
            if (!schematicIconExists(iconName))
                println("There's an icon missing!: assets/eau/textures/blocks/$iconName.png")
            if (!regularIconExists("$iconName-ni"))
                println("There's an icon missing: assets/eau/textures/blocks/$iconName-ni.png")
        }
    }

    private fun schematicIconExists(name: String) = javaClass.classLoader.getResource("assets/eau/textures/blocks/$iconName.png") != null
    private fun regularIconExists(name: String) = javaClass.classLoader.getResource("assets/eau/textures/blocks/$iconName-ni.png") != null

    open val defaultNBT: NBTTagCompound?
        get() = null

    open fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {}

    @SideOnly(value = Side.CLIENT)
    fun updateIcons(iconRegister: IIconRegister) {
        icon = iconRegister.registerIcon("eau:$iconName")
    }

    open fun getName(stack: ItemStack?): String? {
        return name
    }

    open fun setParent(item: Item?, damage: Int) {
        parentItem = item
        parentItemDamage = damage
    }

    fun newItemStack(size: Int): ItemStack {
        return ItemStack(parentItem, size, parentItemDamage)
    }

    fun newItemStack(): ItemStack {
        return ItemStack(parentItem, 1, parentItemDamage)
    }

    fun checkSameItemStack(stack: ItemStack?): Boolean {
        if (stack == null) return false
        return !(stack.item !== parentItem || stack.itemDamage != parentItemDamage)
    }

    open fun onEntityItemUpdate(entityItem: EntityItem?): Boolean {
        return false
    }

    fun onItemUseFirst(stack: ItemStack?, player: EntityPlayer?): Boolean {
        return false
    }

    companion object {
        var INVALID_NAME = "\$NO_DESCRIPTOR"
        var byName = HashMap<String, GenericItemBlockUsingDamageDescriptor>()
        fun getByName(name: String?): GenericItemBlockUsingDamageDescriptor? {
            return byName[name]
        }

        fun getDescriptor(stack: ItemStack?): GenericItemBlockUsingDamageDescriptor? {
            if (stack == null) return null
            val item = stack.item
            if (!(item is GenericItemBlockUsingDamage<*>)) return null
            return item.getDescriptor(stack)
        }

        @JvmStatic
        fun getDescriptor(stack: ItemStack?, extendClass: Class<*>): GenericItemBlockUsingDamageDescriptor? {
            val desc = getDescriptor(stack) ?: return null
            return if (!extendClass.isAssignableFrom(desc.javaClass)) null else desc
        }
    }

    init {
        setDefaultIcon(iconName)
        this.name = name
        byName[name] = this
    }
}
