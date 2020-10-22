package org.ja13.eau.transparentnode.electricalfurnace;

import org.ja13.eau.gui.*;
import org.ja13.eau.item.HeatingCorpElement;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.sim.PhysicalConstant;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.GuiVerticalTrackBarHeat;
import org.ja13.eau.gui.GuiVerticalVoltageSupplyBar;
import org.ja13.eau.gui.HelperStdContainer;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.HeatingCorpElement;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.sim.PhysicalConstant;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalFurnaceGuiDraw extends GuiContainerEln {

    private final TransparentNodeElementInventory inventory;
    ElectricalFurnaceRender render;
    GuiButton buttonGrounded, autoShutDown;
    GuiVerticalTrackBarHeat vuMeterTemperature;

    GuiVerticalVoltageSupplyBar supplyBar;

    public ElectricalFurnaceGuiDraw(EntityPlayer player, IInventory inventory, ElectricalFurnaceRender render) {
        super(new ElectricalFurnaceContainer(null, player, inventory));
        this.inventory = (TransparentNodeElementInventory) inventory;
        this.render = render;
    }

    public void initGui() {
        super.initGui();
        autoShutDown = newGuiButton(6, 6, 99, "");
        buttonGrounded = newGuiButton(6 + 10 * 0, 6 + 20 + 4, 60 - 20, "");
        vuMeterTemperature = newGuiVerticalTrackBarHeat(167 - 20 - 20 - 8 - 4, 8, 20, 69);
        vuMeterTemperature.setStepIdMax(800 / 10);
        vuMeterTemperature.setEnable(true);
        vuMeterTemperature.setRange(0, 800);
        vuMeterTemperature.setComment(new String[]{I18N.tr("Temperature gauge")});
        syncVumeter();

        supplyBar = new GuiVerticalVoltageSupplyBar(167 - 20 - 2, 8, 20, 69, helper);
        add(supplyBar);
    }

    public void syncVumeter() {
        vuMeterTemperature.setValue(render.temperatureTargetSyncValue);
        render.temperatureTargetSyncNew = false;
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        if (render.getPowerOn())
            buttonGrounded.displayString = I18N.tr("Is on");
        else
            buttonGrounded.displayString = I18N.tr("Is off");

        if (render.autoShutDown) {
            buttonGrounded.enabled = false;
            autoShutDown.displayString = I18N.tr("Auto shutdown");
        } else {
            autoShutDown.displayString = I18N.tr("Manual shutdown");
            buttonGrounded.enabled = true;
        }

        if (render.temperatureTargetSyncNew) syncVumeter();
        vuMeterTemperature.temperatureHit = render.temperature;

        vuMeterTemperature.setComment(1, I18N.tr("Actual: %1$°C", Utils.plotValue(render.temperature + PhysicalConstant.Tamb)));
        vuMeterTemperature.setComment(2, I18N.tr("Set point: %1$°C", Utils.plotValue(vuMeterTemperature.getValue() + PhysicalConstant.Tamb)));
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        if (object == buttonGrounded) {
            render.clientSetPowerOn(!render.getPowerOn());
        } else if (object == autoShutDown) {
            render.clientSendId(ElectricalFurnaceElement.unserializeAutoShutDownId);
        } else if (object == vuMeterTemperature) {
            render.clientSetTemperatureTarget(vuMeterTemperature.getValue());
        }
    }

    @Override
    protected void postDraw(float f, int x, int y) {
        super.postDraw(f, x, y);
        helper.drawProcess(40, 57, render.processState);

        //drawString(8, 6, Utils.plotPower("Consummation", render.heatingCorpResistorP));

        ItemStack stack = render.inventory.getStackInSlot(ElectricalFurnaceElement.heatingCorpSlotId);
        if (stack == null) {
            supplyBar.setEnabled(false);
        } else {
            supplyBar.setEnabled(true);
            HeatingCorpElement desc = (HeatingCorpElement) HeatingCorpElement.getDescriptor(stack);
            supplyBar.setNominalU((float) desc.electricalNominalU);
        }
        supplyBar.setVoltage(render.voltage);
        supplyBar.setPower(render.heatingCorpResistorP);
    }

    @Override
    protected GuiHelperContainer newHelper() {
        return new HelperStdContainer(this);
    }
}
