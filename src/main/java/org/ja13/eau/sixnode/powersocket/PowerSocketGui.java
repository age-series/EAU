package org.ja13.eau.sixnode.powersocket;

import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.IGuiObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.GuiTextFieldEln;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;

import static org.ja13.eau.i18n.I18N.tr;

public class PowerSocketGui extends GuiContainerEln {

    final static int maxDeviceCount = 1;

    GuiTextFieldEln[] devices = new GuiTextFieldEln[3];
    private final PowerSocketRender render;

    public PowerSocketGui(PowerSocketRender render, EntityPlayer player, IInventory inventory) {
        super(new PowerSocketContainer(player, inventory));
        this.render = render;
    }

    @Override
    public void initGui() {
        //TODO!
        super.initGui();
        for (int idx = 0; idx < maxDeviceCount; idx++) {
            devices[idx] = newGuiTextField(8, 8 + idx * 16, 138);
            devices[idx].setText(render.channel);
            devices[idx].setComment(0, I18N.tr("Specify the device to supply through this socket."));
        }
    }

    @Override
    protected GuiHelperContainer newHelper() {
        //TODO!
        int contentHeight = maxDeviceCount * 16 + 12;
        return new GuiHelperContainer(this, 176, contentHeight + 84, 8, contentHeight);
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        //TODO!
        if (object == devices[0]) {
            render.clientSetString(PowerSocketElement.setChannelId, devices[0].getText());
        }
        super.guiObjectEvent(object);
    }
}
