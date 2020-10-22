package org.ja13.eau.transparentnode.eggincubator;

import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.mna.misc.MnaConst;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.mna.misc.MnaConst;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class EggIncubatorDescriptor extends TransparentNodeDescriptor {

    Obj3D obj;
    Obj3D defaultFeroObj;
    public GenericCableDescriptor cable;
    private Obj3D.Obj3DPart lamp;
    private EntityItem eggEntity;
    private Obj3D.Obj3DPart lampf;

    Obj3D.Obj3DPart main;

    double nominalVoltage, nominalPower;
    double Rp;

    public EggIncubatorDescriptor(String name,
                                  Obj3D obj,
                                  GenericCableDescriptor cable,
                                  double nominalVoltage, double nominalPower) {
        super(name, EggIncubatorElement.class, EggIncubatorRender.class);
        this.obj = obj;
        this.cable = cable;
        this.nominalVoltage = nominalVoltage;
        this.nominalPower = nominalPower;
        Rp = nominalVoltage * nominalVoltage / nominalPower;

        if (obj != null) {
            main = obj.getPart("main");
            lamp = obj.getPart("lamp");
            lampf = obj.getPart("lampf");
        }

        voltageTier = VoltageTier.LOW;
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
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        } else {
            draw(0, 1f);
        }
    }

    void draw(int eggStackSize, float powerFactor) {
        if (eggStackSize == 0) powerFactor = 0f;
        UtilsClient.disableCulling();
        if (main != null) main.draw();
        if (lampf != null) {
            GL11.glColor3f(0.1f, 0.1f, 0.1f);
            lampf.draw();
        }
        if (lamp != null) {
            UtilsClient.disableLight();
            UtilsClient.enableBlend();
            GL11.glColor4f(1f, 0.2f, 0.0f, powerFactor * powerFactor * 0.5f);
            lamp.draw();
            UtilsClient.disableBlend();
            UtilsClient.enableLight();
        }
        UtilsClient.enableCulling();
    }

    public void applyTo(NbtElectricalLoad powerLoad) {
        cable.applyTo(powerLoad);
    }

    public void setState(Resistor powerLoad, boolean enable) {
        if (enable)
            powerLoad.setR(Rp);
        else
            powerLoad.setR(MnaConst.highImpedance);
    }

    @Override
    public void addCollisionBoxesToList(AxisAlignedBB par5AxisAlignedBB, List list, World world, int x, int y, int z) {
        AxisAlignedBB bb = Blocks.stone.getCollisionBoundingBoxFromPool(world, x, y, z);
        bb.maxY -= 0.5;
        if (par5AxisAlignedBB.intersectsWith(bb)) list.add(bb);
    }
}
