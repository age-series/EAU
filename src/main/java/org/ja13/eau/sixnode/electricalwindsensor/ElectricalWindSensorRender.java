package org.ja13.eau.sixnode.electricalwindsensor;

import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class ElectricalWindSensorRender extends SixNodeElementRender {

    ElectricalWindSensorDescriptor descriptor;

    float alpha = 0;
    float wind = 0;

    RcInterpolator windFilter = new RcInterpolator(5);

    public ElectricalWindSensorRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (ElectricalWindSensorDescriptor) descriptor;
    }

    @Override
    public void draw() {
        super.draw();
        drawSignalPin(front.right(), new float[]{2, 2, 2, 2});

        descriptor.draw(alpha);
    }

    @Override
    public void refresh(float deltaT) {
        windFilter.step(deltaT);
        alpha += windFilter.get() * deltaT * 20;
        if (alpha > 360)
            alpha -= 360;
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return EAU.smallInsulationLowCurrentRender;
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            wind = stream.readFloat();
            windFilter.setTarget(wind);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
