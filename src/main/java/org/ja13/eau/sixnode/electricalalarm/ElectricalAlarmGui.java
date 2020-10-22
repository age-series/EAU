package org.ja13.eau.sixnode.electricalalarm;

import org.ja13.eau.gui.GuiHelper;
import org.ja13.eau.gui.GuiScreenEln;
import org.ja13.eau.gui.IGuiObject;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.gui.GuiHelper;
import org.ja13.eau.gui.GuiScreenEln;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalAlarmGui extends GuiScreenEln {

    GuiButton toogleDefaultOutput;
    ElectricalAlarmRender render;

    public ElectricalAlarmGui(EntityPlayer player, ElectricalAlarmRender render) {
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();

        toogleDefaultOutput = newGuiButton(6, 32 / 2 - 10, 115, I18N.tr("Toggle switch"));
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        if (object == toogleDefaultOutput) {
            render.clientSend(ElectricalAlarmElement.clientSoundToggle);
        }
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        if (!render.mute)
            toogleDefaultOutput.displayString = I18N.tr("Sound is not muted");
        else
            toogleDefaultOutput.displayString = I18N.tr("Sound is muted");
    }

    @Override
    protected GuiHelper newHelper() {
        return new GuiHelper(this, 128, 32);
    }
}
