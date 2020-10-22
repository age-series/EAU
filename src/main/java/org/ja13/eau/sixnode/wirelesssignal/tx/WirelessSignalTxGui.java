package org.ja13.eau.sixnode.wirelesssignal.tx;

import org.ja13.eau.gui.GuiHelper;
import org.ja13.eau.gui.GuiScreenEln;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.gui.GuiHelper;
import org.ja13.eau.gui.GuiScreenEln;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;

import static org.ja13.eau.i18n.I18N.tr;

public class WirelessSignalTxGui extends GuiScreenEln {

    GuiTextFieldEln channel;
    private final WirelessSignalTxRender render;

    public WirelessSignalTxGui(WirelessSignalTxRender render) {
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();
        channel = newGuiTextField(6, 6, 220);
        channel.setText(render.channel);
        channel.setComment(0, I18N.tr("Specify the channel"));
    }

    @Override
    protected GuiHelper newHelper() {
        return new GuiHelper(this, 220 + 12, 12 + 12);
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        if (object == channel) {
            render.clientSetString(WirelessSignalTxElement.setChannelId, channel.getText());
        }
        super.guiObjectEvent(object);
    }
}
