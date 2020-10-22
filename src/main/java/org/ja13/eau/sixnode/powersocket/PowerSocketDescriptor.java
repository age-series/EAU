package org.ja13.eau.sixnode.powersocket;

import org.ja13.eau.misc.*;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class PowerSocketDescriptor extends SixNodeDescriptor {

    private final Obj3D obj;
    private Obj3D.Obj3DPart base;
    private Obj3D.Obj3DPart socket;
    private final int subID;

    public int range;

    public PowerSocketDescriptor(int subID, String name, Obj3D obj, int range) {
        super(name, PowerSocketElement.class, PowerSocketRender.class);
        this.subID = subID;
        this.range = range;
        this.obj = obj;
        if (obj != null) {
            base = obj.getPart("SocketBase");
            switch (subID) {
                // TODO: Refactor this crap
                case 0:
                    socket = obj.getPart("Socket50V");
                    voltageTier = VoltageTier.LOW_HOUSEHOLD;
                    break;
                case 1:
                    socket = obj.getPart("Socket200V");
                    voltageTier = VoltageTier.HIGH_HOUSEHOLD;
                    break;
                default:
                    socket = null;
            }
        }
    }

    public void draw() {
        draw(0);
    }

    public void draw(int color) {
        //GL11.glRotatef(90.f,1.f,0.f,0.f);
        if (base != null)
            base.draw();
        if (socket != null) {
            Utils.setGlColorFromDye(color, 0.7f, 0.3f);
            socket.draw();
            GL11.glColor3f(1f, 1f, 1f);
        }
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
            draw();
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        Collections.addAll(list, I18N.tr("Supplies any device\nplugged in with energy.").split("\n"));
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return LRDU.Down;
    }
}
