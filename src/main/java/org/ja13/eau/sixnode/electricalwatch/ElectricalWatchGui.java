package org.ja13.eau.sixnode.electricalwatch;

import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;

public class ElectricalWatchGui extends GuiContainerEln {

    ElectricalWatchRender render;

    public ElectricalWatchGui(EntityPlayer player, IInventory inventory, ElectricalWatchRender render) {
        super(new ElectricalWatchContainer(player, inventory));
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected GuiHelperContainer newHelper() {
        return new GuiHelperContainer(this, 176, 166 - 52, 8, 84 - 52);
    }
}
