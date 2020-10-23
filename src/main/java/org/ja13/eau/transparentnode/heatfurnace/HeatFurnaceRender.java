package org.ja13.eau.transparentnode.heatfurnace;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.PhysicalInterpolator;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HeatFurnaceRender extends TransparentNodeElementRender {

    double temperature;
    float gainSyncValue = -1234, temperatureTargetSyncValue = -1234;
    boolean gainSyncNew = false, temperatureTargetSyncNew = false;
    short power;

    public boolean controleExternal, takeFuel;

    HeatFurnaceDescriptor descriptor;

    Coordonate coord;
    PhysicalInterpolator interpolator;

    float counter = 0;

    TransparentNodeElementInventory inventory = new HeatFurnaceInventory(4, 64, this);

    boolean boot = true;

    EntityItem entityItemIn;

    public HeatFurnaceRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        this.descriptor = (HeatFurnaceDescriptor) descriptor;
        interpolator = new PhysicalInterpolator(0.4f, 8.0f, 0.9f, 0.2f);
        coord = new Coordonate(tileEntity);
    }

    @Override
    public void draw() {
        front.glRotateXnRef();
        descriptor.draw((float)interpolator.get());

        if (entityItemIn != null)
            drawEntityItem(entityItemIn, -0.1, -0.30, 0, counter, 0.8f);
    }

    @Override
    public void refresh(double deltaT) {
        if (!Utils.isPlayerAround(tileEntity.getWorldObj(), coord.getAxisAlignedBB(1)))
            interpolator.setTarget(0f);
        else
            interpolator.setTarget(1f);
        interpolator.step(deltaT);

        counter += deltaT * 60;
        if (counter >= 360f)
            counter -= 360;
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new HeatFurnaceGuiDraw(player, inventory, this);
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            controleExternal = stream.readBoolean();
            takeFuel = stream.readBoolean();
            temperature = stream.readDouble();
            float readF;
            readF = stream.readFloat();
            if (gainSyncValue != readF || controleExternal) {
                gainSyncValue = readF;
                gainSyncNew = true;
            }
            readF = stream.readFloat();
            if (temperatureTargetSyncValue != readF || controleExternal) {
                temperatureTargetSyncValue = readF;
                temperatureTargetSyncNew = true;
            }

            power = stream.readShort();

            entityItemIn = unserializeItemStackToEntityItem(stream, entityItemIn);

            if (boot) {
                coord.move(front);
                boot = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientToogleControl() {
        clientSendId(HeatFurnaceElement.unserializeToogleControlExternalId);
    }

    public void clientToogleTakeFuel() {
        clientSendId(HeatFurnaceElement.unserializeToogleTakeFuelId);
    }

    public void clientSetGain(float value) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(bos);

            preparePacketForServer(stream);

            stream.writeByte(HeatFurnaceElement.unserializeGain);
            stream.writeFloat(value);

            sendPacketToServer(bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clientSetTemperatureTarget(float value) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(bos);

            preparePacketForServer(stream);

            stream.writeByte(HeatFurnaceElement.unserializeTemperatureTarget);
            stream.writeFloat(value);

            sendPacketToServer(bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean cameraDrawOptimisation() {
        return false;
    }
}
