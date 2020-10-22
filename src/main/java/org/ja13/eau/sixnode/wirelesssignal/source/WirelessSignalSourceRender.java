package org.ja13.eau.sixnode.wirelesssignal.source;

import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class WirelessSignalSourceRender extends SixNodeElementRender {

    WirelessSignalSourceDescriptor descriptor;

    RcInterpolator interpolator;

    String channel;
    boolean state = false;

    public WirelessSignalSourceRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (WirelessSignalSourceDescriptor) descriptor;

        interpolator = new RcInterpolator(this.descriptor.render.speed);
    }

    @Override
    public void draw() {
        super.draw();
        descriptor.draw((float)interpolator.get(), UtilsClient.distanceFromClientPlayer(this.tileEntity), tileEntity);
    }

    @Override
    public void refresh(float deltaT) {
        interpolator.setTarget((float) ((state ? 1 : 0)));
        interpolator.step(deltaT);
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return EAU.smallInsulationLowCurrentRender;
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new WirelessSignalSourceGui(this);
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            channel = stream.readUTF();
            state = stream.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
