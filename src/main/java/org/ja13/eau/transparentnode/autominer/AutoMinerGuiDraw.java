package org.ja13.eau.transparentnode.autominer;

import org.ja13.eau.gui.GuiButtonEln;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.transparentnode.autominer.AutoMinerSlowProcess.jobType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiButtonEln;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;

import static org.ja13.eau.i18n.I18N.tr;

public class AutoMinerGuiDraw extends GuiContainerEln {
    private static final String SK_TOUCH = I18N.tr("Silk Touch");
    private final AutoMinerRender render;
    private GuiButtonEln silkTouch;

    public AutoMinerGuiDraw(EntityPlayer player, IInventory inventory, AutoMinerRender render) {
        super(new AutoMinerContainer(player, inventory));
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();
        silkTouch = newGuiButton(7, 6, 122, SK_TOUCH);
        silkTouch.setComment(0, I18N.tr("Halves speed, triples power draw"));
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        final String text = render.silkTouch ? I18N.tr("On") : I18N.tr("Off");
        silkTouch.displayString = String.format("%s %s", SK_TOUCH, text);
    }

    @Override
    protected void postDraw(float f, int x, int y) {
        if (render.job == AutoMinerSlowProcess.jobType.chestFull) {
            silkTouch.visible = false;
            String[] text = I18N.tr("Chest missing on the\nback of the auto miner!").split("\n");
            drawString(8, 7, text[0]);
            if (text.length >= 2) drawString(8, 7 + 9, text[1]);
        } else {
            silkTouch.visible = true;
        }
        super.postDraw(f, x, y);
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        if (object == silkTouch) {
            render.clientSendId(AutoMinerElement.toggleSilkTouch);
        }
        super.guiObjectEvent(object);
    }

    @Override
    protected GuiHelperContainer newHelper() {
        return new GuiHelperContainer(this, 176, 166 + 18 * 2 - 90, 8, 84 - 90 + 18 * 2);
    }
}
