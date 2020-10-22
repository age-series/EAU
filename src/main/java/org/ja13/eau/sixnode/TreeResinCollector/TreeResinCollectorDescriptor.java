package org.ja13.eau.sixnode.TreeResinCollector;

import org.ja13.eau.misc.*;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class TreeResinCollectorDescriptor extends SixNodeDescriptor {

    private final Obj3D obj;
    private Obj3D.Obj3DPart main, fill;

    float emptyS, emptyT;

    public TreeResinCollectorDescriptor(String name, Obj3D obj) {
        super(name, TreeResinCollectorElement.class, TreeResinCollectorRender.class);
        this.obj = obj;
        if (obj != null) {
            main = obj.getPart("main");
            fill = obj.getPart("fill");
            if (fill != null) {
                emptyT = fill.getFloat("emptyT");
                emptyS = fill.getFloat("emptyS");
            }
        }

        voltageTier = VoltageTier.NEUTRAL;
    }

    void draw(float factor) {
        if (main != null) main.draw();
        if (fill != null) {
            if (factor > 1f) factor = 1f;
            factor = (1f - factor);
            GL11.glTranslatef(0f, 0f, factor * emptyT);
            GL11.glScalef(1f - factor * (1f - emptyS), 1f - factor * (1f - emptyS), 1f);
            fill.draw();
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
            draw(0.0f);
        }
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        Collections.addAll(list, I18N.tr("Produces Tree Resin over\ntime when put on a tree.").split("\n"));
    }

    public static boolean isWood(Block b) {
        for (ItemStack s : OreDictionary.getOres("treeWood")) {
            if (s.getItem() == Item.getItemFromBlock(b)) return true;
        }
        for (ItemStack s : OreDictionary.getOres("logWood")) {
            if (s.getItem() == Item.getItemFromBlock(b)) return true;
        }

        return false;
    }

    public static boolean isLeaf(Block b) {
        for (ItemStack s : OreDictionary.getOres("treeLeaves")) {
            if (s.getItem() == Item.getItemFromBlock(b)) return true;
        }
        return false;
    }

    @Override
    public boolean canBePlacedOnSide(EntityPlayer player, Coordonate c, Direction side) {
        Block b = c.getBlock();
        if (!isWood(b) || side.isY()) {
            Utils.addChatMessage(player, I18N.tr("This block can only be placed on the side of a tree!"));
            return false;
        }
        return true;
    }
}
