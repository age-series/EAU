package org.ja13.eau.registry

import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item.ToolMaterial
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemArmor.ArmorMaterial
import net.minecraft.item.ItemHoe
import net.minecraft.item.ItemSpade
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.oredict.OreDictionary
import org.ja13.eau.EAU
import org.ja13.eau.crafting.CraftingRegistry
import org.ja13.eau.generic.GenericItemUsingDamageDescriptor
import org.ja13.eau.generic.GenericItemUsingDamageDescriptorWithComment
import org.ja13.eau.generic.genericArmorItem
import org.ja13.eau.generic.genericArmorItem.ArmourType
import org.ja13.eau.i18n.I18N
import org.ja13.eau.item.BrushDescriptor
import org.ja13.eau.item.CaseItemDescriptor
import org.ja13.eau.item.CombustionChamber
import org.ja13.eau.item.ConfigCopyToolDescriptor
import org.ja13.eau.item.CopperCableDescriptor
import org.ja13.eau.item.DielectricItem
import org.ja13.eau.item.ElectricalDrillDescriptor
import org.ja13.eau.item.ElectricalFuseDescriptor
import org.ja13.eau.item.EntitySensorFilterDescriptor
import org.ja13.eau.item.FerromagneticCoreDescriptor
import org.ja13.eau.item.FuelBurnerDescriptor
import org.ja13.eau.item.GraphiteDescriptor
import org.ja13.eau.item.HeatingCorpElement
import org.ja13.eau.item.ItemAxeEln
import org.ja13.eau.item.ItemPickaxeEln
import org.ja13.eau.item.LampDescriptor
import org.ja13.eau.item.MachineBoosterDescriptor
import org.ja13.eau.item.MiningPipeDescriptor
import org.ja13.eau.item.OreScanner
import org.ja13.eau.item.OverHeatingProtectionDescriptor
import org.ja13.eau.item.OverVoltageProtectionDescriptor
import org.ja13.eau.item.SolarTrackerDescriptor
import org.ja13.eau.item.TreeResin
import org.ja13.eau.item.electricalitem.BatteryItem
import org.ja13.eau.item.electricalitem.ElectricalArmor
import org.ja13.eau.item.electricalitem.ElectricalAxe
import org.ja13.eau.item.electricalitem.ElectricalLampItem
import org.ja13.eau.item.electricalitem.ElectricalPickaxe
import org.ja13.eau.item.electricalitem.PortableOreScannerItem
import org.ja13.eau.item.regulator.RegulatorAnalogDescriptor
import org.ja13.eau.item.regulator.RegulatorOnOffDescriptor
import org.ja13.eau.mechanical.ClutchPinItem
import org.ja13.eau.mechanical.ClutchPlateItem
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.sixnode.electricaldatalogger.DataLogsPrintDescriptor
import org.ja13.eau.sixnode.lampsocket.LampSocketType
import org.ja13.eau.sixnode.wirelesssignal.WirelessSignalAnalyserItemDescriptor

