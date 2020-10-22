package org.ja13.eau.sixnode.resistor;

import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ResistorProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.mna.misc.MnaConst;
import org.ja13.eau.sim.nbt.NbtElectricalGateInput;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import org.ja13.eau.sim.process.heater.ResistorHeatThermalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ResistorProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.mna.misc.MnaConst;
import org.ja13.eau.sim.nbt.NbtElectricalGateInput;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sim.nbt.NbtThermalLoad;
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog;
import org.ja13.eau.sim.process.destruct.WorldExplosion;
import org.ja13.eau.sim.process.heater.ResistorHeatThermalLoad;

import javax.annotation.Nullable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResistorElement extends SixNodeElement {

    ResistorDescriptor descriptor;
    NbtElectricalLoad aLoad = new NbtElectricalLoad("aLoad");
    NbtElectricalLoad bLoad = new NbtElectricalLoad("bLoad");
    Resistor r = new Resistor(aLoad, bLoad);

    public NbtElectricalGateInput control;

    ThermalLoadWatchDog thermalWatchdog = new ThermalLoadWatchDog();
    NbtThermalLoad thermalLoad = new NbtThermalLoad("thermalLoad");
    ResistorHeatThermalLoad heater = new ResistorHeatThermalLoad(r, thermalLoad);
    ResistorProcess resistorProcess;

    public double nominalRs = 1;

    SixNodeElementInventory inventory = new SixNodeElementInventory(2, 64, this);

    public ResistorElement(SixNode SixNode, Direction side, SixNodeDescriptor descriptor) {
        super(SixNode, side, descriptor);
        this.descriptor = (ResistorDescriptor) descriptor;

        electricalLoadList.add(aLoad);
        electricalLoadList.add(bLoad);
        aLoad.setRs(MnaConst.noImpedance);
        bLoad.setRs(MnaConst.noImpedance);
        electricalComponentList.add(r);
        if (this.descriptor.isRheostat) {
            control = new NbtElectricalGateInput("control");
            electricalLoadList.add(control);
        }

        thermalLoadList.add(thermalLoad);
        thermalSlowProcessList.add(heater);
        thermalLoad.setAsSlow();
        double thermalC = this.descriptor.thermalMaximalPowerDissipated * this.descriptor.thermalNominalHeatTime / (this.descriptor.thermalWarmLimit);
        double thermalRp = this.descriptor.thermalWarmLimit / this.descriptor.thermalMaximalPowerDissipated;
        double thermalRs = this.descriptor.thermalConductivityTao / thermalC / 2;
        thermalLoad.set(thermalRs, thermalRp, thermalC);
        slowProcessList.add(thermalWatchdog);
        thermalWatchdog
            .set(thermalLoad)
            .setLimit(this.descriptor.thermalWarmLimit, this.descriptor.thermalCoolLimit)
            .set(new WorldExplosion(this).cableExplosion());

        resistorProcess = new ResistorProcess(this, r, thermalLoad, this.descriptor);
        if (this.descriptor.tempCoef != 0 || this.descriptor.isRheostat) {
            slowProcessList.add(resistorProcess);
        }
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            if (descriptor.isRheostat)
                stream.writeFloat((float) control.getNormalized());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        if (lrdu == front.right()) return aLoad;
        if (lrdu == front.left()) return bLoad;
        if (lrdu == front) return control;
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return thermalLoad;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        if (lrdu == front.right() || lrdu == front.left()) return NodeBase.MASK_ELECTRIC;
        if (lrdu == front && descriptor.isRheostat) return NodeBase.MASK_ELECTRIC;
        return 0;
    }

    @Override
    public String multiMeterString() {
        double u = -Math.abs(aLoad.getU() - bLoad.getU());
        double i = Math.abs(r.getI());
        return Utils.plotOhm(r.getR(), Utils.plotUIP(u, i)) +
            (control != null ? Utils.plotPercent(control.getNormalized(), "Control Signal:") : "");
    }

    @Nullable
    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        info.put(I18N.tr("Resistance"), Utils.plotValue(r.getR(), "\u2126", ""));
        info.put(I18N.tr("Voltage drop"), Utils.plotVolt(Math.abs(r.getU()), ""));
        if (EAU.wailaEasyMode) {
            info.put(I18N.tr("Current"), Utils.plotAmpere(Math.abs(r.getI()), ""));

        }
        return info;
    }

    @Override
    public String thermoMeterString() {
        return Utils.plotCelsius(thermalLoad.Tc, "");
    }

    @Override
    public void initialize() {
        setupPhysical();
    }

    @Override
    public void inventoryChanged() {
        super.inventoryChanged();
        setupPhysical();
    }

    public void setupPhysical() {
        nominalRs = descriptor.getRsValue(inventory);
        resistorProcess.process(0);
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public Container newContainer(Direction side, EntityPlayer player) {
        return new ResistorContainer(player, inventory);
    }
}
