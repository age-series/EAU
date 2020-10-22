package org.ja13.eau.sixnode.electricalfiredetector

import org.ja13.eau.gui.GuiContainerEln
import org.ja13.eau.gui.GuiHelperContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory

class ElectricalFireDetectorGui(player: EntityPlayer, inventory: IInventory, var render: ElectricalFireDetectorRender)
    : org.ja13.eau.gui.GuiContainerEln(ElectricalFireDetectorContainer(player, inventory)) {
    override fun newHelper(): org.ja13.eau.gui.GuiHelperContainer = org.ja13.eau.gui.GuiHelperContainer(this, 176, 166 - 52, 8, 84 - 52)
}
