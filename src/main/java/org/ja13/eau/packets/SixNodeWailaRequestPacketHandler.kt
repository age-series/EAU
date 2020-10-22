package org.ja13.eau.packets

import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import org.ja13.eau.node.NodeManager
import org.ja13.eau.node.six.SixNode
import net.minecraft.item.ItemStack

class SixNodeWailaRequestPacketHandler : IMessageHandler<SixNodeWailaRequestPacket, SixNodeWailaResponsePacket> {
    override fun onMessage(message: SixNodeWailaRequestPacket, ctx: MessageContext?): SixNodeWailaResponsePacket {
        val coord = message.coord
        val side = message.side
        val node = org.ja13.eau.node.NodeManager.instance.getNodeFromCoordonate(coord) as? org.ja13.eau.node.six.SixNode
        var stringMap: Map<String, String> = emptyMap()
        var itemStack: ItemStack? = null
        if (node != null) {
            val element = node.getElement(side)
            if (element != null) {
                stringMap = element.waila?.filter { it.value != null } ?: emptyMap()
                itemStack = element.sixNodeElementDescriptor.newItemStack()
            }
        }
        return SixNodeWailaResponsePacket(coord, side, itemStack, stringMap)
    }
}
