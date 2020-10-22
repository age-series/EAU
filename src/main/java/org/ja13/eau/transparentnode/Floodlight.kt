package org.ja13.eau.transparentnode

import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.node.transparent.*
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.ThermalLoad
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11

class BasicFloodlightDescriptor(name: String, val obj: org.ja13.eau.misc.Obj3D): org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, BasicFloodlightElement::class.java, BasicFloodlightRender::class.java) {
    val base: org.ja13.eau.misc.Obj3D.Obj3DPart
    val swivel: org.ja13.eau.misc.Obj3D.Obj3DPart
    val head: org.ja13.eau.misc.Obj3D.Obj3DPart
    val bulb1: org.ja13.eau.misc.Obj3D.Obj3DPart
    val bulb2: org.ja13.eau.misc.Obj3D.Obj3DPart
    val bulb1_on: org.ja13.eau.misc.Obj3D.Obj3DPart
    val bulb2_on: org.ja13.eau.misc.Obj3D.Obj3DPart

    init {
        this.name = name
        base = obj.getPart("Lamp_Base_Cube.008")
        swivel = obj.getPart("Lamp_Swivel_Cube.014")
        head = obj.getPart("Lamp_Head_Cylinder.004")
        bulb1 = obj.getPart("Lamp1_OFF_Cylinder.003")
        bulb2 = obj.getPart("Lamp2_OFF_Cylinder.002")
        bulb1_on = obj.getPart("Lamp1_ON_Cylinder.000")
        bulb2_on = obj.getPart("Lamp2_ON_Cylinder.001")
    }

    fun draw(front: Direction, x: Double, y: Double) {
        front.glRotateZnRefInv()
        GL11.glTranslated(-0.5, -0.5, 0.5)
        base.draw()
        GL11.glTranslated(0.5, 0.5, -0.5)
        GL11.glRotated(y, 0.0, 1.0, 0.0)
        GL11.glTranslated(-0.5, -0.5, 0.5)
        swivel.draw()
        GL11.glTranslated(0.5, 0.5, -0.5)
        GL11.glRotated(-x, 1.0, 0.0, 0.0)
        GL11.glTranslated(-0.5, -0.5, 0.5)
        head.draw()
        bulb1.draw()
        bulb2.draw()
        bulb1_on.draw()
        bulb2_on.draw()
    }
}

class BasicFloodlightElement(node: org.ja13.eau.node.transparent.TransparentNode, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElement(node, descriptor) {
    override fun thermoMeterString(side: Direction?): String {
        return ""
    }

    override fun multiMeterString(side: Direction?): String {
        return ""
    }

    override fun getElectricalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ElectricalLoad? {
        return null
    }

    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float, vz: Float): Boolean {
        return false
    }

    override fun getConnectionMask(side: Direction?, lrdu: LRDU?): Int {
        return 0
    }

    override fun getThermalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ThermalLoad? {
        return null
    }

    override fun initialize() {

    }
}

class BasicFloodlightRender(val tileEntity: org.ja13.eau.node.transparent.TransparentNodeEntity, val descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElementRender(tileEntity, descriptor) {

    var x: Double = 0.0
    var y: Double = 0.0

    override fun draw() {
        (descriptor as BasicFloodlightDescriptor).draw(front, x, y)
    }

    override fun refresh(deltaT: Double) {
        x += deltaT * 6
        y += deltaT * 6
        if (x > 180) x = 0.0
        if (y > 360) y = 0.0
    }
}

class MotorizedFloodlightDescriptor(name: String, val obj: org.ja13.eau.misc.Obj3D): org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, MotorizedFloodlightElement::class.java, MotorizedFloodlightRender::class.java) {

    val base: org.ja13.eau.misc.Obj3D.Obj3DPart
    val swivel: org.ja13.eau.misc.Obj3D.Obj3DPart
    val head: org.ja13.eau.misc.Obj3D.Obj3DPart
    val bulb1: org.ja13.eau.misc.Obj3D.Obj3DPart
    val bulb2: org.ja13.eau.misc.Obj3D.Obj3DPart
    val bulb1_on: org.ja13.eau.misc.Obj3D.Obj3DPart
    val bulb2_on: org.ja13.eau.misc.Obj3D.Obj3DPart



    init {
        this.name = name
        base = obj.getPart("Lamp_Base_Cube.008")
        swivel = obj.getPart("Lamp_Swivel_Cube.014")
        head = obj.getPart("Lamp_Head_Cylinder.004")
        bulb1 = obj.getPart("Lamp1_OFF_Cylinder.003")
        bulb2 = obj.getPart("Lamp2_OFF_Cylinder.002")
        bulb1_on = obj.getPart("Lamp1_ON_Cylinder.000")
        bulb2_on = obj.getPart("Lamp2_ON_Cylinder.001")


    }

    fun draw(front: Direction, x: Double, y: Double) {
        front.glRotateZnRefInv()
        GL11.glTranslated(-0.5, -0.5, 0.5)
        //base.draw()
        GL11.glTranslated(0.5, 0.5, -0.5)
        GL11.glRotated(y, 0.0, 1.0, 0.0)
        GL11.glTranslated(-0.5, -0.5, 0.5)
        //swivel.draw()
        GL11.glTranslated(0.5, 0.5, -0.5)
        GL11.glRotated(-x, 1.0, 0.0, 0.0)
        GL11.glTranslated(-0.5, -0.5, 0.5)
        //head.draw()
        //bulb1.draw()
        //bulb2.draw()
    }
}

class MotorizedFloodlightElement(node: org.ja13.eau.node.transparent.TransparentNode, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElement(node, descriptor) {
    init {
        val desc = descriptor as BasicFloodlightDescriptor
    }

    override fun thermoMeterString(side: Direction?): String {
        return ""
    }

    override fun multiMeterString(side: Direction?): String {
        return ""
    }

    override fun getElectricalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ElectricalLoad? {
        return null
    }

    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float, vz: Float): Boolean {
        return false
    }

    override fun getConnectionMask(side: Direction?, lrdu: LRDU?): Int {
        return 0
    }

    override fun getThermalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ThermalLoad? {
        return null
    }

    override fun initialize() {

    }
}

class MotorizedFloodlightRender(val tileEntity: org.ja13.eau.node.transparent.TransparentNodeEntity, val descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElementRender(tileEntity, descriptor) {

    var x: Double = 0.0
    var y: Double = 0.0

    override fun draw() {
        (descriptor as BasicFloodlightDescriptor).draw(front, x, y)
    }

    override fun refresh(deltaT: Double) {
        x += deltaT * 6
        y += deltaT * 6
        if (x > 180) x = 0.0
        if (y > 360) y = 0.0
    }
}
