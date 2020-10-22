package org.ja13.eau.transparentnode.battery;

import org.ja13.eau.gui.*;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiButtonEln;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.GuiVerticalProgressBar;
import org.ja13.eau.gui.HelperStdContainer;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;

import static org.ja13.eau.i18n.I18N.tr;

public class BatteryGuiDraw extends GuiContainerEln {

    private final TransparentNodeElementInventory inventory;
    BatteryRender render;
    GuiButtonEln buttonGrounded;
    GuiVerticalProgressBar energyBar;

    public BatteryGuiDraw(EntityPlayer player, IInventory inventory, BatteryRender render) {
        super(new BatteryContainer(null, player, inventory));
        this.inventory = (TransparentNodeElementInventory) inventory;
        this.render = render;
    }

    public void initGui() {
        super.initGui();

        buttonGrounded = newGuiButton(8, 45, 100, "");
        buttonGrounded.visible = false;
        energyBar = newGuiVerticalProgressBar(167 - 16, 8, 16, 69);
        energyBar.setColor(0.2f, 0.5f, 0.8f);
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        if (buttonGrounded == object) {
            render.clientSetGrounded(!render.grounded);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int param1, int param2) {
        super.drawGuiContainerForegroundLayer(param1, param2);
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        buttonGrounded.displayString = I18N.tr("Grounded: " + render.grounded);
        energyBar.setValue((float) (render.energy / (render.descriptor.electricalStdEnergy * render.life)));
        energyBar.setComment(0, I18N.tr("Energy: %1$", Utils.plotPercent(energyBar.getValue(), "").replace(" ", "")));

    }

    @Override
    protected void postDraw(float f, int x, int y) {
        super.postDraw(f, x, y);
        String str1, str2 = "";

        double p = render.power;
        double energyMiss = render.descriptor.electricalStdEnergy * render.life - render.energy;

        if (Math.abs(p) < 5) {
            str1 = I18N.tr("No charge");
        } else if (p > 0) {
            str1 = I18N.tr("Discharge");
            str2 = Utils.plotTime(render.energy / p, "");
        } else if (energyMiss > 0) {
            str1 = I18N.tr("Charge");
            str2 = Utils.plotTime(-energyMiss / p, "");
        } else {
            str1 = I18N.tr("Charged");
        }

        int xDelta = 70;
        if (render.descriptor.lifeEnable) {
            drawString(8, 8, I18N.tr("Life:"));
            drawString(xDelta, 8, Utils.plotPercent( render.life, ""));
        }
        drawString(8, 17, I18N.tr("Energy:"));
        drawString(xDelta, 17,
            Utils.plotEnergy( render.energy, "J") + Utils.plotEnergy(render.descriptor.electricalStdEnergy * render.life, "J"));

        if (render.power >= 0)
            drawString(8, 26, I18N.tr("Power out:"));
        else
            drawString(8, 26, I18N.tr("Power in:"));
        drawString(xDelta, 26, Utils.plotPower(Math.abs(render.power), "") + Utils.plotPower(render.descriptor.electricalStdP, ""));

        drawString(8, 35, str1);
        drawString(xDelta, 35, str2);

        //   drawString(8, 44, "Thermal protection");
    }
    /*
        list.add("Nominal voltage : " + (int)(electricalU) + "V");
		list.add("Nominal power : " + (int)(electricalStdP) + "W");
     */

    @Override
    protected GuiHelperContainer newHelper() {
        return new HelperStdContainer(this);
    }
}
