package org.ja13.eau.transparentnode.electricalmachine;

import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.*;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ElectricalStackMachineProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.ThermalLoadInitializer;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.ja13.eau.sound.SoundCommand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.RecipesList;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ElectricalStackMachineProcess;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.ThermalLoadInitializer;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.ja13.eau.sound.SoundCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalMachineDescriptor extends TransparentNodeDescriptor {
    public RecipesList recipe = new RecipesList();

    final double nominalU;
    final double nominalP;
    private final ThermalLoadInitializer thermal;
    final GenericCableDescriptor cable;
    final int outStackCount;

    private final double resistorR;

    final double boosterEfficiency = 1.0 / 1.1;
    final double boosterSpeedUp = 1.25 / boosterEfficiency;

    SoundCommand endSound;
    String runningSound;

    private Object defaultHandle = null;

    ElectricalMachineDescriptor(String name, double nominalU, double nominalP, double maximalU,
                                ThermalLoadInitializer thermal, GenericCableDescriptor cable,
                                RecipesList recipe) {
        super(name, ElectricalMachineElement.class, ElectricalMachineRender.class);
        outStackCount = 4;
        this.nominalP = nominalP;
        this.nominalU = nominalU;
        this.cable = cable;
        this.thermal = thermal;
        resistorR = nominalU * nominalU / nominalP;
        this.recipe = recipe;

        voltageTier = VoltageTier.NEUTRAL;
    }

    public ElectricalMachineDescriptor setRunningSound(String runningSound) {
        this.runningSound = runningSound;
        return this;
    }

    public ElectricalMachineDescriptor setEndSound(SoundCommand endSound) {
        this.endSound = endSound;
        return this;
    }

    public float volumeForRunningSound(float processState, float powerFactor) {
        if (powerFactor >= 0.3)
            return 0.3f * powerFactor;
        else
            return 0f;
    }

    @Override
    public void setParent(Item item, int damage) {
        super.setParent(item, damage);
        recipe.addMachine(newItemStack(1));
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        list.add(I18N.tr("Nominal voltage: %1$V", Utils.plotValue(nominalU)));
        list.add(I18N.tr("Nominal power: %1$W", Utils.plotValue(nominalP)));
    }

    public void applyTo(ElectricalLoad load) {
        cable.applyTo(load);
    }

    public void applyTo(Resistor resistor) {
        resistor.setR(resistorR);
    }

    public void applyTo(ElectricalStackMachineProcess machine) {
        machine.setResistorValue(resistorR);
    }

    public void applyTo(ThermalLoad load) {
        thermal.applyTo(load);
    }

    Object newDrawHandle() {
        return null;
    }

    void draw(ElectricalMachineRender render, Object handleO, EntityItem inEntity, EntityItem outEntity,
              float powerFactor, float processState) {
    }

    void refresh(float deltaT, ElectricalMachineRender render, Object handleO, EntityItem inEntity,
                 EntityItem outEntity, float powerFactor, float processState) {
    }

    public boolean powerLrdu(Direction side, Direction front) {
        return true;
    }

    public boolean drawCable() {
        return false;
    }

    CableRenderDescriptor getPowerCableRender() {
        return null;
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
            draw(null, getDefaultHandle(), null, null, 0f, 0f);
        }
    }

    private Object getDefaultHandle() {
        if (defaultHandle == null)
            defaultHandle = newDrawHandle();
        return defaultHandle;
    }
}
