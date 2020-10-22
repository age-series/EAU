package org.ja13.eau.sixnode.energymeter;

import org.ja13.eau.gui.*;
import org.ja13.eau.sixnode.energymeter.EnergyMeterElement.Mod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiButtonEln;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;

import java.text.NumberFormat;
import java.text.ParseException;

import static org.ja13.eau.i18n.I18N.tr;

public class EnergyMeterGui extends GuiContainerEln {

    GuiButtonEln stateBt, passwordBt, modBt, setEnergyBt, resetTimeBt, energyUnitBt, timeUnitBt;
    GuiTextFieldEln passwordFeild, energyFeild;
    EnergyMeterRender render;

    enum SelectedType {none, min, max}

    boolean isLogged;

    public EnergyMeterGui(EntityPlayer player, IInventory inventory, EnergyMeterRender render) {
        super(new EnergyMeterContainer(player, inventory));
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();
        int x = 6, y = 6;

        isLogged = render.password.equals("");
        passwordFeild = newGuiTextField(x, y + 4, 70);
        x += 74;
        passwordBt = newGuiButton(x, y, 106, "");
        passwordFeild.setComment(0, I18N.tr("Enter password"));

        x = 6;
        y += 28;
        x = 6;
        stateBt = newGuiButton(x, y, 70, "");
        x += 74;

        modBt = newGuiButton(x, y, 106, "");
        y += 22;
        x = 6;
        energyFeild = newGuiTextField(x, y + 4, 70);
        x += 74;
        setEnergyBt = newGuiButton(x, y, 106, I18N.tr("Set energy counter"));
        energyFeild.setComment(0, I18N.tr("Enter new energy"));
        energyFeild.setComment(1, I18N.tr("value in kJ"));
        energyFeild.setText("0");

        y += 22;
        x = 6;
        energyUnitBt = newGuiButton(x, y, 34, "");
        x += 34 + 2;
        timeUnitBt = newGuiButton(x, y, 34, "");
        x += 34 + 4;
        resetTimeBt = newGuiButton(x, y, 106, I18N.tr("Reset time counter"));
        y += 22;
        x = 6;

        if (render.descriptor.timeNumberWheel.length == 0) {
            energyUnitBt.enabled = false;
            timeUnitBt.enabled = false;
        }
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);

        if (object == stateBt) {
            render.clientSend(EnergyMeterElement.clientToggleStateId);
        }
        if (object == passwordBt) {
            if (isLogged) {
                render.clientSetString(EnergyMeterElement.clientPasswordId, passwordFeild.getText());
            } else {
                if (passwordFeild.getText().equals(render.password)) {
                    isLogged = true;
                }
            }
        }

        if (object == modBt) {
            switch (render.mod) {
                case ModCounter:
                    render.clientSetString(EnergyMeterElement.clientModId, EnergyMeterElement.Mod.ModPrepay.name());
                    break;
                case ModPrepay:
                    render.clientSetString(EnergyMeterElement.clientModId, EnergyMeterElement.Mod.ModCounter.name());
                    break;
            }
        }

        if (object == setEnergyBt) {
            double newVoltage;
            try {
                newVoltage = NumberFormat.getInstance().parse(energyFeild.getText()).doubleValue();
            } catch (ParseException e) {
                return;
            }

            render.clientSetDouble(EnergyMeterElement.clientEnergyStackId, newVoltage * 1000);
        }

        if (object == resetTimeBt) {
            render.clientSend(EnergyMeterElement.clientTimeCounterId);
        }
        if (object == energyUnitBt) {
            render.clientSend(EnergyMeterElement.clientEnergyUnitId);
        }
        if (object == timeUnitBt) {
            render.clientSend(EnergyMeterElement.clientTimeUnitId);
        }
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        if (!render.switchState)
            stateBt.displayString = I18N.tr("is off");
        else
            stateBt.displayString = I18N.tr("is on");

        if (isLogged)
            passwordBt.displayString = I18N.tr("Change password");
        else
            passwordBt.displayString = I18N.tr("Try password");

        switch (render.mod) {
            case ModCounter:
                modBt.displayString = I18N.tr("Counter Mode");

                modBt.clearComment();
                int lineNumber = 0;
                for (String line : I18N.tr("Counts the energy conducted from\n\u00a74red\u00a7f to \u00a71blue\u00a7f.").split("\n"))
                    modBt.setComment(lineNumber++, line);
                break;
            case ModPrepay:
                modBt.displayString = I18N.tr("Prepay Mode");

                modBt.clearComment();
                lineNumber = 0;
                for (String line : I18N.tr("Counts the energy conducted from\n\u00a74red\u00a7f to \u00a71blue\u00a7f.").split("\n"))
                    modBt.setComment(lineNumber++, line);
                modBt.setComment(lineNumber++, "");
                for (String line : I18N.tr("You can set an initial\namount of available energy.\nWhen the counter arrives at 0\nthe contact will be opened.").split("\n"))
                    modBt.setComment(lineNumber++, line);

                break;
        }

        if (energyUnitBt != null)
            switch (render.energyUnit) {
                case 0:
                    energyUnitBt.displayString = "J";
                    break;
                case 1:
                    energyUnitBt.displayString = "KJ";
                    break;
                case 2:
                    energyUnitBt.displayString = "MJ";
                    break;
                case 3:
                    energyUnitBt.displayString = "GJ";
                    break;
                default:
                    energyUnitBt.displayString = "??";
                    break;
            }

        if (timeUnitBt != null)
            switch (render.timeUnit) {
                case 0:
                    timeUnitBt.displayString = "H";
                    break;
                case 1:
                    timeUnitBt.displayString = "D";
                    break;
                default:
                    timeUnitBt.displayString = "??";
                    break;
            }
        modBt.enabled = isLogged;
        stateBt.enabled = isLogged;
        resetTimeBt.enabled = isLogged;
        setEnergyBt.enabled = isLogged;
        energyUnitBt.enabled = isLogged && render.descriptor.timeNumberWheel.length != 0;
        timeUnitBt.enabled = isLogged && render.descriptor.timeNumberWheel.length != 0;
    }

    @Override
    protected void postDraw(float f, int x, int y) {
        super.postDraw(f, x, y);
        helper.drawRect(6, 29, helper.xSize - 6, 29 + 1, 0xff404040);

        y = 101;
        helper.drawRect(6, y, helper.xSize - 6, y + 1, 0xff404040);

        y += 3;
        helper.drawString(6 + 16 / 2, y, 0xff000000, I18N.tr("Energy counter: %1$J", (int) (render.energyStack)));
        y += 10;
        helper.drawString(6 + 16 / 2, y, 0xff000000, I18N.tr("Time counter:", (int) (render.timerCouter)));
    }

    @Override
    protected GuiHelperContainer newHelper() {
        return new GuiHelperContainer(this, 176 + 16, 42 + 166, 8 + 16 / 2, 42 + 84);
    }
}
