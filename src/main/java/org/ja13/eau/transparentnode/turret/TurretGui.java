package org.ja13.eau.transparentnode.turret;

import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.GuiVerticalTrackBar;
import org.ja13.eau.gui.IGuiObject;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.gui.GuiContainerEln;
import org.ja13.eau.gui.GuiHelperContainer;
import org.ja13.eau.gui.GuiVerticalTrackBar;
import org.ja13.eau.gui.IGuiObject;
import org.ja13.eau.i18n.I18N;

import static org.ja13.eau.i18n.I18N.tr;

public class TurretGui extends GuiContainerEln {
    private GuiButton killOrSpareButton;
    private GuiVerticalTrackBar chargePower;
    private final TurretRender render;

    public TurretGui(EntityPlayer player, IInventory inventory, TurretRender render) {
        super(new TurretContainer(player, inventory));
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();

        killOrSpareButton = newGuiButton(11, 6, 100, "");

        chargePower = newGuiVerticalTrackBar(146, 8, 20, 35);
        chargePower.setStepId(49);
        chargePower.setStepIdMax(49);
        chargePower.setRange(100, 5000);
        chargePower.setComment(0, I18N.tr("Recharge power:"));
        chargePower.setValue((float)render.chargePower);
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);

        if (render.filterIsSpare)
            killOrSpareButton.displayString = I18N.tr("Do not attack:");
        else
            killOrSpareButton.displayString = I18N.tr("Attack:");

        chargePower.setComment(1, "" + chargePower.getValue() + "W");
    }

    @Override
    protected GuiHelperContainer newHelper() {
        return new GuiHelperContainer(this, 176, 186 - 52, 8, 104 - 52);
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);

        if (object == killOrSpareButton) {
            render.clientToggleFilterMeaning();
        } else if (object == chargePower) {
            render.clientSetChargePower(chargePower.getValue());
        }
    }
}

