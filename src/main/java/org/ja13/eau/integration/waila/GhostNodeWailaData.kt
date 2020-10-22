package org.ja13.eau.integration.waila

import org.ja13.eau.misc.Coordonate
import org.ja13.eau.misc.Direction
import org.ja13.eau.packets.GhostNodeWailaResponsePacket
import net.minecraft.item.ItemStack

data class GhostNodeWailaData(val realCoord: Coordonate,
                              val itemStack: ItemStack?,
                              val realType: Byte = GhostNodeWailaResponsePacket.UNKNOWN_TYPE,
                              val realSide: Direction = Direction.XN)
