package org.ja13.eau.sixnode.electricalvumeter;

import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.PhysicalInterpolator;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.PhysicalInterpolator;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class ElectricalVuMeterRender extends SixNodeElementRender {

    ElectricalVuMeterDescriptor descriptor;

    PhysicalInterpolator interpolator;
    float factor;
    LRDU front;

    boolean boot = true;

    public ElectricalVuMeterRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (ElectricalVuMeterDescriptor) descriptor;
        interpolator = new PhysicalInterpolator(0.4f, 2.0f, 1.5f, 0.2f);
    }

    @Override
    public void draw() {
        super.draw();
        drawSignalPin(front, descriptor.pinDistance);

        if (side.isY()) {
            front.right().glRotateOnX();
        }

        descriptor.draw(descriptor.onOffOnly ? (float)interpolator.getTarget() : (float)interpolator.get(), UtilsClient.distanceFromClientPlayer(tileEntity), tileEntity);
    }

    @Override
    public void refresh(float deltaT) {
        interpolator.step(deltaT);
    }

    @Override
    public boolean cameraDrawOptimisation() {
        return false;
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            Byte b;
            b = stream.readByte();
            front = LRDU.fromInt((b >> 4) & 3);
            if (boot) {
                interpolator.setPos(stream.readFloat());
            } else {
                interpolator.setTarget(stream.readFloat());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (boot) {
            boot = false;
        }
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return EAU.smallInsulationLowCurrentRender;
    }
}
