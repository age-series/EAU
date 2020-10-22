package org.ja13.eau.transparentnode.turbine;

import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.FunctionTable;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTierHelpers;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.PhysicalConstant;
import org.ja13.eau.sim.ThermalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.FunctionTable;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTierHelpers;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.PhysicalConstant;
import org.ja13.eau.sim.ThermalLoad;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class TurbineDescriptor extends TransparentNodeDescriptor {
    final CableRenderDescriptor eRender;

    public TurbineDescriptor(String name, String modelName, CableRenderDescriptor eRender,
                             FunctionTable TtoU, FunctionTable PoutToPin, double nominalDeltaT, double nominalU,
                             double nominalP, double nominalPowerLost, double electricalRs,
                             double thermalC, double DeltaTForInput,
                             double powerOutPerDeltaU, String soundFile) {
        super(name, TurbineElement.class, TurbineRender.class);
        double nominalEff = Math.abs(1 - (0 + PhysicalConstant.Tref) / (nominalDeltaT + PhysicalConstant.Tref));
        this.TtoU = TtoU;
        this.PoutToPin = PoutToPin;
        this.nominalDeltaT = nominalDeltaT;
        this.nominalU = nominalU;
        this.nominalP = nominalP;
        this.thermalC = thermalC;
        this.thermalRs = DeltaTForInput / (nominalP / nominalEff);
        this.thermalRp = nominalDeltaT / nominalPowerLost;
        this.electricalRs = electricalRs;
        this.powerOutPerDeltaU = powerOutPerDeltaU;
        this.eRender = eRender;
        this.soundFile = soundFile;
        Obj3D obj = EAU.obj.getObj(modelName);
        if (obj != null) {
            main = obj.getPart("main");
        }

        voltageTier = VoltageTierHelpers.Companion.fromVoltage(nominalU);
    }

    private Obj3D.Obj3DPart main;

    public final double powerOutPerDeltaU;
    public final FunctionTable TtoU;
    public final FunctionTable PoutToPin;
    public final double nominalDeltaT;
    public final double nominalU;
    final double nominalP;
    private final double thermalC;
    private final double thermalRs;
    private final double thermalRp;
    final double electricalRs;
    public final String soundFile;

    public void applyTo(ThermalLoad load) {
        load.C = thermalC;
        load.Rp = thermalRp;
        load.Rs = thermalRs;
    }

    public void applyTo(ElectricalLoad load) {
        load.setRs(electricalRs);
    }

    void draw() {
        if (main != null) main.draw();
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
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        } else {
            draw();
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);

        list.add(I18N.tr("Generates electricity using heat."));
        list.add(I18N.tr("Nominal usage:"));
        list.add("  " + I18N.tr("Temperature difference: %1$°C", Utils.plotValue(nominalDeltaT)));
        list.add("  " + I18N.tr("Voltage: %1$V", Utils.plotValue(nominalU)));
        list.add("  " + I18N.tr("Power: %1$W", Utils.plotValue(nominalP)));
    }
}
