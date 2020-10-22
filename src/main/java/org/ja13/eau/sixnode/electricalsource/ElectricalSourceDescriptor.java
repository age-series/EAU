package org.ja13.eau.sixnode.electricalsource;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.misc.VoltageTierHelpers;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

public class ElectricalSourceDescriptor extends SixNodeDescriptor {

    private final Obj3D obj;
    private Obj3D.Obj3DPart main;
    private Obj3D.Obj3DPart led;
    private boolean signalSource = false;

    public ElectricalSourceDescriptor(String name, Obj3D obj, boolean signalSource) {
        super(name, ElectricalSourceElement.class, ElectricalSourceRender.class);
        this.obj = obj;
        if (obj != null) {
            main = obj.getPart("main");
            led = obj.getPart("led");
        }
        this.signalSource = signalSource;

        if (signalSource) {
          voltageTier = VoltageTier.TTL;
        } else {
            voltageTier = VoltageTier.NEUTRAL;
        }
    }

    public boolean isSignalSource() {
        return signalSource;
    }

    void draw(boolean ledOn) {
        if (main != null) main.draw();
        if (led != null) {
            if (ledOn)
                UtilsClient.drawLight(led);
            else {
                GL11.glPushMatrix();
                GL11.glColor3f(0.1f, 0.1f, 0.1f);
                led.draw();
                GL11.glPopMatrix();
            }
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        Collections.addAll(list, I18N.tr("Provides an ideal voltage source\nwithout energy or power limitation.").split("\n"));
        list.add("");
        list.add(I18N.tr("Internal resistance: %1$\u2126", Utils.plotValue(EAU.smallInsulationLowCurrentCopperCable.electricalRs)));
        list.add("");
        list.add(I18N.tr("Creative block."));
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        switch (type) {
            case ENTITY:
                draw(false);
                break;

            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
                GL11.glPushMatrix();
                GL11.glTranslatef(0.8f, 0.3f, 0.2f);
                GL11.glRotatef(150, 0, 0, 1);
                draw(false);
                GL11.glPopMatrix();
                break;

            case INVENTORY:
            case FIRST_PERSON_MAP:
                if (signalSource) {
                    VoltageTierHelpers.Companion.drawIconBackground(type, VoltageTier.TTL);
                }
                super.renderItem(type, item, data);
                break;
        }
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        if (signalSource) {
            return super.getFrontFromPlace(side, player).left();
        } else {
            return super.getFrontFromPlace(side, player);
        }
    }
}
