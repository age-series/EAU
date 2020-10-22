package org.ja13.eau.sixnode.electricaltimeout;

import org.ja13.eau.misc.*;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalTimeoutDescriptor extends SixNodeDescriptor {

    Obj3D obj;
    Obj3D.Obj3DPart main, rot, led;
    float rotStart, rotEnd;

    String tickSound = null;
    float tickVolume = 0f;

    public ElectricalTimeoutDescriptor(String name, Obj3D obj) {
        super(name, ElectricalTimeoutElement.class, ElectricalTimeoutRender.class);
        if (obj != null) {
            main = obj.getPart("main");
            rot = obj.getPart("rot");
            if (rot != null) {
                rotStart = rot.getFloat("rotStart");
                rotEnd = rot.getFloat("rotEnd");
            }
            led = obj.getPart("led");
        }

        voltageTier = VoltageTier.TTL;
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        Collections.addAll(list, I18N.tr("Upon application of a high signal,\nthe timer maintains the output high for\na configurable interval. Can be re-triggered.").split("\n"));
    }

    void draw(float left) {
        if (main != null) main.draw();
        if (rot != null) {
            rot.draw(rotEnd + (rotStart - rotEnd) * left, 1f, 0f, 0f);
        }
        if (led != null) {
            UtilsClient.ledOnOffColor(left != 0f);
            UtilsClient.drawLight(led);
            GL11.glColor3f(1f, 1f, 1f);
        }
    }

    public ElectricalTimeoutDescriptor setTickSound(String tickSound, float tickVolume) {
        this.tickSound = tickSound;
        this.tickVolume = tickVolume;
        return this;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelperEln(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        }
        draw(1f);
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).right();
    }
}
