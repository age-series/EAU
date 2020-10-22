package org.ja13.eau.simplenode.energyconverter;

import org.ja13.eau.gui.GuiHelper;
import org.ja13.eau.gui.GuiScreenEln;
import org.ja13.eau.gui.GuiVerticalTrackBar;
import org.ja13.eau.gui.IGuiObject;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.gui.GuiHelper;
import org.ja13.eau.gui.GuiScreenEln;
import org.ja13.eau.gui.GuiVerticalTrackBar;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;

import static org.ja13.eau.i18n.I18N.tr;

public class EnergyConverterElnToOtherGui extends GuiScreenEln {

    EnergyConverterElnToOtherEntity render;
    GuiVerticalTrackBar voltage;

    public EnergyConverterElnToOtherGui(EntityPlayer player, EnergyConverterElnToOtherEntity render) {
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();

        voltage = newGuiVerticalTrackBar(6, 6 + 2, 20, 50);
        voltage.setStepIdMax(100);
        voltage.setEnable(true);
        voltage.setRange(0f, 1f);

        syncVoltage();
    }

    public void syncVoltage() {
        voltage.setValue(render.inPowerFactor);
        render.hasChanges = false;
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        if (object == voltage) {
            render.sender.clientSendFloat(EnergyConverterElnToOtherNode.setInPowerFactor, voltage.getValue());
        }
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        if (render.hasChanges) syncVoltage();
        voltage.setComment(0, I18N.tr("Input power is limited to %1$W", (int) (voltage.getValue() * render.inPowerMax)));
    }

    @Override
    protected GuiHelper newHelper() {
        return new GuiHelper(this, 12 + 20, 12 + 50 + 4);
    }
}
