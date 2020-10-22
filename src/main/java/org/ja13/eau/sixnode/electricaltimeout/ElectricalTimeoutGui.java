package org.ja13.eau.sixnode.electricaltimeout;

import org.ja13.eau.gui.GuiHelper;
import org.ja13.eau.gui.GuiScreenEln;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.IGuiObject;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.gui.GuiHelper;
import org.ja13.eau.gui.GuiScreenEln;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;

import java.text.NumberFormat;
import java.text.ParseException;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalTimeoutGui extends GuiScreenEln {

    GuiButton set, reset;
    GuiTextFieldEln timeoutValue;
    ElectricalTimeoutRender render;

    public ElectricalTimeoutGui(EntityPlayer player, ElectricalTimeoutRender render) {
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();

        reset = newGuiButton(6, 6, 50, I18N.tr("Reset"));
        set = newGuiButton(6, 6 + 20 + 4, 50, I18N.tr("Set"));

        timeoutValue = newGuiTextField(6, 6 + 20 * 2 + 4 * 2, 50);

        timeoutValue.setText(render.timeoutValue);

        timeoutValue.setComment(I18N.tr("The time interval the\noutput is kept high.").split("\n"));
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        if (object == set) {
            render.clientSend(ElectricalTimeoutElement.setId);
        } else if (object == reset) {
            render.clientSend(ElectricalTimeoutElement.resetId);
        } else if (object == timeoutValue) {
            try {
                float value = NumberFormat.getInstance().parse(timeoutValue.getText()).floatValue();
                render.clientSetFloat(ElectricalTimeoutElement.setTimeOutValueId, value);
            } catch (ParseException e) {
            }
        }
    }

    @Override
    protected GuiHelper newHelper() {
        return new GuiHelper(this, 50 + 12, 6 + 20 * 2 + 4 * 2 + 12 + 6);
    }
}
