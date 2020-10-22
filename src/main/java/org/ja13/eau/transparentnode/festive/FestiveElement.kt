package org.ja13.eau.transparentnode.festive

import org.ja13.eau.misc.Coordonate
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeElement
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.mna.component.Resistor
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import org.ja13.eau.sixnode.lampsupply.LampSupplyElement
import net.minecraft.entity.player.EntityPlayer
import java.io.DataOutputStream
import java.io.IOException

class FestiveElement(node: org.ja13.eau.node.transparent.TransparentNode, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElement(node, descriptor) {

    val electricalLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("electricalLoad")
    val loadResistor = org.ja13.eau.sim.mna.component.Resistor(electricalLoad, null)
    var powerChannel = "xmas" // TODO: Add a GUI in the render panes and allow the user to specify a different channel.

    init {
        loadResistor.r = 1000.0
        slowProcessList.add(FestiveElementProcess(this))
    }

    override fun thermoMeterString(side: Direction?): String {
        return "Not as warm as it could be"
    }

    override fun multiMeterString(side: Direction?): String {
        return "It probably works if you apply ~200v to the xmas wireless channel"
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
        connect()
    }

    override fun networkSerialize(stream: DataOutputStream?) {
        super.networkSerialize(stream)
        try {
            stream?.writeBoolean(node.lightValue > 4)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    class FestiveElementProcess(val elem: FestiveElement): org.ja13.eau.sim.IProcess {
        var bestChannelHandle: Pair<Double, org.ja13.eau.sixnode.lampsupply.LampSupplyElement.PowerSupplyChannelHandle>? = null

        private fun findBestSupply(here: Coordonate, forceUpdate: Boolean = false): Pair<Double, org.ja13.eau.sixnode.lampsupply.LampSupplyElement.PowerSupplyChannelHandle>? {
            val chanMap = org.ja13.eau.sixnode.lampsupply.LampSupplyElement.channelMap[elem.powerChannel] ?: return null
            val bestChanHand = bestChannelHandle
            // Here's our cached value. We just check if it's null and if it's still a thing.
            if (!(bestChanHand == null || forceUpdate || !chanMap.contains(bestChanHand.second))) {
                return bestChanHand // we good!
            }
            val list = org.ja13.eau.sixnode.lampsupply.LampSupplyElement.channelMap[elem.powerChannel]?.filterNotNull() ?: return null
            val map = list.map { Pair(it.element.sixNode.coordonate.trueDistanceTo(here), it) }
            val sortedBy = map.sortedBy { it.first }
            val chanHand = sortedBy.first()
            bestChannelHandle = chanHand
            return bestChannelHandle
        }

        override fun process(time: Double) {
            val lampSupplyList = findBestSupply(elem.node.coordonate)
            val best = lampSupplyList?.second
            if (best != null && best.element.getChannelState(best.id)) {
                best.element.addToRp(elem.loadResistor.r)
                elem.electricalLoad.state = best.element.powerLoad.state
            } else {
                elem.electricalLoad.state = 0.0
            }
            var lightDouble = 12 * (Math.abs(elem.loadResistor.u) - 180.0) / 20.0
            lightDouble *= 16
            elem.node.lightValue = lightDouble.toInt().coerceIn(0, 15)
        }
    }
}
