package org.ja13.eau.sixnode.electricalrelay;

import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class ElectricalRelayRender extends SixNodeElementRender {

    SixNodeElementInventory inventory = new SixNodeElementInventory(0, 64, this);
    ElectricalRelayDescriptor descriptor;
    long time;

    RcInterpolator interpolator;

    boolean boot = true;
    float switchAlpha = 0;
    public boolean switchState, defaultOutput;

    public ElectricalRelayRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (ElectricalRelayDescriptor) descriptor;
        time = System.currentTimeMillis();
        interpolator = new RcInterpolator(this.descriptor.speed);
    }

    @Override
    public void draw() {
        super.draw();
        //UtilsClient.enableDepthTest();
        drawSignalPin(front, new float[]{2.5f, 2.5f, 2.5f, 2.5f});
        front.glRotateOnX();

        descriptor.draw((float)interpolator.get());
    }

    @Override
    public void refresh(float deltaT) {
        interpolator.step(deltaT);
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            switchState = stream.readBoolean();
            defaultOutput = stream.readBoolean();

            interpolator.setTarget(switchState ? 1f : 0f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (boot) {
            interpolator.setValueFromTarget();
        }
        boot = false;
    }

    public void clientToogleDefaultOutput() {
        clientSend(ElectricalRelayElement.toogleOutputDefaultId);
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new ElectricalRelayGui(player, this);
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        if (lrdu == front) return EAU.smallInsulationLowCurrentRender;
        if (lrdu == front.left() || lrdu == front.right()) return descriptor.cable.render;
        return null;
    }
}
