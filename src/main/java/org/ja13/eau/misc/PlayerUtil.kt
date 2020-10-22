package org.ja13.eau.misc

import org.ja13.eau.EAU
import net.minecraft.entity.player.EntityPlayer

fun EntityPlayer?.isHoldingMeter(): Boolean {
    if (this == null) return false
    val equippedItem = currentEquippedItem
    return (org.ja13.eau.EAU.multiMeterElement.checkSameItemStack(equippedItem)
        || org.ja13.eau.EAU.thermometerElement.checkSameItemStack(equippedItem)
        || org.ja13.eau.EAU.allMeterElement.checkSameItemStack(equippedItem))
}
