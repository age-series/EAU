package org.ja13.eau.packets

import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import org.ja13.eau.misc.Coordonate
import org.ja13.eau.misc.Direction
import net.minecraft.item.ItemStack

class SixNodeWailaResponsePacket : TransparentNodeResponsePacket {
    lateinit var side: Direction
    var itemStack: ItemStack? = null

    constructor()

    constructor(coord: Coordonate, side: Direction, itemStack: ItemStack?, data: Map<String, String>) : super(data, coord) {
        this.side = side
        this.itemStack = itemStack
    }

    override fun fromBytes(buf: ByteBuf?) {
        super.fromBytes(buf)
        side = Direction.fromInt(buf?.readInt() ?: 0)!!
        itemStack = ByteBufUtils.readItemStack(buf)
    }

    override fun toBytes(buf: ByteBuf?) {
        super.toBytes(buf)
        buf?.writeInt(side.int)
        ByteBufUtils.writeItemStack(buf, itemStack)
    }
}
