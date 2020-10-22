package org.ja13.eau.sixnode.electricaldigitaldisplay;

import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

import static org.ja13.eau.sixnode.electricaldigitaldisplay.ElectricalDigitalDisplayDescriptor.Style.LED;

public class ElectricalDigitalDisplayRender extends SixNodeElementRender {
    ElectricalDigitalDisplayDescriptor descriptor;

    public float current = 0.0f;
    public float max = 1000.0f;
    public float min = 0.0f;
    public float dots = 0.0f;
    public boolean strobe = false;
    public ElectricalDigitalDisplayDescriptor.Style style = LED;
    public int dye;

    public ElectricalDigitalDisplayRender(SixNodeEntity entity, Direction side, SixNodeDescriptor descriptor) {
        super(entity, side, descriptor);
        this.descriptor = (ElectricalDigitalDisplayDescriptor) descriptor;
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return EAU.smallInsulationLowCurrentRender;
    }

    @Override
    public void draw() {
        super.draw();
        drawSignalPin(front.inverse(), descriptor.pinDistance);
        descriptor.draw((int) (min + current * (max - min)), strobe, style, dye, (int) (dots * ElectricalDigitalDisplayDescriptor.DOT_STATES));
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            current = stream.readFloat();
            min = stream.readFloat();
            max = stream.readFloat();
            dots = stream.readFloat();
            strobe = stream.readBoolean();
            dye = stream.readByte();
            //Utils.println(String.format("EDDR values %f (%f - %f)", current, min, max));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new ElectricalDigitalDisplayGui(this);
    }
}
