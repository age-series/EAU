package org.ja13.eau.sixnode.electricaldatalogger;

import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.GuiTextFieldEln.GuiTextFieldElnObserver;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.misc.Color;
import org.ja13.eau.misc.UtilsClient;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Color;
import org.ja13.eau.misc.UtilsClient;
import org.lwjgl.opengl.GL11;

import java.text.NumberFormat;
import java.text.ParseException;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalDataLoggerGui extends GuiContainerEln implements GuiTextFieldEln.GuiTextFieldElnObserver {

    GuiButton resetBt, voltageType, energyType, currentType, powerType, celsiusType, percentType, noType, config, printBt, pause;
    GuiTextFieldEln samplingPeriod, maxValue, minValue, yCursorValue;
    ElectricalDataLoggerRender render;

    enum State {display, config}

    State state = State.display;

    public ElectricalDataLoggerGui(EntityPlayer player, IInventory inventory, ElectricalDataLoggerRender render) {
        super(new ElectricalDataLoggerContainer(player, inventory));
        this.render = render;
    }

    void displayEntry() {
        config.displayString = I18N.tr("Configuration");
        config.visible = true;
        pause.visible = true;
        resetBt.visible = true;
        voltageType.visible = false;
        energyType.visible = false;
        percentType.visible = false;
        noType.visible = false;
        currentType.visible = false;
        powerType.visible = false;
        celsiusType.visible = false;
        samplingPeriod.setVisible(false);
        maxValue.setVisible(false);
        minValue.setVisible(false);
        printBt.visible = true;
        state = State.display;
    }

    void configEntry() {
        pause.visible = false;
        config.visible = true;
        config.displayString = I18N.tr("Back to display");
        resetBt.visible = false;
        printBt.visible = true;
        voltageType.visible = true;
        energyType.visible = true;
        percentType.visible = true;
        noType.visible = true;
        currentType.visible = true;
        powerType.visible = true;
        celsiusType.visible = true;
        samplingPeriod.setVisible(true);
        maxValue.setVisible(true);
        minValue.setVisible(true);
        state = State.config;
    }

    @Override
    public void initGui() {
        super.initGui();

        config = newGuiButton(176 / 2 - 50, 8 - 2, 100, "");

        //@devs: Do not translate the following elements. Please.
        voltageType = newGuiButton(176 / 2 - 75 - 2, 8 + 20 + 2 - 2, 75, I18N.tr("Voltage [V]"));
        currentType = newGuiButton(176 / 2 + 2, 8 + 20 + 2 - 2, 75, I18N.tr("Current [A]"));
        powerType = newGuiButton(176 / 2 - 75 - 2, 8 + 40 + 4 - 2, 75, I18N.tr("Power [W]"));
        celsiusType = newGuiButton(176 / 2 + 2, 8 + 40 + 4 - 2, 75, I18N.tr("Temp. [*C]"));
        percentType = newGuiButton(176 / 2 - 75 - 2, 8 + 60 + 6 - 2, 75, I18N.tr("Percent [-]%"));
        energyType = newGuiButton(176 / 2 + 2, 8 + 60 + 6 - 2, 75, I18N.tr("Energy [J]"));
        noType = newGuiButton(176 / 2 - 75 / 2 - 2, 8 + 80 + 8 - 2, 75, I18N.tr("Unit"));

        resetBt = newGuiButton(176 / 2 - 50, 8 + 20 + 2 - 2, 48, I18N.tr("Reset"));
        pause = newGuiButton(176 / 2 + 2, 8 + 20 + 2 - 2, 48, "");

        printBt = newGuiButton(176 / 2 - 48 / 2, 146, 48, I18N.tr("Print"));

        samplingPeriod = newGuiTextField(30, 124, 50);
        samplingPeriod.setText(render.log.samplingPeriod);
        samplingPeriod.setComment(new String[]{I18N.tr("Sampling period")});

        maxValue = newGuiTextField(176 - 50 - 30, 124 - 7, 50);
        maxValue.setText(render.log.maxValue);
        maxValue.setComment(new String[]{I18N.tr("Y-axis max")});

        minValue = newGuiTextField(176 - 50 - 30, 124 + 8, 50);
        minValue.setText(render.log.minValue);
        minValue.setComment(new String[]{I18N.tr("Y-axis min")});

        displayEntry();
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        try {
            if (object == resetBt) {
                render.clientSend(ElectricalDataLoggerElement.resetId);
            } else if (object == pause) {
                render.clientSend(ElectricalDataLoggerElement.tooglePauseId);
            } else if (object == printBt) {
                render.clientSend(ElectricalDataLoggerElement.printId);
            } else if (object == currentType) {
                render.clientSetByte(ElectricalDataLoggerElement.setUnitId, DataLogs.currentType);
            } else if (object == voltageType) {
                render.clientSetByte(ElectricalDataLoggerElement.setUnitId, DataLogs.voltageType);
            } else if (object == energyType) {
                render.clientSetByte(ElectricalDataLoggerElement.setUnitId, DataLogs.energyType);
            } else if (object == percentType) {
                render.clientSetByte(ElectricalDataLoggerElement.setUnitId, DataLogs.percentType);
            } else if(object == noType) {
                render.clientSetByte(ElectricalDataLoggerElement.setUnitId, DataLogs.noType);
            } else if (object == powerType) {
                render.clientSetByte(ElectricalDataLoggerElement.setUnitId, DataLogs.powerType);
            } else if (object == celsiusType) {
                render.clientSetByte(ElectricalDataLoggerElement.setUnitId, DataLogs.celsiusType);
            } else if (object == config) {
                switch (state) {
                    case config:
                        displayEntry();
                        break;
                    case display:
                        configEntry();
                        break;
                    default:
                        break;
                }
            } else if (object == maxValue) {
                render.clientSetFloat(ElectricalDataLoggerElement.setMaxValue, NumberFormat.getInstance().parse(maxValue.getText()).floatValue());
            } else if (object == minValue) {
                render.clientSetFloat(ElectricalDataLoggerElement.setMinValue, NumberFormat.getInstance().parse(minValue.getText()).floatValue());
            } else if (object == samplingPeriod) {
                float value = NumberFormat.getInstance().parse(samplingPeriod.getText()).floatValue();
                if (value < 0.05f) value = 0.05f;
                samplingPeriod.setText(value);

                render.clientSetFloat(ElectricalDataLoggerElement.setSamplingPeriodeId, value);
            }
        } catch (ParseException e) {
        }
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        powerType.enabled = true;
        currentType.enabled = true;
        voltageType.enabled = true;
        celsiusType.enabled = true;
        percentType.enabled = true;
        energyType.enabled = true;

        switch (render.log.unitType) {
            case DataLogs.currentType:
                currentType.enabled = false;
                break;
            case DataLogs.voltageType:
                voltageType.enabled = false;
                break;
            case DataLogs.powerType:
                powerType.enabled = false;
                break;
            case DataLogs.celsiusType:
                celsiusType.enabled = false;
                break;
            case DataLogs.percentType:
                percentType.enabled = false;
                break;
            case DataLogs.energyType:
                energyType.enabled = false;
                break;
            case DataLogs.noType:
                noType.enabled = false;
                break;
        }

        if (render.pause)
            pause.displayString = Color.COLOR_DARK_YELLOW + "Paused";
        else
            pause.displayString = Color.COLOR_BRIGHT_GREEN + "Running";

        boolean a = inventorySlots.getSlot(ElectricalDataLoggerContainer.paperSlotId).getStack() != null;
        boolean b = inventorySlots.getSlot(ElectricalDataLoggerContainer.printSlotId).getStack() == null;
        printBt.enabled = a && b;
    }

    @Override
    protected void postDraw(float f, int x, int y) {
        super.postDraw(f, x, y);
        final float bckrndMargin = 0.05f;

        if (state == State.display) {

            GL11.glPushMatrix();
            GL11.glTranslatef(guiLeft + 8, guiTop + 53, 0);
            GL11.glScalef(50, 50, 1f);

            GL11.glColor4f(0.15f, 0.15f, 0.15f, 1.0f);
            UtilsClient.disableTexture();
            UtilsClient.disableCulling();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(-bckrndMargin, -bckrndMargin);
            GL11.glVertex2f(3.2f + bckrndMargin, -bckrndMargin);
            GL11.glVertex2f(3.2f + bckrndMargin, 1.6f + 3 * bckrndMargin);
            GL11.glVertex2f(-bckrndMargin, 1.6f + 3 * bckrndMargin);
            GL11.glEnd();
            UtilsClient.enableCulling();
            UtilsClient.enableTexture();

            GL11.glColor4f(render.descriptor.cr, render.descriptor.cg, render.descriptor.cb, 1);
            render.log.draw(2.9f, 1.6f, render.descriptor.textColor);
            GL11.glPopMatrix();
        }
    }

    @Override
    protected GuiHelperContainer newHelper() {
        return new GuiHelperContainer(this, 176, 253, 8, 171);
    }
}
