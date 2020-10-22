package org.ja13.eau.sixnode.lampsupply;

import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.cable.CableRenderType;
import org.ja13.eau.generic.GenericItemBlockUsingDamageDescriptor;
import org.ja13.eau.misc.*;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.cable.CableRenderType;
import org.ja13.eau.generic.GenericItemBlockUsingDamageDescriptor;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.PhysicalInterpolator;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementInventory;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LampSupplyRender extends SixNodeElementRender {

    LampSupplyDescriptor descriptor;

    Coordonate coord;
    PhysicalInterpolator interpolator;

    public ArrayList<LampSupplyElement.Entry> entries = new ArrayList<LampSupplyElement.Entry>();


    CableRenderDescriptor cableRender;

    SixNodeElementInventory inventory = new SixNodeElementInventory(1, 64, this);

    public LampSupplyRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (LampSupplyDescriptor) descriptor;
        interpolator = new PhysicalInterpolator(0.4f, 8.0f, 0.9f, 0.2f);
        coord = new Coordonate(tileEntity);
        for (int i = 0; i < ((LampSupplyDescriptor) descriptor).channelCount; i++) {
            entries.add(new LampSupplyElement.Entry("", "", 2));
        }
    }

    @Override
    public void draw() {
        super.draw();

        float[] pinDistances = new float[]{4.98f, 4.98f, 5.98f, 5.98f};

        if (side.isY()) {
            drawPowerPin(front.rotate4PinDistances(pinDistances));
            front.glRotateOnX();
        } else {
            drawPowerPin(pinDistances);
            LRDU.Down.glRotateOnX();
        }
        descriptor.draw((float)interpolator.get());
    }

    @Override
    public void refresh(float deltaT) {
        if (!Utils.isPlayerAround(tileEntity.getWorldObj(), coord.getAxisAlignedBB(0)))
            interpolator.setTarget(0f);
        else
            interpolator.setTarget(1f);

        interpolator.step(deltaT);
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return cableRender;
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new LampSupplyGui(this, player, inventory);
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            for (LampSupplyElement.Entry e : entries) {
                e.powerChannel = stream.readUTF();
                e.wirelessChannel = stream.readUTF();
                e.aggregator = stream.readChar();
            }

            ItemStack cableStack = Utils.unserializeItemStack(stream);
            if (cableStack != null) {
                GenericCableDescriptor desc = (GenericCableDescriptor) GenericItemBlockUsingDamageDescriptor.Companion.getDescriptor(cableStack);
                cableRender = desc.render;
            } else {
                cableRender = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newConnectionType(CableRenderType connectionType) {
        for (int idx = 0; idx < 4; idx++) {
            connectionType.startAt[idx] = 5 / 16f;
        }
    }
}
