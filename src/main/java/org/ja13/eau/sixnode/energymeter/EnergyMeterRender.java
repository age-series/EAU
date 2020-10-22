package org.ja13.eau.sixnode.energymeter;

import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.sixnode.energymeter.EnergyMeterElement.Mod;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;
import java.io.IOException;

public class EnergyMeterRender extends SixNodeElementRender {

    SixNodeElementInventory inventory = new SixNodeElementInventory(1, 64, this);
    EnergyMeterDescriptor descriptor;

    double timerCouter, energyStack;
    boolean switchState;
    String password;
    EnergyMeterElement.Mod mod;

    int energyUnit, timeUnit;

    CableRenderDescriptor cableRender;

    double power;
    double error;
    double serverPowerIdTimer = EnergyMeterElement.SlowProcess.publishTimeoutReset * 34;

    public EnergyMeterRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (EnergyMeterDescriptor) descriptor;

		/*for (int idx = 0; idx < energyRc.length; idx++) {
            energyRc[idx] = new RcInterpolator(0.2f);
		}*/
    }

    //RcInterpolator[] energyRc = new RcInterpolator[7];

    @Override
    public void draw() {
        super.draw();

        GL11.glPushMatrix();

        float[] pinDistances = descriptor.pinDistance;
        if (side.isY()) {
            pinDistances = front.rotate4PinDistances(pinDistances);
            front.left().glRotateOnX();
        }

        descriptor.draw(energyStack / Math.pow(10, energyUnit * 3 - 1), timerCouter / (timeUnit == 0 ? 360 : 8640),
            energyUnit, timeUnit,
            UtilsClient.distanceFromClientPlayer(tileEntity) < 20);

        GL11.glPopMatrix();

        GL11.glColor3f(0.9f, 0f, 0f);
        drawPowerPinWhite(front, pinDistances);
        GL11.glColor3f(0f, 0f, 0.9f);
        drawPowerPinWhite(front.inverse(), pinDistances);
        GL11.glColor3f(1f, 1f, 1f);
    }

    @Override
    public void refresh(float deltaT) {
        double errorComp = error * 1 * deltaT;
        energyStack += power * deltaT + errorComp;
        error -= errorComp;

		/*double stack = energyStack;
		for (int idx = 0; idx < energyRc.length; idx++) {

			energyRc[idx].setTarget((float) ((stack) % 10));
			energyRc[idx].step(deltaT);
			stack /= 10.0;
		}*/

        timerCouter += deltaT * 72;
        serverPowerIdTimer += deltaT;
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return cableRender;
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);

        try {
            switchState = stream.readBoolean();
            password = stream.readUTF();
            mod = EnergyMeterElement.Mod.valueOf(stream.readUTF());
            timerCouter = stream.readDouble();
            // energyStack = stream.readDouble();
            ItemStack itemStack = Utils.unserializeItemStack(stream);
            energyUnit = stream.readByte();
            timeUnit = stream.readByte();
            if (itemStack != null) {
                GenericCableDescriptor desc = (GenericCableDescriptor) GenericCableDescriptor.getDescriptor(itemStack, GenericCableDescriptor.class);
                if (desc == null)
                    cableRender = null;
                else
                    cableRender = desc.render;
            } else {
                cableRender = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new EnergyMeterGui(player, inventory, this);
    }

    @Override
    public void serverPacketUnserialize(DataInputStream stream) throws IOException {
        super.serverPacketUnserialize(stream);

        switch (stream.readByte()) {
            case EnergyMeterElement.serverPowerId:
                if (serverPowerIdTimer > EnergyMeterElement.SlowProcess.publishTimeoutReset * 3) {
                    energyStack = stream.readDouble();
                    error = 0;
                } else {
                    error = stream.readDouble() - energyStack;
                }
                power = stream.readDouble();
                serverPowerIdTimer = 0;
                break;
            default:
                break;
        }
    }
}
