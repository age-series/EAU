package org.ja13.eau.sixnode.electricalweathersensor;

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

public class ElectricalWeatherSensorDescriptor extends SixNodeDescriptor {

    private Obj3D.Obj3DPart main;
    public float[] pinDistance;

    Obj3D obj;

    public ElectricalWeatherSensorDescriptor(String name, Obj3D obj) {
        super(name, ElectricalWeatherSensorElement.class, ElectricalWeatherSensorRender.class);
        this.obj = obj;

        if (obj != null) {
            main = obj.getPart("main");

            pinDistance = Utils.getSixNodePinDistance(main);
        }

        voltageTier = VoltageTier.TTL;
    }

    void draw() {
        UtilsClient.disableCulling();
        if (main != null) main.draw();
        UtilsClient.enableCulling();
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        Collections.addAll(list, I18N.tr("Provides an electrical signal\ndepending the actual weather.").split("\n"));
        list.add(I18N.tr("Clear: %1$V", 0));
        list.add(I18N.tr("Rain: %1$V", Utils.plotValue(VoltageTier.TTL.getVoltage() / 2)));
        list.add(I18N.tr("Storm: %1$V", Utils.plotValue(VoltageTier.TTL.getVoltage())));
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
