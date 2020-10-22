package org.ja13.eau.sixnode.electricalwatch;

import org.ja13.eau.misc.Direction;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class ElectricalWatchRender extends SixNodeElementRender {

    ElectricalWatchDescriptor descriptor;

    boolean upToDate = false;
    long oldDate = 1379;

    SixNodeElementInventory inventory = new SixNodeElementInventory(1, 64, this);

    public ElectricalWatchRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (ElectricalWatchDescriptor) descriptor;
    }

    @Override
    public void draw() {
        super.draw();
        long time;
        if (upToDate)
            time = tileEntity.getWorldObj().getWorldTime();
        else
            time = oldDate;
        time += 6000;
        time %= 24000;

        front.glRotateOnX();

        descriptor.draw(time / 12000f, (time % 1000) / 1000f, upToDate);
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            upToDate = stream.readBoolean();
            oldDate = stream.readLong();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new ElectricalWatchGui(player, inventory, this);
    }
}
