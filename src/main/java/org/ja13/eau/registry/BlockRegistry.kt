package org.ja13.eau.registry

import cpw.mods.fml.common.registry.GameRegistry
import org.ja13.eau.EAU
import org.ja13.eau.Other
import org.ja13.eau.crafting.CraftingRegistry
import org.ja13.eau.i18n.I18N
import org.ja13.eau.node.NodeManager
import org.ja13.eau.node.simple.SimpleNodeItem
import org.ja13.eau.ore.DictTypes
import org.ja13.eau.ore.OreDescriptor
import org.ja13.eau.ore.OreInfo
import org.ja13.eau.simplenode.computerprobe.ComputerProbeBlock
import org.ja13.eau.simplenode.computerprobe.ComputerProbeEntity
import org.ja13.eau.simplenode.computerprobe.ComputerProbeNode
import org.ja13.eau.simplenode.energyconverter.EnergyConverterElnToOtherBlock
import org.ja13.eau.simplenode.energyconverter.EnergyConverterElnToOtherDescriptor
import org.ja13.eau.simplenode.energyconverter.EnergyConverterElnToOtherDescriptor.ElnDescriptor
import org.ja13.eau.simplenode.energyconverter.EnergyConverterElnToOtherDescriptor.Ic2Descriptor
import org.ja13.eau.simplenode.energyconverter.EnergyConverterElnToOtherDescriptor.OcDescriptor
import org.ja13.eau.simplenode.energyconverter.EnergyConverterElnToOtherEntity
import org.ja13.eau.simplenode.energyconverter.EnergyConverterElnToOtherNode
import net.minecraft.tileentity.TileEntity
import org.ja13.eau.misc.VoltageTier

class BlockRegistry {
    companion object {
        fun registerBlocks() {
            registerEnergyConverter()
            registerComputer()
            //registerOre()
        }

        private fun registerEnergyConverter() {
            if (EAU.ElnToOtherEnergyConverterEnable) {
                val entityName = "eau.EnergyConverterElnToOtherEntity"
                TileEntity.addMapping(org.ja13.eau.simplenode.energyconverter.EnergyConverterElnToOtherEntity::class.java, entityName)
                NodeManager.registerUuid(EnergyConverterElnToOtherNode.getNodeUuidStatic(), org.ja13.eau.simplenode.energyconverter.EnergyConverterElnToOtherNode::class.java)
                run {
                    val blockName = I18N.TR_NAME(I18N.Type.TILE, "eau.EnergyConverterElnToOtherLVUBlock")
                    val elnDesc = ElnDescriptor(VoltageTier.LOW.voltage, 2_000.0)
                    val ic2Desc = Ic2Descriptor(32.0, 1)
                    val ocDesc = OcDescriptor(ic2Desc.outMax * Other.elnToOcConversionRatio / Other.elnToIc2ConversionRatio)
                    val desc = EnergyConverterElnToOtherDescriptor("EnergyConverterElnToOtherLVU", elnDesc, ic2Desc, ocDesc)
                    EAU.elnToOtherBlockLvu = EnergyConverterElnToOtherBlock(desc)
                    EAU.elnToOtherBlockLvu.setCreativeTab(EAU.blockTab).setBlockName(blockName)
                    GameRegistry.registerBlock(EAU.elnToOtherBlockLvu, org.ja13.eau.node.simple.SimpleNodeItem::class.java, blockName)
                }
                run {
                    val blockName = I18N.TR_NAME(I18N.Type.TILE, "eau.EnergyConverterElnToOtherMVUBlock")
                    val elnDesc = ElnDescriptor(VoltageTier.HIGH_HOUSEHOLD.voltage, 5_000.0)
                    val ic2Desc = Ic2Descriptor(128.0, 2)
                    val ocDesc = OcDescriptor(ic2Desc.outMax * Other.elnToOcConversionRatio / Other.elnToIc2ConversionRatio)
                    val desc = EnergyConverterElnToOtherDescriptor("EnergyConverterElnToOtherMVU", elnDesc, ic2Desc, ocDesc)
                    EAU.elnToOtherBlockMvu = EnergyConverterElnToOtherBlock(desc)
                    EAU.elnToOtherBlockMvu.setCreativeTab(EAU.blockTab).setBlockName(blockName)
                    GameRegistry.registerBlock(EAU.elnToOtherBlockMvu, org.ja13.eau.node.simple.SimpleNodeItem::class.java, blockName)
                }
                run {
                    val blockName = I18N.TR_NAME(I18N.Type.TILE, "eau.EnergyConverterElnToOtherHVUBlock")
                    val elnDesc = ElnDescriptor(VoltageTier.INDUSTRIAL.voltage, 15_000.0)
                    val ic2Desc = Ic2Descriptor(512.0, 3)
                    val ocDesc = OcDescriptor(ic2Desc.outMax * Other.elnToOcConversionRatio / Other.elnToIc2ConversionRatio)
                    val desc = EnergyConverterElnToOtherDescriptor("EnergyConverterElnToOtherHVU", elnDesc, ic2Desc, ocDesc)
                    EAU.elnToOtherBlockHvu = EnergyConverterElnToOtherBlock(desc)
                    EAU.elnToOtherBlockHvu.setCreativeTab(EAU.blockTab).setBlockName(blockName)
                    GameRegistry.registerBlock(EAU.elnToOtherBlockHvu, org.ja13.eau.node.simple.SimpleNodeItem::class.java, blockName)
                }
            }
        }

        private fun registerComputer() {
            if (EAU.ComputerProbeEnable) {
                val entityName = I18N.TR_NAME(I18N.Type.TILE, "eau.ElnProbe")
                TileEntity.addMapping(org.ja13.eau.simplenode.computerprobe.ComputerProbeEntity::class.java, entityName)
                NodeManager.registerUuid(ComputerProbeNode.getNodeUuidStatic(), org.ja13.eau.simplenode.computerprobe.ComputerProbeNode::class.java)
                EAU.computerProbeBlock = ComputerProbeBlock()
                EAU.computerProbeBlock.setCreativeTab(EAU.blockTab).setBlockName(entityName)
                GameRegistry.registerBlock(EAU.computerProbeBlock, org.ja13.eau.node.simple.SimpleNodeItem::class.java, entityName)
            }
        }

        private fun registerOre() {
            var i = 1
            for (ore in OreInfo.values()) {
                val cleanName = ore.name.replace("Native ","").replace(" ", "")
                val desc = OreDescriptor(ore.name, i, 30, 6, 10,0,80)
                System.out.println("Adding (${i}) ore$cleanName to registry")
                EAU.oreItem.addDescriptor(i, desc)
                CraftingRegistry.addToOre("${DictTypes.ORE}$cleanName", desc.newItemStack())
                i += 1
            }

            /*run {
                id = 4
                name = I18N.TR_NAME(I18N.Type.NONE, "Lead Ore")
                val desc = OreDescriptor(name, id,
                    8 * if (Eln.genLead) 1 else 0, 3, 9, 0, 24
                )
                Eln.oreItem.addDescriptor(id, desc)
                CraftingRegistry.addToOre("oreLead", desc.newItemStack())
            }*/
        }
    }
}
