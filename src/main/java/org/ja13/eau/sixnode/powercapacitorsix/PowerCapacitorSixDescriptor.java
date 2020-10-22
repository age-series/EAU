package org.ja13.eau.sixnode.powercapacitorsix;

import org.ja13.eau.item.DielectricItem;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.misc.series.ISeriesMapping;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.ja13.eau.item.DielectricItem;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.misc.series.ISeriesMapping;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.lwjgl.opengl.GL11;

public class PowerCapacitorSixDescriptor extends SixNodeDescriptor {

    private final Obj3D obj;

    private Obj3D.Obj3DPart CapacitorCore;
    private Obj3D.Obj3DPart CapacitorCables;
    private Obj3D.Obj3DPart Base;

    ISeriesMapping serie;
    public double dischargeTao;

    public PowerCapacitorSixDescriptor(String name,
                                       Obj3D obj,
                                       ISeriesMapping serie,
                                       double dischargeTao) {
        super(name, PowerCapacitorSixElement.class, PowerCapacitorSixRender.class);
        this.serie = serie;
        this.dischargeTao = dischargeTao;
        this.obj = obj;
        if (obj != null) {
            CapacitorCables = obj.getPart("CapacitorCables");
            CapacitorCore = obj.getPart("CapacitorCore");
            Base = obj.getPart("Base");
        }

        voltageTier = VoltageTier.NEUTRAL;
    }

    public double getCValue(int cableCount, double nominalDielVoltage) {
        if (cableCount == 0) return 1e-6;
        double uTemp = nominalDielVoltage / VoltageTier.LOW.getVoltage();
        return serie.getValue(cableCount - 1) / uTemp / uTemp;
    }

    public double getCValue(IInventory inventory) {
        ItemStack core = inventory.getStackInSlot(PowerCapacitorSixContainer.redId);
        ItemStack diel = inventory.getStackInSlot(PowerCapacitorSixContainer.dielectricId);
        if (core == null || diel == null)
            return getCValue(0, 0);
        else {
            return getCValue(core.stackSize, getUNominalValue(inventory));
        }
    }

    public double getUNominalValue(IInventory inventory) {
        ItemStack diel = inventory.getStackInSlot(PowerCapacitorSixContainer.dielectricId);
        if (diel == null)
            return 10000;
        else {
            DielectricItem desc = (DielectricItem) DielectricItem.getDescriptor(diel);
            return desc.uNominal * diel.stackSize;
        }
    }

    void draw() {
        if (null != Base) Base.draw();
        if (null != CapacitorCables) CapacitorCables.draw();
        if (null != CapacitorCore) CapacitorCore.draw();
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
        if (type != ItemRenderType.INVENTORY) {
            GL11.glTranslatef(0.0f, 0.0f, -0.2f);
            GL11.glScalef(1.25f, 1.25f, 1.25f);
            GL11.glRotatef(-90.f, 0.f, 1.f, 0.f);
            draw();
        } else {
            super.renderItem(type, item, data);
        }
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).left();
    }
}
