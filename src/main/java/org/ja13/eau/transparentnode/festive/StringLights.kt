package org.ja13.eau.transparentnode.festive

import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.UtilsClient
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeElementRender
import org.ja13.eau.node.transparent.TransparentNodeEntity
import net.minecraft.init.Blocks
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.World
import org.lwjgl.opengl.GL11
import java.io.DataInputStream
import java.io.IOException

class StringLightsDescriptor(name: String, val obj: org.ja13.eau.misc.Obj3D): org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, FestiveElement::class.java, StringLightsRender::class.java) {
    private var base: org.ja13.eau.misc.Obj3D.Obj3DPart? = null
    private var light: org.ja13.eau.misc.Obj3D.Obj3DPart? = null

    init {
        this.name = name
        base = obj.getPart("Lights_Cube.009")
        light = obj.getPart("LightOn_Cube.002")
    }

    fun draw(front: Direction, powered: Boolean) {
        if (base != null && light != null) {
            front.glRotateZnRef()
            GL11.glRotatef(180.0f, 0f, 1f, 0f)
            GL11.glTranslatef(-0.5f, -0.5f, -0.5f)
            base?.draw()
            if (powered)
                UtilsClient.drawLight(light)
        }
    }

    override fun mustHaveWall() = true
    override fun mustHaveFloor() = false

    /*

    TODO: Fix Hitbox

    override fun addCollisionBoxesToList(par5AxisAlignedBB: AxisAlignedBB, list: MutableList<AxisAlignedBB>, world: World?, x: Int, y: Int, z: Int) {
        val bb = Blocks.stone.getCollisionBoundingBoxFromPool(world, x, y, z)
        bb.maxZ -= 0.5
        if (par5AxisAlignedBB.intersectsWith(bb)) list.add(bb)
    }
     */
}

class StringLightsRender(val tileEntity: org.ja13.eau.node.transparent.TransparentNodeEntity, val descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElementRender(tileEntity, descriptor) {

    var powered = false

    override fun networkUnserialize(stream: DataInputStream?) {
        super.networkUnserialize(stream)
        try {
            powered = stream!!.readBoolean()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun draw() {
        (descriptor as StringLightsDescriptor).draw(front, powered)
    }
}


