package org.ja13.eau.sixnode.electricalbreaker;

import org.ja13.eau.gui.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.HelperStdContainerSmall;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;

import java.text.NumberFormat;
import java.text.ParseException;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalBreakerGui extends GuiContainerEln {

    GuiButton toogleSwitch;
    GuiTextFieldEln setUmin, setUmax;
    ElectricalBreakerRender render;

    enum SelectedType {none, min, max}

    public ElectricalBreakerGui(EntityPlayer player, IInventory inventory, ElectricalBreakerRender render) {
        super(new ElectricalBreakerContainer(player, inventory));
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();

        setUmin = newGuiTextField(12, 58 / 2 + 3, 50);
        setUmax = newGuiTextField(12, 58 / 2 - 5 - 10, 50);

        setUmin.setText(render.uMin);
        setUmax.setText(render.uMax);

        setUmin.setComment(0, I18N.tr("Minimum voltage before cutting off"));
        setUmax.setComment(0, I18N.tr("Maximum voltage before cutting off"));

        toogleSwitch = newGuiButton(72 - 2, 58 / 2 - 10, 70, I18N.tr("Toggle switch"));
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        if (object == setUmax) {
            try {
                render.clientSetVoltageMax(NumberFormat.getInstance().parse(setUmax.getText()).floatValue());
            } catch (ParseException e) {
            }
        } else if (object == setUmin) {
            try {
                render.clientSetVoltageMin(NumberFormat.getInstance().parse(setUmin.getText()).floatValue());
            } catch (ParseException e) {
            }
        } else if (object == toogleSwitch) {
            render.clientToogleSwitch();
        }
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        if (!render.switchState)
            toogleSwitch.displayString = I18N.tr("Switch is off");
        else
            toogleSwitch.displayString = I18N.tr("Switch is on");
    }

    @Override
    protected GuiHelperContainer newHelper() {
        return new HelperStdContainerSmall(this);
    }
}
