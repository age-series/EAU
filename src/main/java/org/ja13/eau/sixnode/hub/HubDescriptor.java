package org.ja13.eau.sixnode.hub;

import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class HubDescriptor extends SixNodeDescriptor {

    Obj3D obj;
    Obj3D.Obj3DPart main;
    Obj3D.Obj3DPart[] connection = new Obj3D.Obj3DPart[6];

    public HubDescriptor(String name, Obj3D obj) {
        super(name, HubElement.class, HubRender.class);
        this.obj = obj;
        if (obj != null) {
            main = obj.getPart("main");
            for (int idx = 0; idx < 6; idx++) {
                connection[idx] = obj.getPart("con" + idx);
            }
        }
        voltageTier = VoltageTier.NEUTRAL;
    }

    void draw(boolean[] connectionGrid) {
        if (main != null) main.draw();
        for (int idx = 0; idx < 6; idx++) {
            if (connectionGrid[idx])
                GL11.glColor3f(40 / 255f, 40 / 255f, 40 / 255f);
            else
                GL11.glColor3f(150 / 255f, 150 / 255f, 150 / 255f);

            if (connection[idx] != null) connection[idx].draw();
        }
        GL11.glColor3f(1, 1, 1);
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        Collections.addAll(list, I18N.tr("Allows crossing cables\non one single block.").split("\n"));
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return LRDU.Up;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelperEln(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        }else {
            draw(new boolean[6]);
        }
    }
}
