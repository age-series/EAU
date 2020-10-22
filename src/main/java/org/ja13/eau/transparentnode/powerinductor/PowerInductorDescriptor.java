package org.ja13.eau.transparentnode.powerinductor;

import org.ja13.eau.EAU;
import org.ja13.eau.item.FerromagneticCoreDescriptor;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.series.ISeriesMapping;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.mna.misc.MnaConst;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.ja13.eau.EAU;
import org.ja13.eau.item.FerromagneticCoreDescriptor;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.series.ISeriesMapping;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.mna.misc.MnaConst;

public class PowerInductorDescriptor extends TransparentNodeDescriptor {

    private final Obj3D obj;

    public PowerInductorDescriptor(
            String name,
            Obj3D obj,
            ISeriesMapping serie

    ) {
        super(name, PowerInductorElement.class, PowerInductorRender.class);
        this.serie = serie;
        this.obj = obj;
        if (obj != null) {

        }

    }

    ISeriesMapping serie;

    public double getlValue(int cableCount) {
        if (cableCount == 0) return 0;
        return serie.getValue(cableCount - 1);
    }

    public double getlValue(IInventory inventory) {
        ItemStack core = inventory.getStackInSlot(PowerInductorContainer.cableId);
        if (core == null)
            return getlValue(0);
        else
            return getlValue(core.stackSize);
    }

    public double getRsValue(IInventory inventory) {
        ItemStack core = inventory.getStackInSlot(PowerInductorContainer.coreId);

        if (core == null) return MnaConst.highImpedance;
        FerromagneticCoreDescriptor coreDescriptor = (FerromagneticCoreDescriptor) FerromagneticCoreDescriptor.getDescriptor(core);

        double coreFactor = coreDescriptor.cableMultiplicator;

        return EAU.uninsulatedHighCurrentCopperCable.electricalRs * coreFactor;
    }

    public void setParent(net.minecraft.item.Item item, int damage) {
        super.setParent(item, damage);
        //Data.addEnergy(newItemStack());
    }

    void draw() {

    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
                                         ItemRendererHelper helper) {
        return true;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        draw();
    }
}
