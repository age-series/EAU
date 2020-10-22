package org.ja13.eau.sixnode.hub;

import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
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
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class HubRender extends SixNodeElementRender {

    HubDescriptor descriptor;

    //double voltage = 0, current = 0;
    int color = 0;

    CableRenderDescriptor[] cableRender = new CableRenderDescriptor[4];
    boolean[] connectionGrid = new boolean[6];

    SixNodeElementInventory inventory = new SixNodeElementInventory(4, 64, this);

    public HubRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (HubDescriptor) descriptor;
    }

    @Override
    public void draw() {
        super.draw();
        descriptor.draw(connectionGrid);
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            for (int idx = 0; idx < 4; idx++) {
                ItemStack cableStack = Utils.unserializeItemStack(stream);
                if (cableStack != null) {
                    GenericCableDescriptor desc = (GenericCableDescriptor) GenericCableDescriptor.getDescriptor(cableStack, GenericCableDescriptor.class);
                    if(desc != null) {
                        cableRender[idx] = desc.render;
                        continue;
                    }
                }
                cableRender[idx] = null;
            }
            for (int idx = 0; idx < 6; idx++) {
                connectionGrid[idx] = stream.readBoolean();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return cableRender[lrdu.toInt()];
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new HubGui(player, inventory, this);
    }
}
