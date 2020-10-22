package org.ja13.eau.sixnode.electricalentitysensor;

import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;

public class ElectricalEntitySensorGui extends GuiContainerEln {

    ElectricalEntitySensorRender render;

    public ElectricalEntitySensorGui(EntityPlayer player, IInventory inventory, ElectricalEntitySensorRender render) {
        super(new ElectricalEntitySensorContainer(player, inventory));
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
