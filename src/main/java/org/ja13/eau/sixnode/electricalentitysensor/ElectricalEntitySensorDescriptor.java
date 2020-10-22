package org.ja13.eau.sixnode.electricalentitysensor;

import org.ja13.eau.item.EntitySensorFilterDescriptor;
import org.ja13.eau.misc.*;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.item.EntitySensorFilterDescriptor;
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

public class ElectricalEntitySensorDescriptor extends SixNodeDescriptor {

    boolean useEntitySpeed = true;
    double speedFactor = 1 / 0.10;
    private Obj3D.Obj3DPart detector, haloMask;
    double maxRange;
    public float[] pinDistance;
    Obj3D obj;

    public ElectricalEntitySensorDescriptor(String name, Obj3D obj, double maxRange) {
        super(name, ElectricalEntitySensorElement.class, ElectricalEntitySensorRender.class);
        this.obj = obj;
        this.maxRange = maxRange;
        if (obj != null) {
            detector = obj.getPart("Detector");
            haloMask = obj.getPart("HaloMask");

            pinDistance = Utils.getSixNodePinDistance(detector);
        }

        voltageTier = VoltageTier.TTL;
    }

    void draw(boolean state, EntitySensorFilterDescriptor filter) {
        if (detector != null) detector.draw();
        if (state) {
            if (filter == null) {
                GL11.glColor3f(1f, 1f, 0f);
            } else {
                filter.glColor();
            }
            UtilsClient.drawLight(haloMask);
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        Collections.addAll(list, I18N.tr("Output voltage increases\nif entities are moving around.").split("\n"));
        list.add(I18N.tr("Range: %1$ blocks", (int) maxRange));
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
            draw(false, null);
        }
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).right();
    }
}
