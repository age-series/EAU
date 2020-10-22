package org.ja13.eau.misc

import org.ja13.eau.node.GhostNode
import org.ja13.eau.node.NodeBase
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.ThermalLoad
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack

class GhostPowerNode(origin: Coordonate, front: Direction, offset: Coordonate, val load: org.ja13.eau.sim.ElectricalLoad): org.ja13.eau.node.GhostNode() {

    val coord = Coordonate(offset).apply { applyTransformation(front, origin) }

    fun initialize() {
        onBlockPlacedBy(coord, Direction.XN, null, null)
    }

    override fun initializeFromThat(front: Direction?, entityLiving: EntityLivingBase?, itemStack: ItemStack?) {
        connect()
    }

    override fun initializeFromNBT() {}

    override fun getSideConnectionMask(directionA: Direction?, lrduA: LRDU?) = org.ja13.eau.node.NodeBase.MASK_ELECTRIC

    override fun getThermalLoad(directionA: Direction, lrduA: LRDU, mask: Int): org.ja13.eau.sim.ThermalLoad? = null

    override fun getElectricalLoad(directionB: Direction, lrduB: LRDU, mask: Int): org.ja13.eau.sim.ElectricalLoad? = load
}
