package org.ja13.eau.sixnode.thermalcable;

import net.minecraft.client.Minecraft;
import org.ja13.eau.cable.CableRender;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class ThermalCableRender extends SixNodeElementRender {

    ThermalCableDescriptor cableDesciptor;

    int color = 0;

    public ThermalCableRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.cableDesciptor = (ThermalCableDescriptor) descriptor;
    }

    public boolean drawCableAuto() {
        return false;
    }

    @Override
    public void draw() {
        Minecraft.getMinecraft().mcProfiler.startSection("TCable");
        Utils.setGlColorFromDye(color);
        UtilsClient.bindTexture(cableDesciptor.render.cableTexture);
        glListCall();
        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    @Override
    public void glListDraw() {
        CableRender.drawCable(cableDesciptor.render, connectedSide, CableRender.connectionType(this, side));
        CableRender.drawNode(cableDesciptor.render, connectedSide, CableRender.connectionType(this, side));
    }

    @Override
    public boolean glListEnable() {
        return true;
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            Byte b;
            b = stream.readByte();
            color = (b >> 4) & 0xF;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return cableDesciptor.render;
    }

    @Override
    public int getCableDry(LRDU lrdu) {
        return color;
    }
}
