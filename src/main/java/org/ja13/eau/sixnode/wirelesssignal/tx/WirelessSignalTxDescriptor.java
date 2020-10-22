package org.ja13.eau.sixnode.wirelesssignal.tx;

import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.lwjgl.opengl.GL11;

public class WirelessSignalTxDescriptor extends SixNodeDescriptor {

    private final Obj3D obj;
    Obj3D.Obj3DPart main;

    int range;

    public WirelessSignalTxDescriptor(String name,
                                      Obj3D obj,
                                      int range) {
        super(name, WirelessSignalTxElement.class, WirelessSignalTxRender.class);
        this.range = range;
        this.obj = obj;
        if (obj != null) main = obj.getPart("main");

        voltageTier = VoltageTier.TTL;
    }

    public void draw() {
        if (main != null) main.draw();
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelperEln(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        } else {
            if (type == ItemRenderType.ENTITY) {
                //	GL11.glTranslatef(1.0f, 0f, 0f);
                GL11.glScalef(2.8f, 2.8f, 2.8f);
            }
            draw();
        }
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).inverse();
    }
}
