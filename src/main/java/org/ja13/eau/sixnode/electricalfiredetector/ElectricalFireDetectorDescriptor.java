package org.ja13.eau.sixnode.electricalfiredetector;

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

public class ElectricalFireDetectorDescriptor extends SixNodeDescriptor {

    private Obj3D.Obj3DPart detector;
    private Obj3D.Obj3DPart led;
    boolean batteryPowered;
    double maxRange;
    public float[] pinDistance;
    final double updateInterval = 0.5;
    static final double PowerComsumption = 20000.0 / (3600 * 40);

    public ElectricalFireDetectorDescriptor(String name, Obj3D obj, double maxRange, boolean batteryPowered) {
        super(name, ElectricalFireDetectorElement.class, ElectricalFireDetectorRender.class);
        this.batteryPowered = batteryPowered;
        this.maxRange = maxRange;
        if (obj != null) {
            detector = obj.getPart("Detector");
            led = obj.getPart("Led");

            pinDistance = Utils.getSixNodePinDistance(detector);
        }

        if (batteryPowered) {
            voltageTier = VoltageTier.NEUTRAL;
        } else {
            voltageTier = VoltageTier.TTL;
        }
    }

    void draw(boolean firePresent) {
        if (detector != null) detector.draw();
        if (led != null) {
            if (firePresent) {
                UtilsClient.drawLight(led);
            } else {
                GL11.glColor3f(0.5f, 0.5f, 0.5f);
                led.draw();
                GL11.glColor3f(1, 1, 1);
            }
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        if (batteryPowered) {
            Collections.addAll(list, I18N.tr("Battery powered buzzer \nactivated in presence of fire.").split("\n"));
        } else {
            Collections.addAll(list, I18N.tr("Output voltage increases\nif a fire has been detected.").split("\n"));
        }
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
            draw(false);
        }
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).right();
    }
}
