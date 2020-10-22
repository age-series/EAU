package org.ja13.eau.sixnode.TreeResinCollector;

import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class TreeResinCollectorRender extends SixNodeElementRender {

    TreeResinCollectorDescriptor descriptor;

    float stock;

    public TreeResinCollectorRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (TreeResinCollectorDescriptor) descriptor;
    }

    @Override
    public void draw() {
        super.draw();

        LRDU.Down.glRotateOnX();
        descriptor.draw(stock);
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            stock = stream.readFloat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
