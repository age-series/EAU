package org.ja13.eau.transparentnode.thermaldissipatoractive;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class ThermalDissipatorActiveDescriptor extends TransparentNodeDescriptor {

    double nominalP, nominalT;
    private final Obj3D obj;
    private Obj3D.Obj3DPart main;
    private Obj3D.Obj3DPart rot;

    public ThermalDissipatorActiveDescriptor(
        String name,
        Obj3D obj,
        double nominalElectricalU, double electricalNominalP,
        double nominalElectricalCoolingPower,
        GenericCableDescriptor cableDescriptor,
        double warmLimit, double coolLimit,
        double nominalP, double nominalT,
        double nominalTao, double nominalConnectionDrop
    ) {
        super(name, ThermalDissipatorActiveElement.class, ThermalDissipatorActiveRender.class);
        this.cableDescriptor = cableDescriptor;
        this.electricalNominalP = electricalNominalP;
        this.nominalElectricalU = nominalElectricalU;
        this.nominalElectricalCoolingPower = nominalElectricalCoolingPower;
        electricalRp = nominalElectricalU * nominalElectricalU / electricalNominalP;
        electricalToThermalRp = nominalT / nominalElectricalCoolingPower;
        thermalC = (nominalP + nominalElectricalCoolingPower) * nominalTao / nominalT;
        thermalRp = nominalT / nominalP;
        thermalRs = nominalConnectionDrop / (nominalP + nominalElectricalCoolingPower);
        EAU.simulator.checkThermalLoad(thermalRs, thermalRp, thermalC);
        this.coolLimit = coolLimit;
        this.warmLimit = warmLimit;
        this.nominalP = nominalP;
        this.nominalT = nominalT;
        this.obj = obj;
        if (obj != null) {
            main = obj.getPart("main");
            rot = obj.getPart("rot");
        }

        voltageTier = VoltageTier.NEUTRAL;
    }

    double warmLimit, coolLimit;
    double nominalElectricalU;
    double nominalElectricalCoolingPower;

    public void applyTo(ThermalLoad load) {
        load.set(thermalRs, thermalRp, thermalC);
    }

    public double thermalRs, thermalRp, thermalC;
    double electricalRp;
    double electricalToThermalRp;
    public double electricalNominalP;
    GenericCableDescriptor cableDescriptor;

    public void applyTo(ElectricalLoad load, Resistor r) {
        cableDescriptor.applyTo(load);
        r.setR(electricalRp);
    }


    void draw(float alpha) {
        if (main != null) main.draw();
        if (rot != null) rot.draw(alpha, 0f, 1f, 0f);
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
            draw(0f);
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer,
                        @NotNull List list) {

        super.addInfo(itemStack, entityPlayer, list);
        list.add(I18N.tr("Used to cool down turbines."));
        list.add(I18N.tr("Max. temperature: %1$°C", Utils.plotValue(warmLimit)));
        list.add(I18N.tr("Nominal usage:"));
        list.add("  " + I18N.tr("Temperature: %1$°C", Utils.plotValue(nominalT)));
        list.add("  " + I18N.tr("Cooling power: %1$W", Utils.plotValue(nominalP)));
        list.add("  " + I18N.tr("Fan voltage: %1$V", Utils.plotValue(nominalElectricalU)));
        list.add("  " + I18N.tr("Fan power consumption: %1$W", Utils.plotValue(electricalNominalP)));
        list.add("  " + I18N.tr("Fan cooling power: %1$W", Utils.plotValue(nominalElectricalCoolingPower)));

    }
}
