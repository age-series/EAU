package org.ja13.eau.gridnode

import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.preserveMatrix
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import org.lwjgl.opengl.GL11

import java.util.ArrayList

import org.lwjgl.opengl.GL11.*

open class GridDescriptor(name: String, private val obj: org.ja13.eau.misc.Obj3D, ElementClass: Class<*>, RenderClass: Class<*>, val cableTexture: String, val cableDescriptor: org.ja13.eau.sixnode.genericcable.GenericCableDescriptor, val connectRange: Int) : org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, ElementClass, RenderClass) {
    val plus = ArrayList<org.ja13.eau.misc.Obj3D.Obj3DPart>()
    val gnd = ArrayList<org.ja13.eau.misc.Obj3D.Obj3DPart>()

    protected var static_parts = ArrayList<org.ja13.eau.misc.Obj3D.Obj3DPart>()
    protected var rotating_parts = ArrayList<org.ja13.eau.misc.Obj3D.Obj3DPart>()

    init {
        rotating_parts.add(obj.getPart("main"))
        var i = 0
        while (true) {
            val plus = obj.getPart("p" + i)
            val gnd = obj.getPart("g" + i)
            if (plus == null || gnd == null) break
            rotating_parts.add(plus)
            rotating_parts.add(gnd)
            this.plus.add(plus)
            this.gnd.add(gnd)
            i++
        }
    }

    fun draw(idealRenderingAngle: Float) {
        preserveMatrix {
            glRotatef(idealRenderingAngle, 0f, 1f, 0f)
            for (part in rotating_parts) {
                part.draw()
            }
        }
        for (part in static_parts) {
            part.draw()
        }
    }

    open fun hasCustomIcon() = false

    override fun renderItem(type: IItemRenderer.ItemRenderType, item: ItemStack, vararg data: Any) {
        if(type == IItemRenderer.ItemRenderType.INVENTORY &&
            hasCustomIcon()) {
            super.renderItem(type, item, data)
            return
        }
        objItemScale(obj)
        Direction.ZN.glRotateXnRef()
        GL11.glPushMatrix()
        GL11.glTranslatef(0f, -1f, 0f)
        GL11.glScalef(0.6f, 0.6f, 0.6f)
        draw(0f)
        GL11.glPopMatrix()
    }

    override fun handleRenderType(item: ItemStack, type: IItemRenderer.ItemRenderType): Boolean {
        return true
    }

    override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType, item: ItemStack,
                                       helper: IItemRenderer.ItemRendererHelper): Boolean {
        if(helper == IItemRenderer.ItemRendererHelper.INVENTORY_BLOCK)
            return !hasCustomIcon()
        return true
    }

    fun use2DIcon(): Boolean {
        return hasCustomIcon()
    }

    open fun rotationIsFixed(): Boolean {
        return false
    }
}
