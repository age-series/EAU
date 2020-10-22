package org.ja13.eau.transparentnode.battery;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.FunctionTable;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTierHelpers;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.*;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.FunctionTable;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTierHelpers;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.BatteryProcess;
import org.ja13.eau.sim.BatterySlowProcess;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.Simulator;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class BatteryDescriptor extends TransparentNodeDescriptor {

    public double electricalU, electricalDischargeRate;
    public double electricalStdP, electricalStdDischargeTime, electricalStdHalfLife, electricalStdEfficiency, electricalPMax;

    public double electricalStdEnergy, electricalStdI;

    public double thermalHeatTime, thermalWarmLimit, thermalCoolLimit;

    public double electricalQ, electricalRs, electricalRp;
    public double thermalC, thermalRp, thermalPMax;
    public double lifeNominalCurrent, lifeNominalLost;
    public double startCharge;
    public boolean isRechargable;
    String description = "todo battery";

    FunctionTable UfCharge;
    String modelName;
    Obj3D.Obj3DPart modelPart;
    public double IMax;
    public boolean lifeEnable;
    private final GenericCableDescriptor cable;

    Obj3D obj;

    Obj3D.Obj3DPart main, plugPlus, plusMinus, battery;

    int renderType;
    private String renderSpec;

    public static final BatteryDescriptor[] list = new BatteryDescriptor[8];

    public double currentDropVoltage = 1000000, currentDropFactor = 0;

    public void draw(boolean plus, boolean minus) {
        switch (renderType) {
            case 0:
                if (modelPart == null) return;
                modelPart.draw();
                break;
            case 1:
                if (main != null) main.draw();
                if (plugPlus != null && plus) plugPlus.draw();
                if (plusMinus != null && minus) plusMinus.draw();
                //if(cables != null) cables.draw();
                if (battery != null) battery.draw();
                break;
        }
    }

    public BatteryDescriptor(String name, String modelName,
                             GenericCableDescriptor cable,
                             double startCharge, boolean isRechargable, boolean lifeEnable,
                             FunctionTable UfCharge,
                             double electricalU, double electricalPMax, double electricalDischargeRate,
                             double electricalStdP, double electricalStdDischargeTime, double electricalStdEfficiency, double electricalStdHalfLife,
                             double thermalHeatTime, double thermalWarmLimit, double thermalCoolLimit,
                             String description) {
        super(name, BatteryElement.class, BatteryRender.class);
        this.electricalU = electricalU;
        this.electricalDischargeRate = electricalDischargeRate;
        this.electricalStdEfficiency = electricalStdEfficiency;
        this.electricalStdP = electricalStdP;
        this.electricalStdHalfLife = electricalStdHalfLife;
        this.electricalStdDischargeTime = electricalStdDischargeTime;
        this.startCharge = startCharge;
        this.isRechargable = isRechargable;
        this.lifeEnable = lifeEnable;
        this.cable = cable;

        this.thermalHeatTime = thermalHeatTime;
        this.thermalWarmLimit = thermalWarmLimit;
        this.thermalCoolLimit = thermalCoolLimit;
        this.electricalPMax = electricalPMax;

        this.UfCharge = UfCharge;
        this.description = description;

        electricalStdI = electricalStdP / electricalU;
        electricalStdEnergy = electricalStdDischargeTime * electricalStdP;

        electricalQ = electricalStdP * electricalStdDischargeTime / electricalU;
        electricalQ = 1;
        double energy = getEnergy(1.0, 1.0);
        electricalQ *= electricalStdEnergy / energy;
        electricalRs = electricalStdP * (1 - electricalStdEfficiency) / electricalStdI / electricalStdI / 2;
        //electricalRs = cable.electricalRs;
        electricalRp = Math.min(electricalU * electricalU / electricalStdP / electricalDischargeRate, 1000000000.0);

        lifeNominalCurrent = electricalStdP / electricalU;
        lifeNominalLost = 0.5 / electricalStdHalfLife;

        thermalPMax = electricalPMax / electricalU * electricalPMax / electricalU * electricalRs * 2;
        thermalC = Math.pow(electricalPMax / electricalU, 2) * electricalRs * thermalHeatTime / thermalWarmLimit;
        thermalRp = thermalWarmLimit / thermalPMax;

        IMax = electricalStdI * 3;

        obj = EAU.obj.getObj(modelName);

        if (obj != null) {
            if (obj.getString("type").equals("A"))
                renderType = 0;
            if (obj.getString("type").equals("B"))
                renderType = 1;

            switch (renderType) {
                case 0:
                    modelPart = obj.getPart("Battery");
                case 1:
                    break;
            }
        }

        voltageTier = VoltageTierHelpers.Companion.fromVoltage(electricalU);
    }

    public void applyTo(Resistor resistor) {
        resistor.setR(electricalRp);
    }

    public void applyTo(BatteryProcess battery) {
        battery.uNominal = electricalU;
        battery.QNominal = electricalQ;
        battery.voltageFunction = UfCharge;
        battery.isRechargeable = isRechargable;
        //battery.efficiency = electricalStdEfficiency;

        // Convert old battery absolute charge in Coulomb to to fraction of battery capacity if the capacity is
        // very small and the output voltage is more than a quarter of the nominal voltage.
        if (battery.Q > 1.5 && battery.getU() > (battery.uNominal / 4)) {
            battery.Q /= electricalQ;
        }
    }

    public void applyTo(ElectricalLoad load, Simulator simulator) {
        load.setRs(electricalRs);
    }

    public void applyTo(ThermalLoad load) {
        load.Rp = thermalRp;
        load.C = thermalC;
        //load.setRsByTao(2);
    }

    public void applyTo(BatterySlowProcess process) {
        process.lifeNominalCurrent = lifeNominalCurrent;
        process.lifeNominalLost = lifeNominalLost;
    }

    public static BatteryDescriptor getDescriptorFrom(ItemStack itemStack) {
        return list[itemStack.getItemDamage() & 0x7];
    }

    @Override
    public NBTTagCompound getDefaultNBT() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setDouble("charge", startCharge);
        nbt.setDouble("life", 1.0);
        return nbt;
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        list.add(Utils.plotVolt(electricalU, I18N.tr("Nominal voltage: ")));
        list.add(Utils.plotPower(electricalStdP, I18N.tr("Nominal power: ")));
        list.add(Utils.plotEnergy(electricalStdDischargeTime * electricalStdP, I18N.tr("Energy capacity: ")));
        list.add(Utils.plotOhm(electricalRs * 2, I18N.tr("Internal resistance: ")));
        list.add("");
        list.add(Utils.plotPercent(getChargeInTag(itemStack), I18N.tr("Actual charge: ")));
        if (lifeEnable)
            list.add(Utils.plotPercent(getLifeInTag(itemStack), I18N.tr("Life: ")));
    }

    @Override
    public String getName(ItemStack stack) {
        return super.getName(stack) + Utils.plotPercent(getChargeInTag(stack), I18N.tr(" charged at "));
    }

    double getChargeInTag(ItemStack stack) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(getDefaultNBT());
        return stack.getTagCompound().getDouble("charge");
    }

    double getLifeInTag(ItemStack stack) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(getDefaultNBT());
        return stack.getTagCompound().getDouble("life");
    }

    public double getEnergy(double charge, double life) {
        int stepNbr = 50;
        double chargeStep = charge / stepNbr;
        double chargeIntegrator = 0;
        double energy = 0;
        double QperStep = electricalQ * life * charge / stepNbr;

        for (int step = 0; step < stepNbr; step++) {
            double voltage = UfCharge.getValue(chargeIntegrator) * electricalU;
            energy += voltage * QperStep;
            chargeIntegrator += chargeStep;
        }
        return energy;
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
            draw(true, true);
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (entityItem.isBurning()) {
            entityItem.worldObj.createExplosion(entityItem, entityItem.posX, entityItem.posY, entityItem.posZ, 2, true);
            entityItem.extinguish();
            entityItem.setDead();
        }
        return false;
    }

    public void setRenderSpec(String renderSpec) {
        this.renderSpec = renderSpec;

        if (obj != null) {
            switch (renderType) {
                case 0:

                case 1:
                    main = obj.getPart("main");
                    plugPlus = obj.getPart("plugPlus");
                    plusMinus = obj.getPart("plugMinus");
                    //	cables = obj.getPart("cables");
                    battery = obj.getPart("battery_" + renderSpec);
                    break;
            }
        }
    }

    public void setCurrentDrop(double currentDropVoltage, double currentDropFactor) {
        this.currentDropFactor = currentDropFactor;
        this.currentDropVoltage = currentDropVoltage;
    }
}
