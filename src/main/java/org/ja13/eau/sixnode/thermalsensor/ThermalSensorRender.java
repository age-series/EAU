package org.ja13.eau.sixnode.thermalsensor;

import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.generic.GenericItemBlockUsingDamageDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.sim.PhysicalConstant;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.ja13.eau.sixnode.thermalcable.ThermalCableDescriptor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.generic.GenericItemBlockUsingDamageDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.sim.PhysicalConstant;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.ja13.eau.sixnode.thermalcable.ThermalCableDescriptor;

import java.io.DataInputStream;
import java.io.IOException;

public class ThermalSensorRender extends SixNodeElementRender {

    SixNodeElementInventory inventory = new SixNodeElementInventory(1, 64, this);
    ThermalSensorDescriptor descriptor;
    long time;

    LRDU front;

    int typeOfSensor = 0;
    float lowValue = 0, highValue = 50;

    ThermalCableDescriptor cable;
    GenericCableDescriptor ecable;

    public ThermalSensorRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (ThermalSensorDescriptor) descriptor;
        time = System.currentTimeMillis();
    }

    @Override
    public void draw() {
        super.draw();
        front.glRotateOnX();
        descriptor.draw(ecable != null);
    }

	/*
	@Override
	public CableRenderDescriptor getCableRender(LRDU lrdu) {
		return descriptor.cableRender;
	}
	*/

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            Byte b;
            b = stream.readByte();
            front = LRDU.fromInt((b >> 4) & 3);
            typeOfSensor = b & 0x3;
            lowValue = (float) (stream.readFloat() + PhysicalConstant.Tamb);
            highValue = (float) (stream.readFloat() + PhysicalConstant.Tamb);
            ItemStack stack = Utils.unserializeItemStack(stream);
            GenericItemBlockUsingDamageDescriptor desc = GenericItemBlockUsingDamageDescriptor.Companion.getDescriptor(stack);
            if (desc instanceof ThermalCableDescriptor) cable = (ThermalCableDescriptor) desc;
            else cable = null;
            if (desc instanceof GenericCableDescriptor) ecable = (GenericCableDescriptor) desc;
            else ecable = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new ThermalSensorGui(player, inventory, this);
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        if (!descriptor.temperatureOnly) {
            if (front.left() == lrdu && cable != null) return cable.render;
            if (front.right() == lrdu && cable != null) return cable.render;
            if (front == lrdu) return EAU.smallInsulationLowCurrentRender;
        } else {
            if (front.inverse() == lrdu && cable != null) return cable.render;
            if (front.inverse() == lrdu && ecable != null) return ecable.render;
            if (front == lrdu) return EAU.smallInsulationLowCurrentRender;
        }
        return null;
    }
}
