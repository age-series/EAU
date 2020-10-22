package org.ja13.eau.sixnode.electricalalarm;

import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.misc.Utils;
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
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class ElectricalAlarmRender extends SixNodeElementRender {

    ElectricalAlarmDescriptor descriptor;

    LRDU front;

    RcInterpolator interpol = new RcInterpolator(0.4f);

    float rotAlpha = 0;
    boolean warm = false;
    boolean mute = false;

    public ElectricalAlarmRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (ElectricalAlarmDescriptor) descriptor;
    }

    @Override
    public void draw() {
        super.draw();


        if (side.isY()) {
            front.right().glRotateOnX();
            drawSignalPin(LRDU.Down, descriptor.pinDistance);
        } else {
            drawSignalPin(front, descriptor.pinDistance);
        }
        descriptor.draw(warm, rotAlpha);
    }

    @Override
    public void refresh(float deltaT) {
        interpol.setTarget(warm ? descriptor.rotSpeed : 0f);
        interpol.step(deltaT);

        rotAlpha += interpol.get() * deltaT;
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            Byte b;
            b = stream.readByte();
            front = LRDU.fromInt((b >> 4) & 3);
            warm = (b & 1) != 0;
            mute = stream.readBoolean();
            Utils.println("WARM : " + warm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return EAU.smallInsulationLowCurrentRender;
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new ElectricalAlarmGui(player, this);
    }
}
