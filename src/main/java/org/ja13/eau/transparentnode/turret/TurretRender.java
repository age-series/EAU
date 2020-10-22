package org.ja13.eau.transparentnode.turret;

import org.ja13.eau.item.EntitySensorFilterDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.item.EntitySensorFilterDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import org.lwjgl.opengl.GL11;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TurretRender extends TransparentNodeElementRender {

    private final TurretDescriptor descriptor;
    private final TurretMechanicsSimulation simulation;
    private final TransparentNodeElementInventory inventory = new TransparentNodeElementInventory(1, 1, this);
    EntitySensorFilterDescriptor filter = null;
    boolean filterIsSpare;
    double chargePower;

    public TurretRender(TransparentNodeEntity tileEntity,
                        TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        this.descriptor = (TurretDescriptor) descriptor;
        simulation = new TurretMechanicsSimulation(this.descriptor);
    }

    public double getTurretAngle() {
        return simulation.getTurretAngle();
    }

    public double getGunPosition() {
        return simulation.getGunPosition();
    }

    public double getGunElevation() {
        return simulation.getGunElevation();
    }

    public boolean isShooting() {
        return simulation.isShooting();
    }

    public boolean isEnabled() {
        return simulation.isEnabled();
    }

    public void clientToggleFilterMeaning() {
        clientSendId(TurretElement.ToggleFilterMeaning);
    }

    public void clientSetChargePower(float power) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(bos);

            preparePacketForServer(stream);

            stream.writeByte(TurretElement.UnserializeChargePower);
            stream.writeFloat(power);

            sendPacketToServer(bos);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void draw() {
        GL11.glPushMatrix();
        front.glRotateXnRef();
        descriptor.draw(this);
        GL11.glPopMatrix();
    }

    @Override
    public void refresh(double deltaT) {
        super.refresh(deltaT);
        simulation.process(deltaT);
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            simulation.setTurretAngle(stream.readDouble());
            simulation.setGunPosition(stream.readDouble());
            simulation.setGunElevation(stream.readDouble());
            simulation.setSeekMode(stream.readBoolean());
            if (stream.readBoolean()) simulation.shoot();
            simulation.setEnabled(stream.readBoolean());
            ItemStack filterStack = Utils.unserializeItemStack(stream);
            filter = (EntitySensorFilterDescriptor) EntitySensorFilterDescriptor.getDescriptor(filterStack);
            filterIsSpare = stream.readBoolean();
            chargePower = stream.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new TurretGui(player, inventory, this);
    }
}

