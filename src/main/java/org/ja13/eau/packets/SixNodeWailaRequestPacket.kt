package org.ja13.eau.packets

import io.netty.buffer.ByteBuf
import org.ja13.eau.misc.Coordonate
import org.ja13.eau.misc.Direction

class SixNodeWailaRequestPacket : TransparentNodeRequestPacket {
    lateinit var side: Direction

    constructor()

    constructor(coord: Coordonate, side: Direction) : super(coord) {
        this.side = side
    }

    override fun fromBytes(buf: ByteBuf?) {
        super.fromBytes(buf)
        side = Direction.fromInt(buf?.readInt() ?: 0)!!
    }

    override fun toBytes(buf: ByteBuf?) {
        super.toBytes(buf)
        buf?.writeInt(side.int)
    }
}
