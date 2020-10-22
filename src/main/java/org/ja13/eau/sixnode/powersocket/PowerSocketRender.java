package org.ja13.eau.sixnode.powersocket;

import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.generic.GenericItemBlockUsingDamageDescriptor;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.generic.GenericItemBlockUsingDamageDescriptor;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;
import java.io.IOException;

public class PowerSocketRender extends SixNodeElementRender {

    PowerSocketDescriptor descriptor;

    Coordonate coord;
    String channel;

    CableRenderDescriptor cableRender;

    SixNodeElementInventory inventory = new SixNodeElementInventory(1, 64, this);

    public int paintColor = 15;

    public PowerSocketRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (PowerSocketDescriptor) descriptor;
        coord = new Coordonate(tileEntity);
    }

    @Override
    public void drawCables() {
        Utils.setGlColorFromDye(paintColor, 1.0f);
        super.drawCables();
        GL11.glColor3f(1f, 1f, 1f);
    }

    @Override
    public void draw() {
        super.draw();
        descriptor.draw(paintColor);
    }

    @Override
    public void refresh(float deltaT) {

    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return cableRender;
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new PowerSocketGui(this, player, inventory);
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            channel = stream.readUTF();

            ItemStack cableStack = Utils.unserializeItemStack(stream);
            if (cableStack != null) {
                GenericCableDescriptor desc = (GenericCableDescriptor) GenericItemBlockUsingDamageDescriptor.Companion.getDescriptor(cableStack);
                cableRender = desc.render;
            } else {
                cableRender = null;
            }

            paintColor = stream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
