package org.ja13.eau.sixnode.powerinductorsix;

import org.ja13.eau.cable.CableRenderType;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.cable.CableRenderType;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

public class PowerInductorSixRender extends SixNodeElementRender {

    public PowerInductorSixDescriptor descriptor;
    private CableRenderType renderPreProcess;

    SixNodeElementInventory inventory = new SixNodeElementInventory(2, 64, this);

    public PowerInductorSixRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (PowerInductorSixDescriptor) descriptor;
    }

    @Override
    public void draw() {
        front.left().glRotateOnX();
        descriptor.draw();
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new PowerInductorSixGui(player, inventory, this);
    }
}
