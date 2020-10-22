package org.ja13.eau.transparentnode.powerinductor;

import org.ja13.eau.cable.CableRenderType;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.cable.CableRenderType;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.node.transparent.TransparentNodeElementInventory;
import org.ja13.eau.node.transparent.TransparentNodeElementRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;

import java.io.DataInputStream;

public class PowerInductorRender extends TransparentNodeElementRender {

    public PowerInductorDescriptor descriptor;
    private CableRenderType renderPreProcess;

    public PowerInductorRender(TransparentNodeEntity tileEntity,
                               TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        this.descriptor = (PowerInductorDescriptor) descriptor;

    }


    @Override
    public void draw() {


        descriptor.draw();

    }

    @Override
    public void refresh(double deltaT) {

    }


    @Override
    public void networkUnserialize(DataInputStream stream) {

        super.networkUnserialize(stream);


	/*	try {


		} catch (IOException e) {
			
			e.printStackTrace();
		}*/

    }

    TransparentNodeElementInventory inventory = new TransparentNodeElementInventory(2, 64, this);

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {

        return new PowerInductorGui(player, inventory, this);
    }


}
