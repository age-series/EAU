package org.ja13.eau.sixnode.electricalsensor;

import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;

import java.io.DataInputStream;
import java.io.IOException;

public class ElectricalSensorRender extends SixNodeElementRender {

    SixNodeElementInventory inventory = new SixNodeElementInventory(1, 64, this);
    ElectricalSensorDescriptor descriptor;
    long time;

    int typeOfSensor = 0;
    float lowValue = 0, highValue = 50;
    byte dirType;
    CableRenderDescriptor cableRender = null;

    public ElectricalSensorRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (ElectricalSensorDescriptor) descriptor;
        time = System.currentTimeMillis();
    }

    @Override
    public void draw() {
        super.draw();
        front.glRotateOnX();
        descriptor.draw();
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
            typeOfSensor = b & 0x3;
            lowValue = stream.readFloat();
            highValue = stream.readFloat();
            dirType = stream.readByte();
            cableRender = GenericCableDescriptor.getCableRender(Utils.unserializeItemStack(stream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        if (descriptor.voltageOnly) {
            if (lrdu == front) return EAU.smallInsulationLowCurrentRender;
            if (lrdu == front.inverse()) return cableRender;
        } else {
            if (lrdu == front) return EAU.smallInsulationLowCurrentRender;
            if (lrdu == front.left() || lrdu == front.right()) return cableRender;
        }
        return super.getCableRender(lrdu);
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new ElectricalSensorGui(player, inventory, this);
    }
}
