package org.ja13.eau.sixnode.batterycharger;

import org.ja13.eau.gui.GuiButtonEln;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.IGuiObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiButtonEln;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;

import static org.ja13.eau.i18n.I18N.tr;

public class BatteryChargerGui extends GuiContainerEln {

    private final BatteryChargerRender render;

    GuiButtonEln powerOn;

    public BatteryChargerGui(BatteryChargerRender render, EntityPlayer player, IInventory inventory) {
        super(new BatteryChargerContainer(player, inventory));
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();
        powerOn = newGuiButton(97 + 10, 6 + 17 - 10, 40, "");
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);

        if (render.powerOn) {
            powerOn.displayString = I18N.tr("Is on");
        } else {
            powerOn.displayString = I18N.tr("Is off");
        }
    }

    @Override
    protected GuiHelperContainer newHelper() {
        return new GuiHelperContainer(this, 176, 166 - 40, 8, 84 - 40);
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        if (object == powerOn) {
            render.clientSend(BatteryChargerElement.toogleCharge);
        }
        super.guiObjectEvent(object);
    }
}
