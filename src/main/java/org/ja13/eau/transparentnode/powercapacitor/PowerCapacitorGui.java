package org.ja13.eau.transparentnode.powercapacitor;

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


public class PowerCapacitorGui extends GuiContainerEln {


    private final TransparentNodeElementInventory inventory;
    PowerCapacitorRender render;


    public PowerCapacitorGui(EntityPlayer player, IInventory inventory, PowerCapacitorRender render) {
        super(new PowerCapacitorContainer(player, inventory));
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
        helper.drawString(8, 8, 0xFF000000, I18N.tr("Capacity: %1$F", Utils.plotValue(render.descriptor.getCValue(render.inventory))));
        helper.drawString(8, 8 + 8 + 1, 0xFF000000, I18N.tr("Nominal voltage: %1$V", Utils.plotValue(render.descriptor.getUNominalValue(render.inventory))));
        super.postDraw(f, x, y);
    }

    @Override
    protected GuiHelperContainer newHelper() {

        return new GuiHelperContainer(this, 176, 166 - 54, 8, 84 - 54);
    }


}
