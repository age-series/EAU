package org.ja13.eau.packets

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import org.ja13.eau.integration.waila.SixNodeCoordonate
import org.ja13.eau.integration.waila.SixNodeWailaData
import org.ja13.eau.integration.waila.WailaCache
import org.ja13.eau.misc.Coordonate

class SixNodeWailaResponsePacketHandler : IMessageHandler<SixNodeWailaResponsePacket, IMessage> {

    private fun Coordonate.isNull() = this.x == 0 && this.y == 0 && this.z == 0 && this.dimention == 0

    override fun onMessage(message: SixNodeWailaResponsePacket, ctx: MessageContext?): IMessage? {
        if (!message.coord.isNull()) {
            WailaCache.sixNodes.put(SixNodeCoordonate(message.coord, message.side),
                SixNodeWailaData(message.itemStack, message.map))
        }

        return null
    }
}
