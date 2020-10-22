package org.ja13.eau.sixnode.tutorialsign;

import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.node.six.SixNodeElementRender;
import org.ja13.eau.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class TutorialSignRender extends SixNodeElementRender {

    TutorialSignDescriptor descriptor;

    String text;
    String baliseName;
    String[] texts;

    RcInterpolator lightInterpol = new RcInterpolator(0.4f);

    public TutorialSignRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (TutorialSignDescriptor) descriptor;
    }

    @Override
    public void draw() {
        super.draw();
        descriptor.draw((float)lightInterpol.get());
    }

    @Override
    public void refresh(float deltaT) {
        lightInterpol.step(deltaT);
        super.refresh(deltaT);
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            baliseName = stream.readUTF();
            text = stream.readUTF();
            texts = text.split("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new TutorialSignGui(this);
    }
}
