package org.ja13.eau.node.six;

import org.ja13.eau.node.ISixNodeCache;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.node.ISixNodeCache;

public class SixNodeCacheStd implements ISixNodeCache {

    @Override
    public boolean accept(ItemStack stack) {

        Block b = Block.getBlockFromItem(stack.getItem());
        if (b == null) return false;
        if (b instanceof BlockContainer) return false;
        if (stack.getItem() instanceof SixNodeItem) return false;
        switch(b.getRenderType()) {
            case 0: return true;
            case 31: return true;  // Logs
            case 39: return true;  // Quartz block
            default: return false;
        }
    }

    @Override
    public int getMeta(ItemStack stack) {

        return stack.getItemDamage();
    }

}
