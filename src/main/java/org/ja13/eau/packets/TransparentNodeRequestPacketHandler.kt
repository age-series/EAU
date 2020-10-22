package org.ja13.eau.packets

import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import org.ja13.eau.misc.Utils
import org.ja13.eau.node.NodeManager
import org.ja13.eau.node.transparent.TransparentNode

/**
 * Created by Gregory Maddra on 2016-06-27.
 */
class TransparentNodeRequestPacketHandler : IMessageHandler<TransparentNodeRequestPacket, TransparentNodeResponsePacket> {
    override fun onMessage(message: TransparentNodeRequestPacket?, ctx: MessageContext?): TransparentNodeResponsePacket? {
        val c = message!!.coord
        val node = org.ja13.eau.node.NodeManager.instance.getNodeFromCoordonate(c) as? org.ja13.eau.node.transparent.TransparentNode
        var stringMap: Map<String, String> = emptyMap()
        if (node != null) {
            try {
                stringMap = node.element.waila
            } catch (e: NullPointerException) {
                Utils.println("Attempted to get WAILA info for an invalid node!")
                e.printStackTrace()
                return null
            }
        }
        return TransparentNodeResponsePacket(stringMap, c)
    }
}
