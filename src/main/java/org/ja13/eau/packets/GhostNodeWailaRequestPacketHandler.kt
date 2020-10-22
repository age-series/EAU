package org.ja13.eau.packets

import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import org.ja13.eau.EAU
import org.ja13.eau.misc.Coordonate
import org.ja13.eau.misc.Direction
import org.ja13.eau.node.NodeManager
import org.ja13.eau.node.six.SixNodeElement
import org.ja13.eau.node.transparent.TransparentNode
import net.minecraft.item.ItemStack

class GhostNodeWailaRequestPacketHandler : IMessageHandler<GhostNodeWailaRequestPacket, GhostNodeWailaResponsePacket> {
    override fun onMessage(message: GhostNodeWailaRequestPacket, ctx: MessageContext?): GhostNodeWailaResponsePacket {
        val realCoord = org.ja13.eau.EAU.ghostManager.getGhost(message.coord)?.observatorCoordonate
        var itemStack: ItemStack? = null
        var type: Byte = GhostNodeWailaResponsePacket.UNKNOWN_TYPE
        var realSide = Direction.XN

        if (realCoord != null) {
            val node = org.ja13.eau.node.NodeManager.instance.getNodeFromCoordonate(realCoord) as? org.ja13.eau.node.transparent.TransparentNode
            if (node != null) {
                itemStack = node.element.descriptor.newItemStack()
                type = GhostNodeWailaResponsePacket.TRANSPARENT_BLOCK_TYPE
            }

            val element = org.ja13.eau.EAU.ghostManager.getObserver(realCoord) as? org.ja13.eau.node.six.SixNodeElement
            if (element != null) {
                itemStack = element.sixNodeElementDescriptor.newItemStack()
                type = GhostNodeWailaResponsePacket.SIXNODE_TYPE
                realSide = element.side
            }
        }

        return GhostNodeWailaResponsePacket(message.coord, realCoord ?: Coordonate(0, 0, 0, 0), itemStack, type,
            realSide)
    }
}
