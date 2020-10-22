package org.ja13.eau.sixnode.electricallightsensor;

import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

public class ElectricalLightSensorRender extends SixNodeElementRender {

    ElectricalLightSensorDescriptor descriptor;

    public ElectricalLightSensorRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (ElectricalLightSensorDescriptor) descriptor;
    }

    @Override
    public void draw() {
        super.draw();
        drawSignalPin(front.right(), descriptor.pinDistance);

        if (side.isY()) {
            front.glRotateOnX();
        }

        descriptor.draw();
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return EAU.smallInsulationLowCurrentRender;
    }
}
