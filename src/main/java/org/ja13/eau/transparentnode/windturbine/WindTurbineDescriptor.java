package org.ja13.eau.transparentnode.windturbine;

import org.ja13.eau.ghost.GhostGroup;
import org.ja13.eau.misc.*;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.ghost.GhostGroup;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.FunctionTable;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class WindTurbineDescriptor extends TransparentNodeDescriptor {
    private final Obj3D obj;
    private final Obj3D.Obj3DPart main;
    private final Obj3D.Obj3DPart rot;
    private final Obj3D.Obj3DPart halo;

    public final GenericCableDescriptor cable;

    final double nominalPower;
    final double maxVoltage;
    private final double maxWind;
    final int offY;
    final int rayX;
    final int rayY;
    final int rayZ;
    int blockMalusSubCount;
    final double blockMalus;
    final String soundName;
    final float nominalVolume;
    final FunctionTable PfW;
    double speed;

    public WindTurbineDescriptor(String name, Obj3D obj, GenericCableDescriptor cable, FunctionTable PfW,
                                 double nominalPower, double nominalWind, double maxVoltage, double maxWind, int offY,
                                 int rayX, int rayY, int rayZ, int blockMalusMinCount, double blockMalus,
                                 String soundName, float nominalVolume) {
        super(name, WindTurbineElement.class, WindTurbineRender.class);

        this.cable = cable;
        this.nominalPower = nominalPower;
        this.maxVoltage = maxVoltage;
        this.maxWind = maxWind;
        this.offY = offY;
        this.rayX = rayX;
        this.rayY = rayY;
        this.rayZ = rayZ;
        this.blockMalusSubCount = blockMalusMinCount + 1;
        this.blockMalus = blockMalus;
        this.soundName = soundName;
        this.nominalVolume = nominalVolume;
        this.PfW = PfW.duplicate(nominalWind, nominalPower);
        this.obj = obj;
        if (obj != null) {
            main = obj.getPart("main");
            rot = obj.getPart("rot");
            halo = obj.getPart("halo");
            if (rot != null) {
                speed = rot.getFloat("speed");
            }
        } else {
            main = null;
            rot = null;
            halo = null;
        }

        voltageTier = VoltageTier.HIGH_HOUSEHOLD;
    }

    public void setGhostGroup(GhostGroup ghostGroup) {
        blockMalusSubCount += ghostGroup.size();
        this.ghostGroup = ghostGroup;
    }

    public void draw(float alpha, boolean haloState) {
        if (main != null) main.draw();
        if (rot != null) rot.draw(alpha, 1f, 0f, 0f);
        if (halo != null) {
            if (haloState) {
                UtilsClient.disableLight();
                UtilsClient.enableBlend();
                UtilsClient.disableCulling();
                GL11.glColor3f(1.f, 0.f, 0.f);
                halo.draw();
                UtilsClient.enableCulling();
                UtilsClient.disableBlend();
                UtilsClient.enableLight();
            }
        }
    }

    @Override
    public Direction getFrontFromPlace(Direction side,
                                       EntityLivingBase entityLiving) {
        return Direction.XN;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
                                         ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        } else {
            objItemScale(obj);
            Direction.ZN.glRotateXnRef();
            draw(0f, false);
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer,
                        @NotNull List<String> list) {
        super.addInfo(itemStack, entityPlayer, list);

        list.add(I18N.tr("Generates energy from wind."));
        list.add(I18N.tr("Voltage: %1$V", Utils.plotValue(maxVoltage)));
        list.add(I18N.tr("Power: %1$W", Utils.plotValue(nominalPower)));
        list.add(I18N.tr("Wind area:"));
        list.add("  " + I18N.tr("Front: %1$", rayX));
        list.add("  " + I18N.tr("Up/Down: %1$", rayY));
        list.add("  " + I18N.tr("Left/Right: %1$", rayZ));
    }
}
