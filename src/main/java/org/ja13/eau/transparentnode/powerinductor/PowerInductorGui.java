package org.ja13.eau.transparentnode.powerinductor;

import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;

import static org.ja13.eau.i18n.I18N.tr;


public class PowerInductorGui extends GuiContainerEln {


    private final TransparentNodeElementInventory inventory;
    PowerInductorRender render;


    public PowerInductorGui(EntityPlayer player, IInventory inventory, PowerInductorRender render) {
        super(new PowerInductorContainer(player, inventory));
        this.inventory = (TransparentNodeElementInventory) inventory;
        this.render = render;
    }

    public void initGui() {
        super.initGui();
    }


    @Override
    public void guiObjectEvent(IGuiObject object) {

        super.guiObjectEvent(object);

    }

    @Override
    protected void preDraw(float f, int x, int y) {

        super.preDraw(f, x, y);
    }

    @Override
    protected void postDraw(float f, int x, int y) {
        helper.drawString(8, 12, 0xFF000000, I18N.tr("Inductance: %1$H", Utils.plotValue(render.descriptor.getlValue(render.inventory))));
        super.postDraw(f, x, y);
    }

    @Override
    protected GuiHelperContainer newHelper() {

        return new GuiHelperContainer(this, 176, 166 - 54, 8, 84 - 54);
    }


}
