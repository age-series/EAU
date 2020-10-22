package org.ja13.eau.sixnode.wirelesssignal.source;

import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.sixnode.electricalgatesource.ElectricalGateSourceRenderObj;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class WirelessSignalSourceDescriptor extends SixNodeDescriptor {

    int range;
    public boolean autoReset;
    ElectricalGateSourceRenderObj render;

    public WirelessSignalSourceDescriptor(String name,
                                          ElectricalGateSourceRenderObj render,
                                          int range, boolean autoReset) {
        super(name, WirelessSignalSourceElement.class, WirelessSignalSourceRender.class);
        this.range = range;
        this.autoReset = autoReset;
        this.render = render;

        voltageTier = VoltageTier.TTL;
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List<String> list) {
        super.addInfo(itemStack, entityPlayer, list);
        if (autoReset) {
            Collections.addAll(list, I18N.tr("Acts like a\npush button.").split("\n"));
        } else {
            Collections.addAll(list, I18N.tr("Acts like a\ntoggle switch.").split("\n"));
        }
    }

    void draw(float factor, float distance, TileEntity e) {
        render.draw(factor, distance, e);
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
            GL11.glScalef(1.5f, 1.5f, 1.5f);
            draw(0f, 1f, null);
        }
    }
}
