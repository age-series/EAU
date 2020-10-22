package org.ja13.eau.sixnode.thermalcable;

import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.sim.ThermalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.sim.ThermalLoad;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class ThermalCableDescriptor extends SixNodeDescriptor {
    double thermalRp = 1, thermalRs = 1, thermalC = 1;
    double thermalWarmLimit, thermalCoolLimit;
    double thermalStdT, thermalStdPower;
    double thermalStdDrop, thermalStdLost;
    double thermalTao;

    String description = "todo cable";

    public CableRenderDescriptor render;

    public static final ThermalCableDescriptor[] list = new ThermalCableDescriptor[256];

    public ThermalCableDescriptor(String name,
                                  double thermalWarmLimit, double thermalCoolLimit,
                                  double thermalStdT, double thermalStdPower,
                                  double thermalStdDrop, double thermalStdLost,
                                  double thermalTao,
                                  CableRenderDescriptor render,
                                  String description) {
        super(name, ThermalCableElement.class, ThermalCableRender.class);

        this.description = description;
        this.render = render;

        this.thermalWarmLimit = thermalWarmLimit;
        this.thermalCoolLimit = thermalCoolLimit;
        this.thermalStdT = thermalStdT;
        this.thermalStdPower = thermalStdPower;
        this.thermalStdDrop = thermalStdDrop;
        this.thermalStdLost = thermalStdLost;
        this.thermalTao = thermalTao;

        thermalRs = thermalStdDrop / 2 / thermalStdPower;
        thermalRp = thermalStdT / thermalStdLost;
        thermalC = EAU.simulator.getMinimalThermalC(thermalRs, thermalRp);
        if (!EAU.simulator.checkThermalLoad(thermalRs, thermalRp, thermalC)) {
            Utils.println("Bad thermalCable setup");
            while (true) ;
        }
        voltageTier = VoltageTier.NEUTRAL;
    }

    public static ThermalCableDescriptor getDescriptorFrom(ItemStack itemStack) {
        return list[(itemStack.getItemDamage() >> 8) & 0xFF];
    }

    public void setThermalLoad(ThermalLoad thermalLoad) {
        thermalLoad.Rp = thermalRp;
        thermalLoad.Rs = thermalRs;
        thermalLoad.C = thermalC;
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);

        list.add(I18N.tr("Max. temperature: %1$°C", Utils.plotValue(thermalWarmLimit)));
        list.add(I18N.tr("Serial resistance: %1$K/W", Utils.plotValue(thermalRs * 2)));
        list.add(I18N.tr("Parallel resistance: %1$K/W", Utils.plotValue(thermalRp)));
        list.add("");
        Collections.addAll(list, I18N.tr("Low serial resistance\n => High conductivity.").split("\n"));
        Collections.addAll(list, I18N.tr("High parallel resistance\n => Low power dissipation.").split("\n"));
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
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }
}
