package org.ja13.eau.sixnode.lampsupply;

import org.ja13.eau.misc.*;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.client.Minecraft;
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

import java.util.Collections;
import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class LampSupplyDescriptor extends SixNodeDescriptor {

    private final Obj3D obj;
    Obj3D.Obj3DPart base;
    private Obj3D.Obj3DPart window;
    private float windowOpenAngle;
    public boolean isWireless;
    public int range;
    public int channelCount = 3;

    public LampSupplyDescriptor(String name, Obj3D obj, int range) {
        super(name, LampSupplyElement.class, LampSupplyRender.class);
        this.isWireless = isWireless;
        this.range = range;
        this.obj = obj;
        if (obj != null) {
            base = obj.getPart("base");
            window = obj.getPart("window");
        }
        if (window != null) {
            windowOpenAngle = window.getFloat("windowOpenAngle");
        }

        voltageTier = VoltageTier.NEUTRAL;
    }

    public void draw(float openFactor) {
        if (base != null) base.draw();
        //UtilsClient.drawLight(led);
        UtilsClient.disableCulling();
        //UtilsClient.disableDepthTest();
        UtilsClient.enableBlend();
        obj.bindTexture("Glass.png");
        float rotYaw = Minecraft.getMinecraft().thePlayer.rotationYaw / 360.f;
        float rotPitch = Minecraft.getMinecraft().thePlayer.rotationPitch / 180.f;
        float pos = (((float) Minecraft.getMinecraft().thePlayer.posX) + ((float) Minecraft.getMinecraft().thePlayer.posZ)) / 64.f;
        if (window != null)
            window.draw((1f - openFactor) * windowOpenAngle, 0f, 0f, 1f, rotYaw + pos + (openFactor * 0.5f), rotPitch * 0.65f);
        UtilsClient.disableBlend();
        //UtilsClient.enableDepthTest();
        UtilsClient.enableCulling();
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
        } else {
            draw(1f);
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        list.add(I18N.tr("Supplies power to nearby lamps."));
        list.add(I18N.tr("Capable of operating 3 light channels."));
        Collections.addAll(list, I18N.tr("Supports control from a wireless signal\nchannel for each lighting channel.").split("\n"));
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).inverse();
    }
}
