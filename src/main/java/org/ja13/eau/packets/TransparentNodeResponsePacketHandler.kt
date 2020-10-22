package org.ja13.eau.packets

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import org.ja13.eau.integration.waila.WailaCache

/**
 * Created by Gregory Maddra on 2016-06-27.
 */
class TransparentNodeResponsePacketHandler : IMessageHandler<TransparentNodeResponsePacket, IMessage> {
    override fun onMessage(message: TransparentNodeResponsePacket?, ctx: MessageContext?): IMessage? {
        val map = message!!.map
        val coord = message.coord
        WailaCache.nodes.put(coord, map)
        return null
    }
}
