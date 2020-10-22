package org.ja13.eau.sixnode.electricaltimeout;

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

public class ElectricalTimeoutRender extends SixNodeElementRender {

    ElectricalTimeoutDescriptor descriptor;
    long time;

    float timeoutValue = 0, timeoutCounter = 0;
    boolean inputState;

    public ElectricalTimeoutRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (ElectricalTimeoutDescriptor) descriptor;
        time = System.currentTimeMillis();
    }

    //PhysicalInterpolator interpolator = new PhysicalInterpolator(0.2f, 2.0f, 1.5f, 0.2f);

    @Override
    public void draw() {
        super.draw();
        front.glRotateOnX();

        descriptor.draw(timeoutCounter / timeoutValue);
    }

    @Override
    public void refresh(float deltaT) {
        if (!inputState) {
            timeoutCounter -= deltaT;
            if (timeoutCounter < 0f) timeoutCounter = 0f;
        }
    }

    @Override
    public boolean cameraDrawOptimisation() {
        return false;
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            timeoutValue = stream.readFloat();
            timeoutCounter = stream.readFloat();
            inputState = stream.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return EAU.smallInsulationLowCurrentRender;
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new ElectricalTimeoutGui(player, this);
    }
}
