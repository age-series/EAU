package org.ja13.eau.item.electricalitem

import org.ja13.eau.EAU
import org.ja13.eau.item.electricalitem.PortableOreScannerItem.RenderStorage.OreScannerConfigElement
import org.ja13.eau.misc.Utils
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraftforge.oredict.OreDictionary

class OreScannerTasks {
    companion object {
        fun regenOreScannerFactors() {
            org.ja13.eau.item.electricalitem.PortableOreScannerItem.RenderStorage.blockKeyFactor = null
            org.ja13.eau.EAU.oreScannerConfig.clear()
            if (org.ja13.eau.EAU.addOtherModOreToXRay) {
                for (name in OreDictionary.getOreNames()) {
                    if (name == null) continue
                    // Utils.println(name + " " +
                    // OreDictionary.getOreID(name));
                    if (name.startsWith("ore")) {
                        for (stack in OreDictionary.getOres(name)) {
                            val id = Utils.getItemId(stack) + 4096 * stack.item.getMetadata(stack.itemDamage)
                            // Utils.println(OreDictionary.getOreID(name));
                            var find = false
                            for (c in org.ja13.eau.EAU.oreScannerConfig) {
                                if (c.blockKey == id) {
                                    find = true
                                    break
                                }
                            }
                            if (!find) {
                                Utils.println("$id added to xRay (other mod)")
                                org.ja13.eau.EAU.oreScannerConfig.add(OreScannerConfigElement(id, 0.15f))
                            }
                        }
                    }
                }
            }
            org.ja13.eau.EAU.oreScannerConfig.add(OreScannerConfigElement(Block.getIdFromBlock(Blocks.coal_ore), 5 / 100f))
            org.ja13.eau.EAU.oreScannerConfig.add(OreScannerConfigElement(Block.getIdFromBlock(Blocks.iron_ore), 15 / 100f))
            org.ja13.eau.EAU.oreScannerConfig.add(OreScannerConfigElement(Block.getIdFromBlock(Blocks.gold_ore), 40 / 100f))
            org.ja13.eau.EAU.oreScannerConfig.add(OreScannerConfigElement(Block.getIdFromBlock(Blocks.lapis_ore), 40 / 100f))
            org.ja13.eau.EAU.oreScannerConfig.add(OreScannerConfigElement(Block.getIdFromBlock(Blocks.redstone_ore), 40 / 100f))
            org.ja13.eau.EAU.oreScannerConfig.add(OreScannerConfigElement(Block.getIdFromBlock(Blocks.diamond_ore), 100 / 100f))
            org.ja13.eau.EAU.oreScannerConfig.add(OreScannerConfigElement(Block.getIdFromBlock(Blocks.emerald_ore), 40 / 100f))
            org.ja13.eau.EAU.oreScannerConfig.add(OreScannerConfigElement(Block.getIdFromBlock(org.ja13.eau.EAU.oreBlock) + (1 shl 12), 10 / 100f))
            org.ja13.eau.EAU.oreScannerConfig.add(OreScannerConfigElement(Block.getIdFromBlock(org.ja13.eau.EAU.oreBlock) + (4 shl 12), 20 / 100f))
            org.ja13.eau.EAU.oreScannerConfig.add(OreScannerConfigElement(Block.getIdFromBlock(org.ja13.eau.EAU.oreBlock) + (5 shl 12), 20 / 100f))
            org.ja13.eau.EAU.oreScannerConfig.add(OreScannerConfigElement(Block.getIdFromBlock(org.ja13.eau.EAU.oreBlock) + (6 shl 12), 20 / 100f))
        }
    }
}
