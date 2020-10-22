package org.ja13.eau.transparentnode.computercraftio;

import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;

import javax.xml.crypto.Data;

public class ComputerCraftIoDescriptor extends TransparentNodeDescriptor {

    public ComputerCraftIoDescriptor(String name, Obj3D obj) {
        super(name, ComputerCraftIoElement.class, ComputerCraftIoRender.class);
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        draw(0, 1f);
    }

    void draw(int eggStackSize, float powerFactor) {
    }
}
