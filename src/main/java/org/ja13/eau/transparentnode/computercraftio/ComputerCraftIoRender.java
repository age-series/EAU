package org.ja13.eau.transparentnode.computercraftio;

import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;

public class ComputerCraftIoRender extends TransparentNodeElementRender {

    ComputerCraftIoDescriptor descriptor;

    public ComputerCraftIoRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        this.descriptor = (ComputerCraftIoDescriptor) descriptor;
    }

    @Override
    public void draw() {
    }
}
