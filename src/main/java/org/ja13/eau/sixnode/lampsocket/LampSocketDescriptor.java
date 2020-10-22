package org.ja13.eau.sixnode.lampsocket;

import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class LampSocketDescriptor extends SixNodeDescriptor {

    public LampSocketType socketType;
    LampSocketObjRender render;

    public boolean cameraOpt = true;

    public int range;
    public String modelName;
    float alphaZMin, alphaZMax, alphaZBoot;
    public boolean cableFront = true;
    public boolean cableLeft = true;
    public boolean cableRight = true;
    public boolean cableBack = true;

    public float initialRotateDeg = 0.f;
    public boolean rotateOnlyBy180Deg = false;

    public boolean paintable = false;
    public boolean renderIconInHand = false;

    public LampSocketDescriptor(String name, LampSocketObjRender render,
                                LampSocketType socketType,
                                boolean paintable,
                                int range,
                                float alphaZMin, float alphaZMax,
                                float alphaZBoot) {
        super(name, LampSocketElement.class, LampSocketRender.class);
        this.socketType = socketType;
        this.paintable = paintable;
        this.range = range;
        this.alphaZMin = alphaZMin;
        this.alphaZMax = alphaZMax;
        this.alphaZBoot = alphaZBoot;
        this.render = render;

        voltageTier = VoltageTier.NEUTRAL;
    }

    public void setInitialOrientation(float rotateDeg) {
        this.initialRotateDeg = rotateDeg;
    }

    public void setUserRotationLibertyDegrees(boolean only180) {
        this.rotateOnlyBy180Deg = only180;
    }

    boolean noCameraOpt() {
        return cameraOpt;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return !renderIconInHand && type != ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelperEln(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return !renderIconInHand && type != ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY || renderIconInHand)
            super.renderItem(type, item, data);
        else {
            GL11.glScalef(1.25f, 1.25f, 1.25f);
            render.draw(this, type, 0.f);
        }
    }

    @Override
    public boolean hasVolume() {
        return hasGhostGroup();
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        //list.add("Socket Type : " + socketType.toString());

        if (range != 0 || alphaZMin != alphaZMax) {
            //list.add("Projector");
            if (range != 0) {
                list.add(I18N.tr("Spot range: %1$ blocks", range));
            }
            if (alphaZMin != alphaZMax) {
                list.add(I18N.tr("Angle: %1$° to %2$°", ((int) alphaZMin), ((int) alphaZMax)));
            }
        }
    }
}
