package org.ja13.eau.transparentnode.thermaldissipatoractive;

import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class ThermalDissipatorActiveRender extends TransparentNodeElementRender {
    ThermalDissipatorActiveDescriptor descriptor;

    public ThermalDissipatorActiveRender(TransparentNodeEntity tileEntity,
                                         TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        this.descriptor = (ThermalDissipatorActiveDescriptor) descriptor;
    }

    RcInterpolator rc = new RcInterpolator(2f);

    @Override
    public void draw() {
        front.glRotateXnRef();


        descriptor.draw(alpha);
    }

    @Override
    public void refresh(double deltaT) {
        rc.setTarget(powerFactor);
        rc.step(deltaT);
        alpha += rc.get() * 360f * deltaT;
        while (alpha > 360f) alpha -= 360f;
    }

    float alpha = 0;
    float powerFactor;

    @Override
    public void networkUnserialize(DataInputStream stream) {

        super.networkUnserialize(stream);
        try {
            powerFactor = stream.readFloat();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
