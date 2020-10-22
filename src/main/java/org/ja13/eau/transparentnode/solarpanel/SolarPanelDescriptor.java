package org.ja13.eau.transparentnode.solarpanel;

import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.ghost.GhostGroup;
import org.ja13.eau.misc.*;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.ElectricalLoad;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.ghost.GhostGroup;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTierHelpers;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.ElectricalLoad;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class SolarPanelDescriptor extends TransparentNodeDescriptor {

    final Coordonate groundCoordinate;
    boolean basicModel;
    private final Obj3D obj;
    private Obj3D.Obj3DPart main;
    private Obj3D.Obj3DPart panel;

    public SolarPanelDescriptor(
            String name,
            Obj3D obj, CableRenderDescriptor cableRender,
            GhostGroup ghostGroup, int solarOffsetX, int solarOffsetY, int solarOffsetZ,
            Coordonate groundCoordinate, double electricalUmax, double electricalPmax,
            double electricalDropFactor,
            double alphaMin, double alphaMax

    ) {
        super(name, SolarPanelElement.class, SolarPanelRender.class);
        this.groundCoordinate = groundCoordinate;
        this.ghostGroup = ghostGroup;

        electricalRs = electricalUmax * electricalUmax * electricalDropFactor
            / electricalPmax / 2.0;
        this.electricalPmax = electricalPmax;
        this.solarOffsetX = solarOffsetX;
        this.solarOffsetY = solarOffsetY;
        this.solarOffsetZ = solarOffsetZ;
        this.alphaMax = alphaMax;
        this.alphaMin = alphaMin;
        basicModel = true;
        this.electricalUmax = electricalUmax;

        this.obj = obj;
        if (obj != null) {
            main = obj.getPart("main");
            panel = obj.getPart("panel");
        }

        this.cableRender = cableRender;

        canRotate = alphaMax != alphaMin;

        voltageTier = VoltageTierHelpers.Companion.fromVoltage(electricalUmax);
    }

    CableRenderDescriptor cableRender;
    double electricalUmax;
    double electricalPmax;

    int solarOffsetX, solarOffsetY, solarOffsetZ;
    double alphaMin, alphaMax;
    //double efficiency;
    double electricalRs;

    boolean canRotate;

    public void applyTo(ElectricalLoad load) {
        load.setRs(electricalRs);
    }


    public double alphaTrunk(double alpha) {
        if (alpha > alphaMax) return alphaMax;
        if (alpha < alphaMin) return alphaMin;
        return alpha;
    }

    @Override
    public Direction getFrontFromPlace(Direction side, EntityLivingBase entityLiving) {
        if (canRotate && groundCoordinate != null) {
            // That is, if this isn't a 1x1 panel.
            return Direction.ZN;
        } else {
            return super.getFrontFromPlace(side, entityLiving);
        }
    }

    void draw(float alpha, Direction front) {
        front.glRotateZnRef();
        GL11.glTranslatef(0, 0, main.getFloat("offZ"));
        if (main != null) main.draw();
        if (panel != null) {
            front.glRotateZnRefInv();
            panel.draw(alpha, 0f, 0f, 1f);
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
                                         ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        } else {
            draw((float) alphaMin, Direction.XN);
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);

        list.add(I18N.tr("Produces power from solar radiation."));
        list.add("  " + I18N.tr("Max. voltage: %1$V", Utils.plotValue(electricalUmax)));
        list.add("  " + I18N.tr("Max. power: %1$W", Utils.plotValue(electricalPmax)));
        if (canRotate) list.add(I18N.tr("Can be geared towards the sun."));
    }

    @Override
    public void addCollisionBoxesToList(AxisAlignedBB par5AxisAlignedBB, List list, World world, int x, int y, int z) {
        if (canRotate) {
            super.addCollisionBoxesToList(par5AxisAlignedBB, list, world, x, y, z);
            return;
        }
        AxisAlignedBB bb = Blocks.stone.getCollisionBoundingBoxFromPool(world, x, y, z);
        bb.maxY -= 0.5;
        if (par5AxisAlignedBB.intersectsWith(bb)) list.add(bb);
    }
}
