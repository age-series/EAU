package org.ja13.eau.sixnode.electricalbreaker;

import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalBreakerDescriptor extends SixNodeDescriptor {

    private final Obj3D obj;
    private Obj3D.Obj3DPart main;
    private Obj3D.Obj3DPart lever;
    private Obj3D.Obj3DPart led;

    float alphaOff, alphaOn, speed;

    public ElectricalBreakerDescriptor(String name, Obj3D obj) {
        super(name, ElectricalBreakerElement.class, ElectricalBreakerRender.class);
        this.obj = obj;
        if (obj != null) {
            main = obj.getPart("case");
            lever = obj.getPart("lever");

            if (lever != null) {
                speed = lever.getFloat("speed");
                alphaOff = lever.getFloat("alphaOff");
                alphaOn = lever.getFloat("alphaOn");
            }
        }

        voltageTier = VoltageTier.NEUTRAL;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelperEln(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) /*GL11.glScalef(1.8f, 1.8f, 1.8f);*/ {
            super.renderItem(type, item, data);
        } else
            draw(0f, 0f);
    }

    public void draw(float on, float distance) {
        if (main != null) main.draw();
        if (lever != null) {
            lever.draw(on * (alphaOn - alphaOff) + alphaOff, 0, 1, 0);
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        Collections.addAll(list, (I18N.tr("Protects electrical components\nOpens contact if:\n  - Voltage exceeds a certain level\n  - Current exceeds the cable limit").split("\n")));
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).inverse();
    }
}