class ItemRegistry {
    companion object {

        enum class IID (val id: Int) {
            HEATING_CORES(0),
            REGULATORS(1),
            LAMPS(2),
            PROTECTION(3),
            COMBUSTION_CHAMBER(4),
            FERROMAGNETIC_CORES(5),
            INGOTS(6),
            DUSTS(7),
            MOTOR_ITEM(8),
            SOLAR_TRACKER(9),
            METERS(10),
            DRILLS(11),
            ORE_SCANNER(12),
            MINING_PIPES(13),
            RESIN_RUBBER(14),
            RAW_CABLE(15),
            ARC_ITEMS(16),
            BRUSHES(17),
            MISC(18),
            ELECTRICAL_TOOLS(19),
            PORTABLE(20),
            FUEL_BURNERS(21),
            FUSES(22)
        }

        fun register() {
            registerHeatingCorp(IID.HEATING_CORES.id)
            registerRegulatorItem(IID.REGULATORS.id)
            registerLampItem(IID.LAMPS.id)
            registerProtection(IID.PROTECTION.id)
            registerCombustionChamber(IID.COMBUSTION_CHAMBER.id)
            registerFerromagneticCore(IID.FERROMAGNETIC_CORES.id)
            registerIngot(IID.INGOTS.id)
            registerDust(IID.DUSTS.id)
            registerElectricalMotor(IID.MOTOR_ITEM.id)
            registerSolarTracker( IID.SOLAR_TRACKER.id)
            registerMeter(IID.METERS.id)
            registerElectricalDrill(IID.DRILLS.id)
            registerOreScanner(IID.ORE_SCANNER.id)
            registerMiningPipe(IID.MINING_PIPES.id)
            registerTreeResinAndRubber(IID.RESIN_RUBBER.id)
            registerRawCable(IID.RAW_CABLE.id)
            registerArc(IID.ARC_ITEMS.id)
            registerBrush(IID.BRUSHES.id)
            registerMiscItem(IID.MISC.id)
            registerElectricalTool(IID.ELECTRICAL_TOOLS.id)
            registerPortableItem(IID.PORTABLE.id)
            registerFuelBurnerItem(IID.FUEL_BURNERS.id)
            registerFuses(IID.FUSES.id)

            registerArmor()
            registerTool()
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun registerItem(group: Int, subId: Int, element: GenericItemUsingDamageDescriptor) {
            EAU.sharedItem.addElement(subId + (group shl 6), element)
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun registerHiddenItem(group: Int, subId: Int, element: GenericItemUsingDamageDescriptor) {
            EAU.sharedItem.addWithoutRegistry(subId + (group shl 6), element)
        }

        private fun registerArmor() {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "Copper Helmet")
                EAU.helmetCopper = genericArmorItem(ArmorMaterial.IRON, 2, ArmourType.Helmet, "eau:textures/armor/copper_layer_1.png", "eau:textures/armor/copper_layer_2.png").setUnlocalizedName(name).setTextureName("eau:copper_helmet").setCreativeTab(EAU.itemTab) as ItemArmor
                GameRegistry.registerItem(EAU.helmetCopper, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.helmetCopper))
            }
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "Copper Chestplate")
                EAU.plateCopper = genericArmorItem(ArmorMaterial.IRON, 2, ArmourType.Chestplate, "eau:textures/armor/copper_layer_1.png", "eau:textures/armor/copper_layer_2.png").setUnlocalizedName(name).setTextureName("eau:copper_chestplate").setCreativeTab(EAU.itemTab) as ItemArmor
                GameRegistry.registerItem(EAU.plateCopper, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.plateCopper))
            }
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "Copper Leggings")
                EAU.legsCopper = genericArmorItem(ArmorMaterial.IRON, 2, ArmourType.Leggings, "eau:textures/armor/copper_layer_1.png", "eau:textures/armor/copper_layer_2.png").setUnlocalizedName(name).setTextureName("eau:copper_leggings").setCreativeTab(EAU.itemTab) as ItemArmor
                GameRegistry.registerItem(EAU.legsCopper, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.legsCopper))
            }
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "Copper Boots")
                EAU.bootsCopper = genericArmorItem(ArmorMaterial.IRON, 2, ArmourType.Boots, "eau:textures/armor/copper_layer_1.png", "eau:textures/armor/copper_layer_2.png").setUnlocalizedName(name).setTextureName("eau:copper_boots").setCreativeTab(EAU.itemTab) as ItemArmor
                GameRegistry.registerItem(EAU.bootsCopper, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.bootsCopper))
            }
            val t1 = "eau:textures/armor/ecoal_layer_1.png"
            val t2 = "eau:textures/armor/ecoal_layer_2.png"
            val energyPerDamage = 500.0
            var armor: Int
            val eCoalMaterial = EnumHelper.addArmorMaterial("ECoal", 10, intArrayOf(3, 8, 6, 3), 9)
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "E-Coal Helmet")
                armor = 3
                EAU.helmetECoal = ElectricalArmor(eCoalMaterial, 2, ArmourType.Helmet, t1, t2,
                    8000.0, 2000.0,
                    armor / 20.0, armor * energyPerDamage,
                    energyPerDamage
                ).setUnlocalizedName(name).setTextureName("eau:ecoal_helmet").setCreativeTab(EAU.itemTab) as ItemArmor
                GameRegistry.registerItem(EAU.helmetECoal, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.helmetECoal))
            }
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "E-Coal Chestplate")
                armor = 8
                EAU.plateECoal = ElectricalArmor(eCoalMaterial, 2, ArmourType.Chestplate, t1, t2,
                    8000.0, 2000.0,
                    armor / 20.0, armor * energyPerDamage,
                    energyPerDamage
                ).setUnlocalizedName(name).setTextureName("eau:ecoal_chestplate").setCreativeTab(EAU.itemTab) as ItemArmor
                GameRegistry.registerItem(EAU.plateECoal, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.plateECoal))
            }
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "E-Coal Leggings")
                armor = 6
                EAU.legsECoal = ElectricalArmor(eCoalMaterial, 2, ArmourType.Leggings, t1, t2,
                    8000.0, 2000.0,  // double
                    armor / 20.0, armor * energyPerDamage,
                    energyPerDamage
                ).setUnlocalizedName(name).setTextureName("eau:ecoal_leggings").setCreativeTab(EAU.itemTab) as ItemArmor
                GameRegistry.registerItem(EAU.legsECoal, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.legsECoal))
            }
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "E-Coal Boots")
                armor = 3
                EAU.bootsECoal = ElectricalArmor(eCoalMaterial, 2, ArmourType.Boots, t1, t2,
                    8000.0, 2000.0,
                    armor / 20.0, armor * energyPerDamage,
                    energyPerDamage
                ).setUnlocalizedName(name).setTextureName("eau:ecoal_boots").setCreativeTab(EAU.itemTab) as ItemArmor
                GameRegistry.registerItem(EAU.bootsECoal, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.bootsECoal))
            }
        }

        private fun registerTool() {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "Copper Sword")
                EAU.swordCopper = ItemSword(ToolMaterial.IRON).setUnlocalizedName(name).setTextureName("eau:copper_sword").setCreativeTab(EAU.itemTab)
                GameRegistry.registerItem(EAU.swordCopper, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.swordCopper))
            }
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "Copper Hoe")
                EAU.hoeCopper = ItemHoe(ToolMaterial.IRON).setUnlocalizedName(name).setTextureName("eau:copper_hoe").setCreativeTab(EAU.itemTab)
                GameRegistry.registerItem(EAU.hoeCopper, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.hoeCopper))
            }
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "Copper Shovel")
                EAU.shovelCopper = ItemSpade(ToolMaterial.IRON).setUnlocalizedName(name).setTextureName("eau:copper_shovel").setCreativeTab(EAU.itemTab)
                GameRegistry.registerItem(EAU.shovelCopper, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.shovelCopper))
            }
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "Copper Pickaxe")
                EAU.pickaxeCopper = ItemPickaxeEln(ToolMaterial.IRON).setUnlocalizedName(name).setTextureName("eau:copper_pickaxe").setCreativeTab(EAU.itemTab)
                GameRegistry.registerItem(EAU.pickaxeCopper, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.pickaxeCopper))
            }
            run {
                name = I18N.TR_NAME(I18N.Type.ITEM, "Copper Axe")
                EAU.axeCopper = ItemAxeEln(ToolMaterial.IRON).setUnlocalizedName(name).setTextureName("eau:copper_axe").setCreativeTab(EAU.itemTab)
                GameRegistry.registerItem(EAU.axeCopper, "Eln.$name")
                GameRegistry.registerCustomItemStack(name, ItemStack(EAU.axeCopper))
            }
        }

        private fun registerHeatingCorp(id: Int) {
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "Small 12V Copper Heating Corp"),
                    VoltageTier.LOW.voltage, 150.0,
                    190.0)
                registerItem(id, 0, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "12V Copper Heating Corp"),
                    VoltageTier.LOW.voltage, 250.0,
                    320.0)
                registerItem(id, 1, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "Small 120V Copper Heating Corp"),
                    VoltageTier.LOW_HOUSEHOLD.voltage, 400.0,
                    500.0)
                registerItem(id, 2, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "120V Copper Heating Corp"),
                    VoltageTier.LOW_HOUSEHOLD.voltage, 600.0,
                    750.0)
                registerItem(id, 3, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "Small 12V Iron Heating Corp"),
                    VoltageTier.LOW.voltage, 180.0,
                    225.0)
                registerItem(id, 4, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "12V Iron Heating Corp"),
                    VoltageTier.LOW.voltage, 375.0,
                    480.0)
                registerItem(id, 5, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "Small 120V Iron Heating Corp"),
                    VoltageTier.LOW_HOUSEHOLD.voltage, 600.0,
                    750.0)
                registerItem(id, 6, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "120V Iron Heating Corp"),
                    VoltageTier.LOW_HOUSEHOLD.voltage, 900.0,
                    1050.0)
                registerItem(id, 7, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "Small 12V Tungsten Heating Corp"),
                    VoltageTier.LOW.voltage, 240.0,
                    300.0)
                registerItem(id, 8, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "12V Tungsten Heating Corp"),
                    VoltageTier.LOW.voltage, 500.0,
                    640.0)
                registerItem(id, 9, element)
            }
            run {
                val element = HeatingCorpElement(
                    I18N.TR_NAME(I18N.Type.NONE, "Small 120V Tungsten Heating Corp"),
                    VoltageTier.LOW_HOUSEHOLD.voltage, 800.0,
                    1000.0)
                registerItem(id, 10, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "120V Tungsten Heating Corp"),
                    VoltageTier.LOW_HOUSEHOLD.voltage, 1200.0,
                    1500.0)
                registerItem(id, 11, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "Small 240V Tungsten Heating Corp"),
                    VoltageTier.HIGH_HOUSEHOLD.voltage, 3600.0,
                    4800.0)
                registerItem(id, 12, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "240V Tungsten Heating Corp"),
                    VoltageTier.HIGH_HOUSEHOLD.voltage, 4812.0,
                    6015.0)
                registerItem(id, 13, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "Small 480V Tungsten Heating Corp"),
                    VoltageTier.INDUSTRIAL.voltage, 4000.0,
                    6000.0)
                registerItem(id, 14, element)
            }
            run {
                val element = HeatingCorpElement(I18N.TR_NAME(I18N.Type.NONE, "480V Tungsten Heating Corp"),
                    VoltageTier.INDUSTRIAL.voltage, 12000.0,
                    15000.0)
                registerItem(id, 15, element)
            }
        }

        private fun registerRegulatorItem(id: Int) {
            run {
                val element = RegulatorOnOffDescriptor(I18N.TR_NAME(I18N.Type.NONE, "On/OFF Regulator 1 Percent"),
                    "onoffregulator", 0.01)
                registerItem(id, 0, element)
            }
            run {
                val element = RegulatorOnOffDescriptor(I18N.TR_NAME(I18N.Type.NONE, "On/OFF Regulator 10 Percent"),
                    "onoffregulator", 0.1)
                registerItem(id, 1, element)
            }
            run {
                val element = RegulatorAnalogDescriptor(I18N.TR_NAME(I18N.Type.NONE, "Analogic Regulator"),
                    "Analogicregulator")
                registerItem(id, 2, element)
            }
        }

        private fun registerLampItem(id: Int) {
            val lightPower = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 15.0, 20.0, 25.0, 30.0, 40.0)
            val lightLevel = DoubleArray(16)
            val economicPowerFactor = 0.5
            val standardGrowRate = 0.0
            for (idx in 0..15) {
                lightLevel[idx] = (idx + 0.49) / 15.0
            }
            run {
                val element = LampDescriptor(I18N.TR_NAME(I18N.Type.NONE, "Small 12V Incandescent Light Bulb"),
                    "incandescentironlamp", LampDescriptor.Type.INCANDESCENT,
                    LampSocketType.Douille, VoltageTier.LOW.voltage, lightPower[12],
                    lightLevel[12], EAU.incandescentLampLife, standardGrowRate
                )
                registerItem(id, 0, element)
            }
            run {
                val element = LampDescriptor(I18N.TR_NAME(I18N.Type.NONE, "12V Incandescent Light Bulb"),
                    "incandescentironlamp", LampDescriptor.Type.INCANDESCENT,
                    LampSocketType.Douille, VoltageTier.LOW.voltage, lightPower[14],
                    lightLevel[14], EAU.incandescentLampLife, standardGrowRate
                )
                registerItem(id, 1, element)
            }
            run {
                val element = LampDescriptor(I18N.TR_NAME(I18N.Type.NONE, "120V Incandescent Light Bulb"),
                    "incandescentironlamp", LampDescriptor.Type.INCANDESCENT,
                    LampSocketType.Douille, VoltageTier.LOW_HOUSEHOLD.voltage, lightPower[14],
                    lightLevel[14], EAU.incandescentLampLife, standardGrowRate
                )
                registerItem(id, 2, element)
            }
            run {
                val element = LampDescriptor(
                    I18N.TR_NAME(I18N.Type.NONE, "Small 12V Carbon Incandescent Light Bulb"),
                    "incandescentcarbonlamp", LampDescriptor.Type.INCANDESCENT,
                    LampSocketType.Douille, VoltageTier.LOW.voltage, lightPower[11],
                    lightLevel[11], EAU.carbonLampLife, standardGrowRate
                )
                registerItem(id, 3, element)
            }
            run {
                val element = LampDescriptor(I18N.TR_NAME(I18N.Type.NONE, "12V Carbon Incandescent Light Bulb"),
                    "incandescentcarbonlamp", LampDescriptor.Type.INCANDESCENT,
                    LampSocketType.Douille, VoltageTier.LOW.voltage, lightPower[13],
                    lightLevel[13], EAU.carbonLampLife, standardGrowRate
                )
                registerItem(id, 4, element)
            }
            run {
                val element = LampDescriptor(I18N.TR_NAME(I18N.Type.NONE, "Small 12V Economic Light Bulb"),
                    "fluorescentlamp", LampDescriptor.Type.ECO,
                    LampSocketType.Douille, VoltageTier.LOW.voltage, lightPower[12]
                    * economicPowerFactor,
                    lightLevel[12], EAU.economicLampLife, standardGrowRate
                )
                registerItem(id, 5, element)
            }
            run {
                val element = LampDescriptor(I18N.TR_NAME(I18N.Type.NONE, "12V Economic Light Bulb"),
                    "fluorescentlamp", LampDescriptor.Type.ECO,
                    LampSocketType.Douille, VoltageTier.LOW.voltage, lightPower[14]
                    * economicPowerFactor,
                    lightLevel[14], EAU.economicLampLife, standardGrowRate
                )
                registerItem(id, 6, element)
            }
            run {
                val element = LampDescriptor(I18N.TR_NAME(I18N.Type.NONE, "120V Economic Light Bulb"),
                    "fluorescentlamp", LampDescriptor.Type.ECO,
                    LampSocketType.Douille, VoltageTier.LOW_HOUSEHOLD.voltage, lightPower[14]
                    * economicPowerFactor,
                    lightLevel[14], EAU.economicLampLife, standardGrowRate
                )
                registerItem(id, 7, element)
            }
            run {
                val element = LampDescriptor(I18N.TR_NAME(I18N.Type.NONE, "12V Farming Lamp"),
                    "farminglamp", LampDescriptor.Type.INCANDESCENT,
                    LampSocketType.Douille, VoltageTier.LOW.voltage, 120.0,
                    lightLevel[15], EAU.incandescentLampLife, 0.50
                )
                registerItem(id, 8, element)
            }
            run {
                val element = LampDescriptor(I18N.TR_NAME(I18N.Type.NONE, "120V Farming Lamp"),
                    "farminglamp", LampDescriptor.Type.INCANDESCENT,
                    LampSocketType.Douille, VoltageTier.LOW_HOUSEHOLD.voltage, 120.0,
                    lightLevel[15], EAU.incandescentLampLife, 0.50
                )
                registerItem(id, 9, element)
            }
            run {
                val element = LampDescriptor(I18N.TR_NAME(I18N.Type.NONE, "12V LED Bulb"),
                    "ledlamp", LampDescriptor.Type.LED,
                    LampSocketType.Douille, VoltageTier.LOW.voltage, lightPower[14] / 2,
                    lightLevel[14], EAU.ledLampLife, standardGrowRate
                )
                registerItem(id, 10, element)
            }
            run {
                val element = LampDescriptor(I18N.TR_NAME(I18N.Type.NONE, "120V LED Bulb"),
                    "ledlamp", LampDescriptor.Type.LED, 
                    LampSocketType.Douille, VoltageTier.LOW_HOUSEHOLD.voltage, lightPower[14] / 2,
                    lightLevel[14], EAU.ledLampLife, standardGrowRate
                )
                registerItem(id, 11, element)
            }
        }

        private fun registerProtection(id: Int) {
            run {
                val element = OverHeatingProtectionDescriptor(
                    I18N.TR_NAME(I18N.Type.NONE, "Overheating Protection"))
                registerItem(id, 0, element)
            }
            run {
                val element = OverVoltageProtectionDescriptor(
                    I18N.TR_NAME(I18N.Type.NONE, "Overvoltage Protection"))
                registerItem(id, 1, element)
            }
        }

        private fun registerCombustionChamber(id: Int) {
            run {
                val element = CombustionChamber(I18N.TR_NAME(I18N.Type.NONE, "Combustion Chamber"))
                registerItem(id, 0, element)
            }
        }

        private fun registerFerromagneticCore(id: Int) {
            run {
                val element = FerromagneticCoreDescriptor(
                    I18N.TR_NAME(I18N.Type.NONE, "Cheap Ferromagnetic Core"), EAU.obj.getObj("feromagneticcorea"), 100.0)
                registerItem(id, 0, element)
            }
            run {
                val element = FerromagneticCoreDescriptor(
                    I18N.TR_NAME(I18N.Type.NONE, "Average Ferromagnetic Core"), EAU.obj.getObj("feromagneticcorea"), 50.0)
                registerItem(id, 1, element)
            }
            run {
                val element = FerromagneticCoreDescriptor(
                    I18N.TR_NAME(I18N.Type.NONE, "Optimal Ferromagnetic Core"), EAU.obj.getObj("feromagneticcorea"), 1.0)
                registerItem(id, 2, element)
            }
        }

        private fun registerIngot(id: Int) {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Copper Ingot")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                EAU.copperIngot = element
                registerItem(id, 0, element)
                CraftingRegistry.addToOre("ingotCopper", element.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Lead Ingot")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                EAU.plumbIngot = element
                registerItem(id, 1, element)
                CraftingRegistry.addToOre("ingotLead", element.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Tungsten Ingot")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                EAU.tungstenIngot = element
                registerItem(id, 2, element)
                CraftingRegistry.addToOre(EAU.dictTungstenIngot, element.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Alloy Ingot")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                registerItem(id, 4, element)
                CraftingRegistry.addToOre("ingotAlloy", element.newItemStack())
            }
        }

        private fun registerDust(id: Int) {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Copper Dust")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                EAU.dustCopper = element
                registerItem(id, 0, element)
                CraftingRegistry.addToOre("dustCopper", element.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Iron Dust")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                EAU.dustCopper = element
                registerItem(id, 1, element)
                CraftingRegistry.addToOre("dustIron", element.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Lapis Dust")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                EAU.dustCopper = element
                registerItem(id, 2, element)
                CraftingRegistry.addToOre("dustLapis", element.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Diamond Dust")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                EAU.dustCopper = element
                registerItem(id, 3, element)
                CraftingRegistry.addToOre("dustDiamond", element.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Lead Dust")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                registerItem(id, 4, element)
                CraftingRegistry.addToOre("dustLead", element.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Tungsten Dust")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                registerItem(id, 5, element)
                CraftingRegistry.addToOre(EAU.dictTungstenDust, element.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Gold Dust")
                val element = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 6, element)
                CraftingRegistry.addToOre("dustGold", element.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Coal Dust")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                registerItem(id, 7, element)
                CraftingRegistry.addToOre("dustCoal", element.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Alloy Dust")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                registerItem(id, 8, element)
                CraftingRegistry.addToOre("dustAlloy", element.newItemStack())
            }
        }

        private fun registerElectricalMotor(id: Int) {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Electrical Motor")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                registerItem(id, 0, element)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Advanced Electrical Motor")
                val element = GenericItemUsingDamageDescriptorWithComment(name, arrayOf())
                registerItem(id, 1, element)
            }
        }

        private fun registerSolarTracker(id: Int) {
            run {
                val element = SolarTrackerDescriptor(I18N.TR_NAME(I18N.Type.NONE, "Solar Tracker"))
                registerItem(id, 0, element)
            }
        }

        private fun registerMeter(id: Int) {
            run {
                val element = GenericItemUsingDamageDescriptor(I18N.TR_NAME(I18N.Type.NONE, "MultiMeter"))
                EAU.multiMeterElement = element
                registerItem(id, 0, element)
                EAU.itemTabIcon = element.newItemStack().item
            }
            run {
                val element = GenericItemUsingDamageDescriptor(I18N.TR_NAME(I18N.Type.NONE, "Thermometer"))
                EAU.thermometerElement = element
                registerItem(id, 1, element)
            }
            run {
                val element = GenericItemUsingDamageDescriptor(I18N.TR_NAME(I18N.Type.NONE, "AllMeter"))
                EAU.allMeterElement = element
                registerItem(id, 2, element)
            }
            run {
                val element = WirelessSignalAnalyserItemDescriptor(I18N.TR_NAME(I18N.Type.NONE, "Wireless Analyser"))
                registerItem(id, 3, element)
            }
            run {
                val element = ConfigCopyToolDescriptor(I18N.TR_NAME(I18N.Type.NONE, "Config Copy Tool"))
                EAU.configCopyToolElement = element
                registerItem(id, 4, element)
            }
        }

        private fun registerElectricalDrill(id: Int) {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Cheap Electrical Drill")
                val element = ElectricalDrillDescriptor(name,
                    8.0, 4000.0
                )
                registerItem(id, 0, element)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Average Electrical Drill")
                val element = ElectricalDrillDescriptor(name,
                    5.0, 5000.0
                )
                registerItem(id, 1, element)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Fast Electrical Drill")
                val element = ElectricalDrillDescriptor(name,
                    3.0, 6000.0
                )
                registerItem(id, 2, element)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Turbo Electrical Drill")
                val element = ElectricalDrillDescriptor(name,
                    1.0, 10000.0
                )
                registerItem(id, 3, element)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Irresponsible Electrical Drill")
                val element = ElectricalDrillDescriptor(name,
                    0.1, 20000.0
                )
                registerItem(id, 4, element)
            }
        }

        private fun registerOreScanner(id: Int) {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Ore Scanner")
                val element = OreScanner(name)
                registerItem(id, 0, element)
            }
        }

        private fun registerMiningPipe(id: Int) {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Mining Pipe")
                val element = MiningPipeDescriptor(name)
                EAU.miningPipeDescriptor = element
                registerItem(id, 0, element)
            }
        }

        private fun registerTreeResinAndRubber(id: Int) {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Tree Resin")
                val element = TreeResin(name)
                EAU.treeResin = element
                CraftingRegistry.addToOre("materialResin", element.newItemStack())
                registerItem(id, 0, element)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Rubber")
                val element = GenericItemUsingDamageDescriptor(name)
                CraftingRegistry.addToOre("itemRubber", element.newItemStack())
                registerItem(id, 1, element)
            }
        }

        private fun registerRawCable(id: Int) {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Copper Cable")
                EAU.copperCableDescriptor = CopperCableDescriptor(name)
                registerItem(id, 0, EAU.copperCableDescriptor)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Iron Cable")
                val element = GenericItemUsingDamageDescriptor(name)
                
                registerItem(id, 1, element)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Tungsten Cable")
                val element = GenericItemUsingDamageDescriptor(name)
                
                registerItem(id, 2, element)
            }
        }

        private fun registerArc(id: Int) {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Graphite Rod")
                EAU.GraphiteDescriptor = GraphiteDescriptor(name)
                registerItem(id, 0, EAU.GraphiteDescriptor)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "2x Graphite Rods")
                val descriptor = GenericItemUsingDamageDescriptor(name)
                registerItem(id, 1, descriptor)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "3x Graphite Rods")
                val descriptor = GenericItemUsingDamageDescriptor(name)
                registerItem(id, 2, descriptor)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "4x Graphite Rods")
                val descriptor = GenericItemUsingDamageDescriptor(name)
                registerItem(id, 3, descriptor)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Synthetic Diamond")
                val descriptor = GenericItemUsingDamageDescriptor(name)
                registerItem(id, 4, descriptor)
            }
        }

        private fun registerBrush(id: Int) {
            var subId: Int
            //var whiteDesc: BrushDescriptor? = null
            var name: String
            val subNames = arrayOf(
                I18N.TR_NAME(I18N.Type.NONE, "Black Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Red Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Green Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Brown Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Blue Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Purple Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Cyan Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Silver Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Gray Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Pink Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Lime Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Yellow Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Light Blue Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Magenta Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "Orange Brush"),
                I18N.TR_NAME(I18N.Type.NONE, "White Brush"))
            for (idx in 0..15) {
                subId = idx
                name = subNames[idx]
                val desc = BrushDescriptor(name)
                registerItem(id, subId, desc)
                //whiteDesc = desc
            }
            /*
            TODO: Re-enable this.

            val emptyStack: ItemStack = CraftingRegistry.findItemStack("White Brush", 1)
            whiteDesc!!.setLife(emptyStack, 0)
            for (idx in 0..15) {
                CraftingRegistry.addShapelessRecipe(emptyStack.copy(),
                    ItemStack(Blocks.wool, 1, idx),
                    CraftingRegistry.findItemStack("Iron Cable", 1))
            }
            for (idx in 0..15) {
                name = subNames[idx]
                CraftingRegistry.addShapelessRecipe(CraftingRegistry.findItemStack(name, 1),
                    ItemStack(Items.dye, 1, idx),
                    emptyStack.copy())
            }
             */
        }

        private fun registerMiscItem(id: Int) {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Cheap Chip")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 0, desc)
                OreDictionary.registerOre(EAU.dictCheapChip, desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Advanced Chip")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 1, desc)
                OreDictionary.registerOre(EAU.dictAdvancedChip, desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Machine Block")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 2, desc)
                CraftingRegistry.addToOre("casingMachine", desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Electrical Probe Chip")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 3, desc)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Thermal Probe Chip")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 4, desc)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Copper Plate")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 5, desc)
                CraftingRegistry.addToOre("plateCopper", desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Iron Plate")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 6, desc)
                CraftingRegistry.addToOre("plateIron", desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Gold Plate")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 7, desc)
                CraftingRegistry.addToOre("plateGold", desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Lead Plate")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 8, desc)
                CraftingRegistry.addToOre("plateLead", desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Silicon Plate")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 9, desc)
                CraftingRegistry.addToOre("plateSilicon", desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Alloy Plate")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 10, desc)
                CraftingRegistry.addToOre("plateAlloy", desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Coal Plate")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 11, desc)
                CraftingRegistry.addToOre("plateCoal", desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Silicon Dust")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 12, desc)
                CraftingRegistry.addToOre("dustSilicon", desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Silicon Ingot")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 13, desc)
                CraftingRegistry.addToOre("ingotSilicon", desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Machine Booster")
                val desc = MachineBoosterDescriptor(name)
                registerItem(id, 14, desc)
            }
            run {
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    I18N.TR_NAME(I18N.Type.NONE, "Advanced Machine Block"), arrayOf())
                registerItem(id, 15, desc)
                CraftingRegistry.addToOre("casingMachineAdvanced", desc.newItemStack())
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Basic Magnet")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 16, desc)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Advanced Magnet")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 17, desc)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Data Logger Print")
                val desc = DataLogsPrintDescriptor(name)
                EAU.dataLogsPrintDescriptor = desc
                desc.setDefaultIcon("empty-texture")
                registerHiddenItem(id, 18, desc)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Signal Antenna")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, arrayOf())
                registerItem(id, 19, desc)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Player Filter")
                val desc = EntitySensorFilterDescriptor(name, EntityPlayer::class.java, 0f, 1f, 0f)
                registerItem(id, 20, desc)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Monster Filter")
                val desc = EntitySensorFilterDescriptor(name, IMob::class.java, 1f, 0f, 0f)
                registerItem(id, 21, desc)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Animal Filter")
                val desc = EntitySensorFilterDescriptor(name, EntityAnimal::class.java, .3f, .3f, 1f)
                registerItem(id,  22, desc)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Wrench")
                val desc = GenericItemUsingDamageDescriptorWithComment(
                    name, I18N.TR("Electrical age wrench,\nCan be used to turn\nsmall wall blocks").split("\n".toRegex()).toTypedArray())
                registerItem(id, 23, desc)
                EAU.wrenchItemStack = desc.newItemStack()
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Dielectric")
                val desc = DielectricItem(name, VoltageTier.LOW.voltage)
                registerItem(id, 24, desc)
            }

            registerItem(id, 25, CaseItemDescriptor(I18N.TR_NAME(I18N.Type.NONE, "Casing")))
            registerItem(id, 26, ClutchPinItem("Clutch Pin"))
            registerItem(id, 27, ClutchPlateItem("Iron Clutch Plate", 5120f, 640f, 640f, 160f, 0.0001f, false))
            registerItem(id, 28, ClutchPlateItem("Gold Clutch Plate", 10240f, 2048f, 1024f, 512f, 0.001f, false))
            registerItem(id, 29, ClutchPlateItem("Copper Clutch Plate", 8192f, 4096f, 1024f, 512f, 0.0003f, false))
            registerItem(id, 30, ClutchPlateItem("Lead Clutch Plate", 15360f, 1024f, 1536f, 768f, 0.0015f, false))
            registerItem(id, 31, ClutchPlateItem("Coal Clutch Plate", 1024f, 128f, 128f, 32f, 0.1f, true))
        }

        private fun registerElectricalTool(id: Int) {
            var subId: Int
            var name: String
            run {
                subId = 0
                name = I18N.TR_NAME(I18N.Type.NONE, "Small Flashlight")
                val desc = ElectricalLampItem(
                    name,
                    10, 8, 20.0, 15, 5, 50.0,
                    6000.0, 100.0
                )
                EAU.sharedItemStackOne.addElement(subId + (id shl 6), desc)
            }
            run {
                subId = 8
                name = I18N.TR_NAME(I18N.Type.NONE, "Portable Electrical Mining Drill")
                val desc = ElectricalPickaxe(
                    name,
                    22f, 1f,
                    40000.0, 200.0, 10000.0
                )
                EAU.sharedItemStackOne.addElement(subId + (id shl 6), desc)
            }
            run {
                subId = 12
                name = I18N.TR_NAME(I18N.Type.NONE, "Portable Electrical Axe")
                val desc = ElectricalAxe(
                    name,
                    22f, 1f,
                    40000.0, 200.0, 10000.0
                )
                EAU.sharedItemStackOne.addElement(subId + (id shl 6), desc)
            }
        }

        private fun registerPortableItem(id: Int) {
            var subId: Int
            var name: String
            run {
                subId = 0
                name = I18N.TR_NAME(I18N.Type.NONE, "Portable Battery")
                val desc = BatteryItem(
                    name,
                    40000.0, 125.0, 250.0, 2
                )
                EAU.sharedItemStackOne.addElement(subId + (id shl 6), desc)
            }
            run {
                subId = 1
                name = I18N.TR_NAME(I18N.Type.NONE, "Portable Battery Pack")
                val desc = BatteryItem(
                    name,
                    160000.0, 500.0, 1000.0, 2
                )
                EAU.sharedItemStackOne.addElement(subId + (id shl 6), desc)
            }
            run {
                subId = 16
                name = I18N.TR_NAME(I18N.Type.NONE, "Portable Condensator")
                val desc = BatteryItem(
                    name,
                    4000.0, 2000.0, 2000.0, 1
                )
                EAU.sharedItemStackOne.addElement(subId + (id shl 6), desc)
            }
            run {
                subId = 17
                name = I18N.TR_NAME(I18N.Type.NONE, "Portable Condensator Pack")
                val desc = BatteryItem(
                    name,
                    16000.0, 8000.0, 8000.0, 1
                )
                EAU.sharedItemStackOne.addElement(subId + (id shl 6), desc)
            }
            run {
                subId = 32
                name = I18N.TR_NAME(I18N.Type.NONE, "X-Ray Scanner")
                val desc = PortableOreScannerItem(
                    name, EAU.obj.getObj("XRayScanner"),
                    100000.0, 400.0, 300.0,
                    EAU.xRayScannerRange.toFloat(), (Math.PI / 2).toFloat(),
                    32, 20
                )
                EAU.sharedItemStackOne.addElement(subId + (id shl 6), desc)
            }
        }

        private fun registerFuelBurnerItem(id: Int) {
            EAU.sharedItemStackOne.addElement(0 + (id shl 6),
                FuelBurnerDescriptor(I18N.TR_NAME(I18N.Type.NONE, "Small Fuel Burner"), 5000 * EAU.fuelHeatFurnacePowerFactor, 2, 1.6f))
            EAU.sharedItemStackOne.addElement(1 + (id shl 6),
                FuelBurnerDescriptor(I18N.TR_NAME(I18N.Type.NONE, "Medium Fuel Burner"), 10000 * EAU.fuelHeatFurnacePowerFactor, 1, 1.4f))
            EAU.sharedItemStackOne.addElement(2 + (id shl 6),
                FuelBurnerDescriptor(I18N.TR_NAME(I18N.Type.NONE, "Big Fuel Burner"), 25000 * EAU.fuelHeatFurnacePowerFactor, 0, 1f))
        }

        private fun registerFuses(id: Int) {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Electrical Fuse")
                val desc = ElectricalFuseDescriptor(name, EAU.smallInsulationMediumCurrentCopperCable, EAU.obj.getObj("ElectricalFuse"))
                registerItem(id, 0, desc)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Blown Electrical Fuse")
                val desc = ElectricalFuseDescriptor(name, null, EAU.obj.getObj("ElectricalFuse"))
                ElectricalFuseDescriptor.BlownFuse = desc
                registerItem(id, 4, desc)
            }
        }
    }
}
