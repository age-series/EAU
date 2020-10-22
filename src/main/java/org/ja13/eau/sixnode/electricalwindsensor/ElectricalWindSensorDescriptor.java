package org.ja13.eau.sixnode.electricalwindsensor;

import org.ja13.eau.misc.*;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalWindSensorDescriptor extends SixNodeDescriptor {

    private Obj3D.Obj3DPart baseWall, baseGround, anemometer;

    public float[] pinDistance;

    Obj3D obj;

    public double windMax;

    public ElectricalWindSensorDescriptor(
        String name,
        Obj3D obj,
        double windMax) {
        super(name, ElectricalWindSensorElement.class, ElectricalWindSensorRender.class);
        this.windMax = windMax;
        this.obj = obj;

        if (obj != null) {
            baseWall = obj.getPart("base_wall");
            baseGround = obj.getPart("base_ground");
            anemometer = obj.getPart("anemometer");

            pinDistance = Utils.getSixNodePinDistance(baseWall);
        }

        voltageTier = VoltageTier.TTL;
    }

    void draw(float alpha) {
        if (baseWall != null) baseWall.draw();
        if (anemometer != null) {
            UtilsClient.disableCulling();
            anemometer.draw(alpha, 0, 1, 0);
            UtilsClient.enableCulling();
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        Collections.addAll(list, I18N.tr("Provides an electrical signal\ndependant on wind speed.").split("\n"));
        list.add(I18N.tr("Maximum wind speed is %1$m/s", Utils.plotValue(windMax)));
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
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        } else {
            GL11.glRotatef(270, 1, 0, 0);
            GL11.glTranslatef(-0.6f, 0f, 0f);

            GL11.glScalef(2f, 2f, 2f);

            draw(0);
        }
    }

    @Override
    public boolean canBePlacedOnSide(EntityPlayer player, Direction side) {
        if (side.isY()) {
            Utils.addChatMessage(player, I18N.tr("You can't place this block on the floor or the ceiling"));
            return false;
        }
        return super.canBePlacedOnSide(player, side);
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).right();
    }
}
