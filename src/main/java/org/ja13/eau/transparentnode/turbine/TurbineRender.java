package org.ja13.eau.transparentnode.turbine;

import org.ja13.eau.cable.CableRender;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.cable.CableRenderType;
import org.ja13.eau.misc.*;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import org.ja13.eau.sound.LoopedSound;
import net.minecraft.client.audio.ISound;
import org.ja13.eau.cable.CableRender;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.cable.CableRenderType;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.LRDUMask;
import org.ja13.eau.misc.SlewLimiter;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import org.ja13.eau.sound.LoopedSound;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;
import java.io.IOException;


public class TurbineRender extends TransparentNodeElementRender {
    private final TurbineDescriptor descriptor;

    private CableRenderType connectionType;
    private final SlewLimiter factorLimiter = new SlewLimiter(0.2f);

    private boolean cableRefresh;
    private final LRDUMask eConn = new LRDUMask();
    private final LRDUMask maskTemp = new LRDUMask();

    public TurbineRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        this.descriptor = (TurbineDescriptor) descriptor;
        addLoopedSound(new LoopedSound(this.descriptor.soundFile, coordonate(), ISound.AttenuationType.LINEAR) {
            @Override
            public float getVolume() {
                return 0.1f * (float)factorLimiter.getPosition();
            }

            @Override
            public float getPitch() {
                return 0.9f + 0.2f * (float)factorLimiter.getPosition();
            }
        });
    }

    @Override
    public void draw() {
        GL11.glPushMatrix();
        front.glRotateXnRef();
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        descriptor.draw();
        GL11.glPopMatrix();

        if (cableRefresh) {
            cableRefresh = false;
            connectionType = CableRender.connectionType(tileEntity, eConn, front.down());
        }

        glCableTransforme(front.down());
        descriptor.eRender.bindCableTexture();

        for (LRDU lrdu : LRDU.values()) {
            Utils.setGlColorFromDye(connectionType.otherdry[lrdu.toInt()]);
            if (!eConn.get(lrdu)) continue;
            if (lrdu != front.down().getLRDUGoingTo(front) && lrdu.inverse() != front.down().getLRDUGoingTo(front))
                continue;
            maskTemp.set(1 << lrdu.toInt());
            CableRender.drawCable(descriptor.eRender, maskTemp, connectionType);
        }
    }

    @Override
    public CableRenderDescriptor getCableRender(Direction side, LRDU lrdu) {
        if (lrdu == LRDU.Down) {
            if (side == front) return descriptor.eRender;
            if (side == front.back()) return descriptor.eRender;
        }
        return null;
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        eConn.deserialize(stream);
        cableRefresh = true;
        try {
            float deltaT = stream.readFloat();
            if (deltaT >= 40) {
                factorLimiter.setTarget((float) (deltaT / TurbineRender.this.descriptor.nominalDeltaT));
            } else {
                factorLimiter.setTarget(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh(double deltaT) {
        factorLimiter.step(deltaT);
        super.refresh(deltaT);
    }
}
