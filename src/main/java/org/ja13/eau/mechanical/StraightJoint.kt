package org.ja13.eau.mechanical

import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.Utils
import org.ja13.eau.node.transparent.EntityMetaTag
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.ThermalLoad
import net.minecraft.entity.player.EntityPlayer

open class StraightJointDescriptor(baseName: String, obj: org.ja13.eau.misc.Obj3D) : SimpleShaftDescriptor(baseName,
    StraightJointElement::class, ShaftRender::class, org.ja13.eau.node.transparent.EntityMetaTag.Basic) {
    override val obj = obj
    override val static = arrayOf(obj.getPart("Stand"), obj.getPart("Cowl"))
    override val rotating = arrayOf(obj.getPart("Shaft"))
}

open class StraightJointElement(node: org.ja13.eau.node.transparent.TransparentNode, desc_: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : SimpleShaftElement(node, desc_) {
    override val shaftMass = 0.5

    override fun getElectricalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ElectricalLoad? = null

    override fun getThermalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ThermalLoad? = null

    override fun getConnectionMask(side: Direction?, lrdu: LRDU?): Int = 0

    override fun thermoMeterString(side: Direction?): String? = null

    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float,
                                  vz: Float): Boolean = false

    override fun getWaila(): Map<String, String> {
        val info = mutableMapOf<String, String>()
        info["Speed"] = Utils.plotRads(shaft.rads)
        info["Energy"] = Utils.plotEnergy(shaft.energy)
        return info
    }
}
