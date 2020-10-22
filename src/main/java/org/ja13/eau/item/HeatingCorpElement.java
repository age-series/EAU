package org.ja13.eau.item;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTierHelpers;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.RegulatorThermalLoadToElectricalResistor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.RegulatorThermalLoadToElectricalResistor;

import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class HeatingCorpElement extends GenericItemUsingDamageDescriptorUpgrade {

    public double electricalNominalU;
    double electricalNominalP;
    double electricalMaximalP;
    double electricalR;
    double Umax;

    public HeatingCorpElement(String name,
                              double electricalNominalU, double electricalNominalP,
                              double electricalMaximalP) {
        super(name);

        this.electricalNominalU = electricalNominalU;
        this.electricalNominalP = electricalNominalP;
        this.electricalMaximalP = electricalMaximalP;

        electricalR = electricalNominalU * electricalNominalU / electricalNominalP;

        Umax = Math.sqrt(electricalMaximalP * electricalR);

        voltageTier = VoltageTierHelpers.Companion.fromVoltage(electricalNominalU);
    }
/*
    public void applyTo(ElectricalResistor resistor) {
		resistor.setR(electricalR);
	}*/

    public void applyTo(ElectricalLoad load) {
        EAU.uninsulatedHighCurrentCopperCable.applyTo(load);
    }

    public void applyTo(RegulatorThermalLoadToElectricalResistor regulator) {
        regulator.setRmin(electricalR);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);

        list.add(I18N.tr("Nominal:"));
        list.add("  " + tr("Voltage: %1$V", Utils.plotValue(electricalNominalU)));
        list.add("  " + tr("Power: %1$W", Utils.plotValue(electricalNominalP)));
        list.add("  " + tr("Resistance: %1$\u2126", Utils.plotValue(electricalR)));
    }
}
