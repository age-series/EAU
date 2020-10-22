package org.ja13.eau.transparentnode.electricalantennatx;

import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRender;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.cable.CableRenderType;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.LRDUMask;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import org.ja13.eau.EAU;
import org.ja13.eau.cable.CableRender;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.cable.CableRenderType;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.LRDUMask;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;

public class ElectricalAntennaTxRender extends TransparentNodeElementRender {

    ElectricalAntennaTxDescriptor descriptor;

    LRDUMask maskTemp = new LRDUMask();
    LRDU rot;

    LRDUMask lrduConnection = new LRDUMask();
    CableRenderType connectionType;
    boolean cableRefresh = false;

    public ElectricalAntennaTxRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        this.descriptor = (ElectricalAntennaTxDescriptor) descriptor;
    }

    @Override
    public void draw() {
        GL11.glPushMatrix();
        front.glRotateXnRef();
        rot.glRotateOnX();
        descriptor.draw();
        GL11.glPopMatrix();

        glCableTransforme(front.getInverse());
        descriptor.cable.bindCableTexture();

        if (cableRefresh) {
            cableRefresh = false;
            connectionType = CableRender.connectionType(tileEntity, lrduConnection, front.getInverse());
        }

        for (LRDU lrdu : LRDU.values()) {
            Utils.setGlColorFromDye(connectionType.otherdry[lrdu.toInt()]);
            if (!lrduConnection.get(lrdu)) continue;
            maskTemp.set(1 << lrdu.toInt());
            if (lrdu == rot)
                CableRender.drawCable(descriptor.cable.render, maskTemp, connectionType);
            else if (lrdu == rot.left() || lrdu == rot.right())
                CableRender.drawCable(EAU.smallInsulationLowCurrentRender, maskTemp, connectionType);
        }
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        rot = LRDU.deserialize(stream);
        lrduConnection.deserialize(stream);
        cableRefresh = true;
    }

    @Override
    public CableRenderDescriptor getCableRender(Direction side, LRDU lrdu) {
        if (front.getInverse() != side.applyLRDU(lrdu)) return null;

        if (side == front.applyLRDU(rot)) return descriptor.cable.render;
        if (side == front.applyLRDU(rot.left())) return EAU.smallInsulationLowCurrentRender;
        if (side == front.applyLRDU(rot.right())) return EAU.smallInsulationLowCurrentRender;
        return null;
    }

    @Override
    public void notifyNeighborSpawn() {
        super.notifyNeighborSpawn();
        cableRefresh = true;
    }
}
