package org.ja13.eau.transparentnode.festive

import org.ja13.eau.ghost.GhostGroup
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.UtilsClient
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeElementRender
import org.ja13.eau.node.transparent.TransparentNodeEntity
import org.lwjgl.opengl.GL11
import java.io.DataInputStream
import java.io.IOException

class ChristmasTreeDescriptor(name: String, val obj: org.ja13.eau.misc.Obj3D): org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, FestiveElement::class.java, ChristmasTreeRender::class.java) {
    private var star: org.ja13.eau.misc.Obj3D.Obj3DPart? = null
    private var string1: org.ja13.eau.misc.Obj3D.Obj3DPart? = null
    private var string2: org.ja13.eau.misc.Obj3D.Obj3DPart? = null
    private var tree: org.ja13.eau.misc.Obj3D.Obj3DPart? = null

    init {
        this.name = name
        star = obj.getPart("StarOn_Star.002")
        string1 = obj.getPart("Strip1_Star.000")
        string2 = obj.getPart("Strip2_Star.001")
        tree = obj.getPart("Tree_Cone.006")
        val gg = org.ja13.eau.ghost.GhostGroup()
        gg.addRectangle(0, 2, 0, 1, -1, 1)
        gg.addElement(1, 2, 0)
        gg.addElement(1, 3, 0)
        gg.removeElement(0, 0, 0)
        ghostGroup = gg
    }

    fun draw(front: Direction, delta: Int, powered: Boolean) {
        if (star != null && tree != null && string1 != null && string2 != null) {
            front.glRotateZnRef()
            GL11.glTranslatef(0.5f, -0.5f, 0.5f)
            if (powered) {
                UtilsClient.drawLight(star)
                if (delta > 10) {
                    UtilsClient.drawLight(string2)
                    string1?.draw()
                } else {
                    UtilsClient.drawLight(string1)
                    string2?.draw()
                }
            } else {
                star?.draw()
                string1?.draw()
                string2?.draw()
            }
            tree?.draw()
        }
    }
}

class ChristmasTreeRender(val tileEntity: org.ja13.eau.node.transparent.TransparentNodeEntity, val descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElementRender(tileEntity, descriptor) {
    var x = 0
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
        (descriptor as ChristmasTreeDescriptor).draw(front, x, powered)
    }

    override fun refresh(deltaT: Double) {
        x += 1
        if (x > 20) x = 0
    }

    override fun cameraDrawOptimisation(): Boolean {
        return false
    }
}
