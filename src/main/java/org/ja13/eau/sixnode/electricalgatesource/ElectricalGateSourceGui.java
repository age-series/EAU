package org.ja13.eau.sixnode.electricalgatesource;

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

public class ElectricalGateSourceGui extends GuiScreenEln {

    ElectricalGateSourceRender render;
    GuiVerticalTrackBar voltage;

    public ElectricalGateSourceGui(EntityPlayer player, ElectricalGateSourceRender render) {
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();

        voltage = newGuiVerticalTrackBar(6, 6 + 2, 20, 50);
        voltage.setStepIdMax(100);
        voltage.setEnable(true);
        voltage.setRange(0f, 5f);

        syncVoltage();
    }

    public void syncVoltage() {
        voltage.setValue(render.voltageSyncValue);
        render.voltageSyncNew = false;
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        if (object == voltage) {
            render.clientSetFloat(ElectricalGateSourceElement.setVoltagerId, voltage.getValue());
        }
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        if (render.voltageSyncNew) syncVoltage();
        voltage.setComment(0, I18N.tr("Output at %1$%", ((int) voltage.getValue() * 2)));
    }

    @Override
    protected GuiHelper newHelper() {
        return new GuiHelper(this, 12 + 20, 12 + 50 + 4);
    }
}
