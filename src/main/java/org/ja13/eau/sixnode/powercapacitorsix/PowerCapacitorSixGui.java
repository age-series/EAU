package org.ja13.eau.sixnode.powercapacitorsix;

import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.six.SixNodeElementInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.six.SixNodeElementInventory;

import static org.ja13.eau.i18n.I18N.tr;

public class PowerCapacitorSixGui extends GuiContainerEln {

    private final SixNodeElementInventory inventory;
    PowerCapacitorSixRender render;

    public PowerCapacitorSixGui(EntityPlayer player, IInventory inventory, PowerCapacitorSixRender render) {
        super(new PowerCapacitorSixContainer(player, inventory));
        this.inventory = (SixNodeElementInventory) inventory;
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
