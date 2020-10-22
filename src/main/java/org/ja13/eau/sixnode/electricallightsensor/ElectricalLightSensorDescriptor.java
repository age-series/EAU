package org.ja13.eau.sixnode.electricallightsensor;

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
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalLightSensorDescriptor extends SixNodeDescriptor {

    private Obj3D.Obj3DPart main;
    public boolean dayLightOnly;
    public float[] pinDistance;

    Obj3D obj;

    public ElectricalLightSensorDescriptor(String name, Obj3D obj, boolean dayLightOnly) {
        super(name, ElectricalLightSensorElement.class, ElectricalLightSensorRender.class);
        this.obj = obj;
        this.dayLightOnly = dayLightOnly;

        if (obj != null) {
            main = obj.getPart("main");
            pinDistance = Utils.getSixNodePinDistance(main);
        }

        voltageTier = VoltageTier.TTL;
    }

    void draw() {
        if (main != null) main.draw();
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        if (dayLightOnly) {
            Collections.addAll(list, I18N.tr("Provides an electrical voltage\nwhich is proportional to\nthe intensity of daylight.").split("\n"));
            list.add(I18N.tr("0V at night, %1$V at noon.", Utils.plotValue(VoltageTier.TTL.getVoltage())));
        } else {
            Collections.addAll(list, I18N.tr("Provides an electrical voltage\nin the presence of light.").split("\n"));
        }
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
            GL11.glScalef(2f, 2f, 2f);
            draw();
        }
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).right();
    }
}
