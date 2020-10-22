package org.ja13.eau.sixnode.diode;

import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class DiodeRender extends SixNodeElementRender {

    private final DiodeDescriptor descriptor;

    double voltageAnode = 0, voltageCatode = 0, current = 0, temperature = 0;
    LRDU front;

    public DiodeRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (DiodeDescriptor) descriptor;
    }

    @Override
    public void draw() {
        front.glRotateOnX();
        descriptor.draw();
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            Byte b;
            b = stream.readByte();
            front = LRDU.fromInt((b >> 4) & 3);
            voltageAnode = stream.readShort() / NodeBase.networkSerializeUFactor;
            voltageCatode = stream.readShort() / NodeBase.networkSerializeUFactor;
            current = stream.readShort() / NodeBase.networkSerializeIFactor;
            temperature = stream.readShort() / NodeBase.networkSerializeTFactor;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
