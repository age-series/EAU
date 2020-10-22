package org.ja13.eau.sixnode.genericcable;

import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.generic.GenericItemBlockUsingDamage;
import org.ja13.eau.generic.GenericItemBlockUsingDamageDescriptor;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;
import net.minecraft.item.ItemStack;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.generic.GenericItemBlockUsingDamageDescriptor;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.mna.component.Resistor;

public abstract class GenericCableDescriptor extends SixNodeDescriptor {

    public double electricalNominalVoltage;
    public double electricalMaximalVoltage;
    public double electricalMaximalCurrent;
    public double electricalNominalPower;
    public double electricalRs;

    // TODO: remove later
    public double thermalRp;
    public double thermalC;
    public double thermalRs;
    public double thermalWarmLimit;
    public double thermalCoolLimit;

    public CableRenderDescriptor render;

    public GenericCableDescriptor(String name, Class ElementClass, Class RenderClass) {
        super(name, ElementClass, RenderClass);
    }

    public int getNodeMask() {
        return NodeBase.MASK_ELECTRIC;
    }

    public abstract void applyTo(ElectricalLoad electricalLoad, double rsFactor);

    public abstract void applyTo(ElectricalLoad electricalLoad);

    public abstract void applyTo(Resistor resistor);

    public abstract void applyTo(Resistor resistor, double factor);

    public abstract void applyTo(ThermalLoad thermalLoad);

    public static CableRenderDescriptor getCableRender(ItemStack cable) {
        if(cable == null) return null;
        GenericItemBlockUsingDamageDescriptor desc = GenericItemBlockUsingDamageDescriptor.Companion.getDescriptor(cable);
        if (desc instanceof GenericCableDescriptor)
            return ((GenericCableDescriptor) desc).render;
        else
            return null;
    }

    public void bindCableTexture() {
        this.render.bindCableTexture();
    }
}
