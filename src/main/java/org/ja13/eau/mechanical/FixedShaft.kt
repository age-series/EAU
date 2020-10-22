package org.ja13.eau.mechanical

import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.node.transparent.EntityMetaTag
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.ThermalLoad
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound

class FixedShaftDescriptor(name: String, override val obj: org.ja13.eau.misc.Obj3D) : SimpleShaftDescriptor(
    name, FixedShaftElement::class, ShaftRender::class, org.ja13.eau.node.transparent.EntityMetaTag.Basic
) {
    override val static = arrayOf(obj.getPart("Stand"), obj.getPart("Shaft"))
    override val rotating = emptyArray<org.ja13.eau.misc.Obj3D.Obj3DPart>()

    override fun draw(angle: Double) {
        static.forEach { it.draw() }
    }
}

class FixedShaftElement(node: org.ja13.eau.node.transparent.TransparentNode, desc_: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : SimpleShaftElement(node, desc_) {
    override val shaftMass = 10.0

    override var shaft: ShaftNetwork = StaticShaftNetwork()
    override fun setShaft(dir: Direction, net: ShaftNetwork?) {
        if(net == null) return
        if(net !is StaticShaftNetwork) {
            val staticNet = StaticShaftNetwork()
            staticNet.takeAll(net)  // NB: This recurses back here, hopefully into the other branch
            super.setShaft(dir, staticNet)
        } else {
            super.setShaft(dir, net)
        }
    }

    override fun initialize() {
        reconnect()
        val rads = shaft.rads  // Carry over loaded rads, if any
        shaft = StaticShaftNetwork(this, shaftConnectivity.iterator())
        shaft.rads = rads
        // Utils.println(String.format("SS.i: new %s r=%f", shaft, shaft.rads))
        shaftConnectivity.forEach {
            // These calls can still change the speed via mergeShaft
            shaft.connectShaft(this, it)
        }
    }

    override fun thermoMeterString(side: Direction?): String? = null
    override fun getThermalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ThermalLoad? = null
    override fun getElectricalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ElectricalLoad? = null
    override fun getConnectionMask(side: Direction?, lrdu: LRDU?): Int = 0
    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float, vz: Float): Boolean = false
}
