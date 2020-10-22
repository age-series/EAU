package org.ja13.eau.sixnode.powerinductorsix;

import org.ja13.eau.EAU;
import org.ja13.eau.item.FerromagneticCoreDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.misc.series.ISeriesMapping;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.sim.mna.misc.MnaConst;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.ja13.eau.EAU;
import org.ja13.eau.item.FerromagneticCoreDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.misc.series.ISeriesMapping;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.sim.mna.misc.MnaConst;
import org.lwjgl.opengl.GL11;

public class PowerInductorSixDescriptor extends SixNodeDescriptor {

    private final Obj3D obj;
    Obj3D.Obj3DPart InductorBaseExtention, InductorCables, InductorCore, Base;

    ISeriesMapping serie;

    public PowerInductorSixDescriptor(String name,
                                      Obj3D obj,
                                      ISeriesMapping serie) {
        super(name, PowerInductorSixElement.class, PowerInductorSixRender.class);
        this.serie = serie;
        this.obj = obj;
        if (obj != null) {
            InductorBaseExtention = obj.getPart("InductorBaseExtention");
            InductorCables = obj.getPart("InductorCables");
            InductorCore = obj.getPart("InductorCore");
            Base = obj.getPart("Base");
        }

        voltageTier = VoltageTier.NEUTRAL;
    }

    public double getlValue(int cableCount) {
        if (cableCount == 0) return 0;
        return serie.getValue(cableCount - 1);
    }

    public double getlValue(IInventory inventory) {
        ItemStack core = inventory.getStackInSlot(PowerInductorSixContainer.cableId);
        if (core == null)
            return getlValue(0);
        else
            return getlValue(core.stackSize);
    }

    public double getRsValue(IInventory inventory) {
        ItemStack core = inventory.getStackInSlot(PowerInductorSixContainer.coreId);

        if (core == null) return MnaConst.highImpedance;
        FerromagneticCoreDescriptor coreDescriptor = (FerromagneticCoreDescriptor) FerromagneticCoreDescriptor.getDescriptor(core);

        double coreFactor = coreDescriptor.cableMultiplicator;

        return EAU.uninsulatedHighCurrentCopperCable.electricalRs * coreFactor;
    }

    void draw() {
        //UtilsClient.disableCulling();
        //UtilsClient.disableTexture();
        if (null != Base) Base.draw();
        if (null != InductorBaseExtention) InductorBaseExtention.draw();
        if (null != InductorCables) InductorCables.draw();
        if (null != InductorCore) InductorCore.draw();
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
