package org.ja13.eau.sixnode.powercapacitorsix;

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
import org.lwjgl.opengl.GL11;

public class PowerCapacitorSixRender extends SixNodeElementRender {

    public PowerCapacitorSixDescriptor descriptor;
    private CableRenderType renderPreProcess;

    SixNodeElementInventory inventory = new SixNodeElementInventory(2, 64, this);

    public PowerCapacitorSixRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (PowerCapacitorSixDescriptor) descriptor;
    }

    @Override
    public void draw() {
        GL11.glRotatef(90, 1, 0, 0);
        front.glRotateOnX();
        descriptor.draw();
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new PowerCapacitorSixGui(player, inventory, this);
    }
}
