package org.ja13.eau.item

import org.ja13.eau.generic.GenericItemUsingDamageDescriptor
import org.ja13.eau.i18n.I18N.tr
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.UtilsClient
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.IItemRenderer
import org.lwjgl.opengl.GL11
import javax.xml.crypto.Data

class BrushDescriptor(name: String): GenericItemUsingDamageDescriptor(name) {

    private val ricon = ResourceLocation("eau", "textures/items/" + name.toLowerCase().replace(" ", "") + ".png")


    override fun getName(stack: ItemStack): String {
        val creative = Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode
        val color = getColor(stack)
        val life = getLife(stack)
        return if (!creative && color == 15 && life == 0) "Empty " + super.getName(stack) else super.getName(stack)?: ""
    }

    fun getColor(stack: ItemStack) = stack.itemDamage and 0xF

    private fun getLife(stack: ItemStack?) = if (stack == null || stack.tagCompound == null)
        32
    else
        stack.tagCompound.getInteger("life")

    fun setLife(stack: ItemStack, life: Int) {
        stack.tagCompound.setInteger("life", life)
    }

    override fun getDefaultNBT(): NBTTagCompound? {
        val nbt = NBTTagCompound()
        nbt.setInteger("life", 32)
        return nbt
    }

    override fun addInformation(itemStack: ItemStack?, entityPlayer: EntityPlayer?, list: MutableList<String>, par4: Boolean) {
        super.addInformation(itemStack, entityPlayer, list, par4)

        if (itemStack != null) {
            val creative = Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode
            list.add(tr("Can paint %1$ blocks", if (creative) "infinite" else itemStack.tagCompound.getInteger("life")))
        }
    }

    fun use(stack: ItemStack, entityPlayer: EntityPlayer): Boolean {

        val creative = entityPlayer.capabilities.isCreativeMode
        var life = stack.tagCompound.getInteger("life")
        return if (creative || life != 0) {
            if (!creative) {
                --life
                stack.tagCompound.setInteger("life", life)
            }
            true
        } else {
            Utils.addChatMessage(entityPlayer, tr("Brush is dry"))
            false
        }
    }

    override fun handleRenderType(item: ItemStack?, type: IItemRenderer.ItemRenderType?) = type == IItemRenderer.ItemRenderType.INVENTORY

    override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType?, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper?) =
        type != IItemRenderer.ItemRenderType.INVENTORY

    override fun renderItem(type: IItemRenderer.ItemRenderType?, item: ItemStack?, vararg data: Any?) {
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            val creative = Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode
            UtilsClient.drawIcon(type, ricon)
            if (!creative) {
                GL11.glColor4f(1f, 1f, 1f, 0.75f - 0.75f * getLife(item) / 32f)
                UtilsClient.drawIcon(type, dryOverlay)
                GL11.glColor3f(1f, 1f, 1f)
            }
        } else {
            super.renderItem(type, item, *data)
        }
    }

    companion object {
        private val dryOverlay = ResourceLocation("eau", "textures/items/brushdryoverlay.png")
    }
}
