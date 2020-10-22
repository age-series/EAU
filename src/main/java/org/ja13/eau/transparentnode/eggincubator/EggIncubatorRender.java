package org.ja13.eau.transparentnode.eggincubator;

import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.cable.CableRenderType;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.LRDUMask;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.cable.CableRenderType;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.LRDUMask;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;
import java.io.IOException;

public class EggIncubatorRender extends TransparentNodeElementRender {

    TransparentNodeElementInventory inventory = new EggIncubatorInventory(1, 64, this);
    EggIncubatorDescriptor descriptor;

    float alpha = 0;

    byte eggStackSize;

    EntityItem egg;
    public float voltage;

    LRDUMask priConn = new LRDUMask(), secConn = new LRDUMask(), eConn = new LRDUMask();
    CableRenderType cableRenderType;

    public EggIncubatorRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        this.descriptor = (EggIncubatorDescriptor) descriptor;
    }

    @Override
    public void draw() {
        GL11.glPushMatrix();
        front.glRotateXnRef();
        if (egg != null) {
            UtilsClient.drawEntityItem(egg, 0.0f, -0.3f, 0.13f, alpha, 0.6f);
        }
        descriptor.draw(eggStackSize, (float) (voltage / descriptor.nominalVoltage));
        GL11.glPopMatrix();
        cableRenderType = drawCable(front.down(), descriptor.cable.render, eConn, cableRenderType);
    }

    @Override
    public void refresh(double deltaT) {
        alpha += deltaT * 60;
        if (alpha >= 360) alpha -= 360;
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new EggIncubatorGuiDraw(player, inventory, this);
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            eggStackSize = stream.readByte();
            if (eggStackSize != 0) {
                egg = new EntityItem(this.tileEntity.getWorldObj(), 0, 0, 0, new ItemStack(Items.egg));
            } else {
                egg = null;
            }
            eConn.deserialize(stream);
            voltage = stream.readFloat();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cableRenderType = null;
    }

    @Override
    public CableRenderDescriptor getCableRender(Direction side, LRDU lrdu) {
        return descriptor.cable.render;
    }

    @Override
    public void notifyNeighborSpawn() {
        super.notifyNeighborSpawn();
        cableRenderType = null;
    }
}
