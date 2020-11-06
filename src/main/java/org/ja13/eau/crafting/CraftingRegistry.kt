package org.ja13.eau.crafting

import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.launchwrapper.LogWrapper
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.ShapedOreRecipe
import org.ja13.eau.EAU
import org.ja13.eau.misc.Recipe
import org.ja13.eau.misc.Utils

class CraftingRegistry {
    companion object {
        fun addToOre(name: String?, ore: ItemStack?) {
            EAU.dictionnaryOreFromMod[name] = ore
            OreDictionary.registerOre(name, ore)
        }

        fun addRecipe(output: ItemStack?, recipeRows: List<String>, typeKey: Map<Char, String>) {
            if (output != null) {
                val typeKeyItems = typeKey.map {
                    Pair(it.key, findItemStack(it.value))
                }.toMap()
                for (key in typeKeyItems) {
                    if  (key.value == null) {
                        Utils.println("Key value is null in ${key.key}")
                        return
                    }
                    if (!recipeRows.joinToString().contains(key.key)) {
                        Utils.println("Key ${key.key} not in recipe rows: ${recipeRows.joinToString()}")
                        return
                    }
                }
                for (char in recipeRows.joinToString()) {
                    if (!typeKeyItems.keys.contains(char)) {
                        Utils.println("Recipe does not have key for ${char} in ${typeKeyItems.keys}")
                        return
                    }
                }
                GameRegistry.addRecipe(ShapedOreRecipe(output, arrayOf(*recipeRows.toTypedArray(), *(typeKeyItems.entries.toTypedArray()))))
            }
        }

        fun addShapelessRecipe(output: ItemStack?, inputs: List<ItemStack?>) {
            if (output != null) {
                if (null in inputs) {
                    Utils.println("One of the inputs to this shapeless recipe for ${output.displayName} is null!")
                    return
                }
                GameRegistry.addShapelessRecipe(output, inputs)
            }
        }

        fun findItemStack(name: String, stackSize: Int): ItemStack? {
            var stack = GameRegistry.findItemStack("EAU", name, stackSize)
            if (stack == null) {
                stack = EAU.dictionnaryOreFromMod[name]
                if (stack != null)
                    stack = Utils.newItemStack(Item.getIdFromItem(stack.item), stackSize, stack.itemDamage)
                else return null
            }
            return stack
        }

        fun findItemStack(name: String): ItemStack? {
            return findItemStack(name, 1)
        }

        fun firstExistingOre(oreNames: List<String>): String {
            for (oreName in oreNames) {
                if (OreDictionary.doesOreNameExist(oreName)) {
                    return oreName
                }
            }
            return ""
        }

        fun checkRecipe() {
            Utils.println("No recipe for ")
            // TODO: Re-evaluate types nullity here.
            for (d in EAU.sixNodeItem.subItemList.values) {
                val stack = d?.newItemStack()
                if (!recipeExists(stack)) {
                    Utils.println("  " + d?.name)
                }
            }
            for (d in EAU.transparentNodeItem.subItemList.values) {
                val stack = d?.newItemStack()
                if (!recipeExists(stack)) {
                    Utils.println("  " + d?.name)
                }
            }
            for (d in EAU.sharedItem.subItemList.values) {
                val stack = d.newItemStack()
                if (!recipeExists(stack)) {
                    Utils.println("  " + d.name)
                }
            }
            for (d in EAU.sharedItemStackOne.subItemList.values) {
                val stack = d.newItemStack()
                if (!recipeExists(stack)) {
                    Utils.println("  " + d.name)
                }
            }
        }

        fun recipeExists(stack: ItemStack?): Boolean {
            if (stack == null) return false
            val list = CraftingManager.getInstance().recipeList
            for (o in list) {
                if (o is IRecipe) {
                    val r = o
                    if (r.recipeOutput == null) continue
                    if (Utils.areSame(stack, r.recipeOutput)) return true
                }
            }
            return false
        }
        
        fun registerCrafting() {
            recipeEnergyConverter()
            recipeComputerProbe()
            recipeArmor()
            recipeTool()
            recipeGround()
            recipeElectricalCable()
            recipeThermalCable()
            recipeLampSocket()
            recipeLampSupply()
            recipeSwitch()
            recipeWirelessSignal()
            recipeElectricalRelay()
            recipeElectricalDataLogger()
            recipeElectricalGateSource()
            recipeElectricalBreaker()
            recipeFuses()
            recipeElectricalVuMeter()
            recipeElectricalEnvironmentalSensor()
            recipeElectricalRedstone()
            recipeElectricalGate()
            recipeElectricalAlarm()
            recipeElectricalSensor()
            recipeThermalSensor()
            recipeSixNodeMisc()
            recipeTurret()
            recipeMachine()
            //recipeChips()
            recipeTransformer()
            recipeHeatFurnace()
            recipeTurbine()
            recipeBattery()
            recipeElectricalFurnace()
            recipeAutoMiner()
            recipeSolarPanel()
            recipeThermalDissipatorPassiveAndActive()
            recipeElectricalAntenna()
            recipeEggIncubator()
            recipeBatteryCharger()
            recipeTransporter()
            recipeWindTurbine()
            recipeFuelGenerator()
            recipeGeneral()
            recipeHeatingCorp()
            recipeRegulatorItem()
            recipeLampItem()
            recipeFerromagneticCore()
            recipeDust()
            recipeElectricalMotor()
            recipeSolarTracker()
            recipeMeter()
            recipeElectricalDrill()
            recipeOreScanner()
            recipeMiningPipe()
            recipeTreeResinAndRubber()
            recipeRawCable()
            recipeGraphite()
            recipeMiscItem()
            recipeBatteryItem()
            recipeElectricalTool()
            recipePortableCapacitor()
            recipeFurnace()
            recipeMacerator()
            recipeCompressor()
            recipePlateMachine()
            recipeMagnetizer()
            recipeFuelBurnerItem()
            recipeDisplays()
            recipeECoal()
            recipeGridDevices()
        }

        private fun recipeMaceratorModOres() {
            val f = 4000f
            recipeMaceratorModOre(f * 3f, "oreCertusQuartz", "dustCertusQuartz", 3)
            recipeMaceratorModOre(f * 1.5f, "crystalCertusQuartz", "dustCertusQuartz", 1)
            recipeMaceratorModOre(f * 3f, "oreNetherQuartz", "dustNetherQuartz", 3)
            recipeMaceratorModOre(f * 1.5f, "crystalNetherQuartz", "dustNetherQuartz", 1)
            recipeMaceratorModOre(f * 1.5f, "crystalFluix", "dustFluix", 1)
        }

        private fun recipeMaceratorModOre(f: Float, inputName: String, outputName: String, outputCount: Int) {
            if (!OreDictionary.doesOreNameExist(inputName)) {
                LogWrapper.info("No entries for oredict: $inputName")
                return
            }
            if (!OreDictionary.doesOreNameExist(outputName)) {
                LogWrapper.info("No entries for oredict: $outputName")
                return
            }
            val inOres = OreDictionary.getOres(inputName)
            val outOres = OreDictionary.getOres(outputName)
            if (inOres.size == 0) {
                LogWrapper.info("No ores in oredict entry: $inputName")
            }
            if (outOres.size == 0) {
                LogWrapper.info("No ores in oredict entry: $outputName")
                return
            }
            val output = outOres[0].copy()
            output.stackSize = outputCount
            LogWrapper.info("Adding mod recipe from $inputName to $outputName")
            //for (input in inOres) {
                //EAU.maceratorRecipes.addRecipe(Recipe(input, output, f))
            //}
        }

        private fun recipeEnergyConverter() {
            if (EAU.ElnToOtherEnergyConverterEnable) {
                addRecipe(ItemStack(EAU.elnToOtherBlockLvu),
                    listOf(
                        "III",
                        "cCR",
                        "III"
                    ),
                    mapOf(
                        Pair('C', EAU.dictCheapChip),
                        Pair('c', "Copper Cable"),
                        Pair('I', "Iron Cable"),
                        Pair('R', "ingotCopper")
                    )
                )
                addRecipe(ItemStack(EAU.elnToOtherBlockMvu),
                    listOf(
                        "III",
                        "cCR",
                        "III"
                    ),
                    mapOf(
                        Pair('C', EAU.dictCheapChip),
                        Pair('c', "Copper Cable"),
                        Pair('I', "Iron Cable"),
                        Pair('R', EAU.dictTungstenIngot)
                    )
                )
                addRecipe(ItemStack(EAU.elnToOtherBlockHvu),
                    listOf(
                        "III",
                        "cCR",
                        "III"
                    ),
                    mapOf(
                        Pair('C', EAU.dictAdvancedChip),
                        Pair('c', "Copper Cable"),
                        Pair('I', "Iron Cable"),
                        Pair('R', "ingotGold")
                    )
                )
            }
        }

        private fun recipeComputerProbe() {
            if (EAU.ComputerProbeEnable) {
                addRecipe(ItemStack(EAU.computerProbeBlock),
                    listOf(
                        "cIw",
                        "ICI",
                        "WIc"
                    ),
                    mapOf(
                        Pair('C', EAU.dictAdvancedChip),
                        Pair('c', "Copper Cable"),
                        Pair('I', "Iron cable"),
                        Pair('w', "Wireless Signal Receiver"),
                        Pair('W', "Wireless Signal Transmitter")
                    )
                )
            }
        }

        private fun recipeArmor() {

            addRecipe(ItemStack(EAU.helmetCopper),
                listOf(
                    "CCC",
                    "C C",
                    "   "
                ),
                mapOf(
                    Pair('C', "ingotCopper")
                )
            )
            addRecipe(ItemStack(EAU.plateCopper),
                listOf(
                    "C C",
                    "CCC",
                    "CCC"
                ),
                mapOf(
                    Pair('C', "ingotCopper")
                )
            )
            addRecipe(ItemStack(EAU.legsCopper),
                listOf(
                    "CCC",
                    "C C",
                    "C C"
                ),
                mapOf(
                Pair('C', "ingotCopper")
                )
            )
            addRecipe(ItemStack(EAU.bootsCopper),
                listOf(
                    "C C",
                    "C C"
                ),
                mapOf(
                    Pair('C', "ingotCopper")
                )
            )
        }

        private fun recipeTool() {
            val materialMap = mapOf(
                Pair('i', "ingotCopper"),
                Pair('s', "stick")
            )
            addRecipe(ItemStack(EAU.shovelCopper),
                listOf(
                    "i",
                    "s",
                    "s"
                ), materialMap
            )
            addRecipe(ItemStack(EAU.axeCopper),
                listOf(
                    "ii",
                    "is",
                    " s"
                ), materialMap
            )
            addRecipe(ItemStack(EAU.hoeCopper),
                listOf(
                    "ii",
                    " s",
                    " s"
                ), materialMap
            )
            addRecipe(ItemStack(EAU.pickaxeCopper),
                listOf(
                    "iii",
                    " s ",
                    " s "
                ), materialMap
            )
            addRecipe(ItemStack(EAU.swordCopper),
                listOf(
                "i",
                "i",
                "s"
                ), materialMap
            )
        }

        private fun recipeGround() {
            addRecipe(findItemStack("Ground Cable"),
                listOf(
                    " C ",
                    " C ",
                    "CCC"
                ),
                mapOf(Pair('C', "Copper Cable"))
            )
        }

        private fun recipeElectricalCable() {
            addRecipe(EAU.uninsulatedHighCurrentCopperCable.newItemStack(4),
                listOf("CC"),
                mapOf(Pair('C', "ingotCopper"))
            )
            addRecipe(EAU.uninsulatedMediumCurrentCopperCable.newItemStack(4),
                listOf("CC"),
                mapOf(Pair('C', "Uninsulated High Current Copper Cable"))
            )
            addRecipe(EAU.uninsulatedLowCurrentCopperCable.newItemStack(4),
                listOf("CC"),
                mapOf(Pair('C', "Uninsulated Medium Current Copper Cable"))
            )

            addRecipe(EAU.uninsulatedHighCurrentAluminumCable.newItemStack(4),
                listOf("AA"),
                mapOf(Pair('A', "ingotAluminum"))
            )
            addRecipe(EAU.uninsulatedMediumCurrentAluminumCable.newItemStack(4),
                listOf("AA"),
                mapOf(Pair('A', "ingotAluminum"))
            )
        }

        private fun recipeThermalCable() {
            addRecipe(findItemStack("Copper Thermal Cable", 12),
                listOf(
                    "SSS",
                    "CCC",
                    "SSS"
                ),
                mapOf(
                    Pair('S', "cobblestone"),
                    Pair('C', "ingotCopper")
                )
            )
            addRecipe(findItemStack("Copper Thermal Cable", 1),
                listOf(
                    "S",
                    "C"
                ),
                mapOf(
                    Pair('S', "cobblestone"),
                    Pair('C', "Copper Cable")
                )
            )
        }

        private fun recipeLampSocket() {
            val paneGlassIronCable = mapOf(
                Pair('G', "paneGlass"),
                Pair('I', "Iron Cable")
            )
            val paneglassIronIngot = mapOf(
                Pair('G', "paneGlass"),
                Pair('I', "ingotIron")
            )
            addRecipe(findItemStack("Lamp Socket A", 3),
                listOf(
                "G ",
                "IG",
                "G "
                ), paneGlassIronCable
            )
            addRecipe(findItemStack("Lamp Socket B Projector", 3),
                listOf(
                    " G",
                    "GI",
                    " G"
                ), paneGlassIronCable
            )
            addRecipe(findItemStack("Street Light", 1),
                listOf(
                    "G",
                    "I",
                    "I"
                ), paneglassIronIngot
            )
            addRecipe(findItemStack("Robust Lamp Socket", 3),
                listOf("GIG"), paneglassIronIngot
            )
            addRecipe(findItemStack("Flat Lamp Socket", 3),
                listOf("IGI"), paneGlassIronCable
            )
            addRecipe(findItemStack("Simple Lamp Socket", 3),
                listOf(
                    " I ",
                    "GGG"
                ), paneglassIronIngot
            )
            addRecipe(findItemStack("Fluorescent Lamp Socket", 3),
                listOf(
                " I ",
                "G G"
                ),
                mapOf(
                    Pair('G', "Iron Cable"),
                    Pair('I', "ingotIron")
                )
            )
            addRecipe(findItemStack("Suspended Lamp Socket", 2),
                listOf(
                "I",
                "G"
                ),
                mapOf(
                    Pair('G', "Robust Lamp Socket"),
                    Pair('I', "Iron Cable")
                )
            )
            addRecipe(findItemStack("Long Suspended Lamp Socket", 2),
                listOf(
                    "I",
                    "I",
                    "G"
                ),
                mapOf(
                    Pair('G', "Robust Lamp Socket"),
                    Pair('I', "Iron Cable")
                )
            )
            addRecipe(findItemStack("Suspended Lamp Socket (No Swing)", 4),
                listOf(
                    "I",
                    "G"
                ),
                mapOf(
                    Pair('G', "Robust Lamp Socket"),
                    Pair('I', "ingotIron")
                )
            )
            addRecipe(findItemStack("Long Suspended Lamp Socket (No Swing)", 4),
                listOf(
                    "I",
                    "I",
                    "G"
                ),
                mapOf(
                    Pair('G', "Robust Lamp Socket"),
                    Pair('I', "ingotIron")
                )
            )
            addRecipe(findItemStack("Sconce Lamp Socket", 2),
                listOf(
                    "GCG",
                    "GIG"
                ),
                mapOf(
                    Pair('G', "paneGlass"),
                    Pair('C', "dustCoal"),
                    Pair('I', "ingotIron")
                )
            )
            addRecipe(findItemStack("50V Emergency Lamp"),
                listOf(
                    "cbc",
                    " l ",
                    " g "
                ),
                mapOf(
                    Pair('c', "Copper Cable"),
                    Pair('b', "Portable Battery Pack"),
                    Pair('l', "50V LED Bulb"),
                    Pair('g', "paneGlass")
                )
            )
            addRecipe(findItemStack("200V Emergency Lamp"),
                listOf(
                    "cbc",
                    " l ",
                    " g "
                ),
                mapOf(
                    Pair('c', "Copper Cable"),
                    Pair('b', "Portable Battery Pack"),
                    Pair('l', "200V LED Bulb"),
                    Pair('g', "paneGlass")
                )
            )
        }

        private fun recipeLampSupply() {
            addRecipe(findItemStack("Lamp Supply", 1),
                listOf(
                    " I ",
                    "ICI",
                    " I "
                ),
                mapOf(
                    Pair('C', "ingotCopper"),
                    Pair('I', "ingotIron")
                )
            )
        }
/*
        private fun recipePassiveComponent() {
            addRecipe(findItemStack("10A Diode", 3),
                " RB",
                "IIR",
                " RB",
                'R', ItemStack(Items.redstone),
                'I', findItemStack("Iron Cable"),
                'B', "itemRubber")
            addRecipe(findItemStack("25A Diode"),
                "D",
                "D",
                "D",
                'D', findItemStack("10A Diode"))
            addRecipe(findItemStack("Power Capacitor"),
                "cPc",
                "III",
                'I', ItemStack(Items.iron_ingot),
                'c', findItemStack("Iron Cable"),
                'P', "plateIron")
            addRecipe(findItemStack("Power Inductor"),
                "   ",
                "cIc",
                "   ",
                'I', ItemStack(Items.iron_ingot),
                'c', findItemStack("Copper Cable"))
            addRecipe(findItemStack("Power Resistor"),
                "   ",
                "cCc",
                "   ",
                'c', findItemStack("Copper Cable"),
                'C', findItemStack("Coal Dust"))
            addRecipe(findItemStack("Rheostat"),
                " R ",
                " MS",
                "cmc",
                'R', findItemStack("Power Resistor"),
                'c', findItemStack("Copper Cable"),
                'm', findItemStack("Machine Block"),
                'M', findItemStack("Electrical Motor"),
                'S', findItemStack("Signal Cable")
            )
            addRecipe(findItemStack("Thermistor"),
                "   ",
                "csc",
                "   ",
                's', "dustSilicon",
                'c', findItemStack("Copper Cable"))
            addRecipe(findItemStack("Large Rheostat"),
                "   ",
                " D ",
                "CRC",
                'R', findItemStack("Rheostat"),
                'C', findItemStack("Copper Thermal Cable"),
                'D', findItemStack("Small Passive Thermal Dissipator")
            )
        }
*/
        private fun recipeSwitch() {
            addRecipe(findItemStack("Low Current Switch"),
                listOf(
                    "  I",
                    " I ",
                    "CAC"
                ),
                mapOf(
                    Pair('A', "itemRubber"),
                    Pair('I', "slabOak"),
                    Pair('C', "Copper Cable")
                )
            )
            addRecipe(findItemStack("High Current Switch"),
                listOf(
                    "  I",
                    " C ",
                    "CAC"
                ),
                mapOf(
                    Pair('A', "itemRubber"),
                    Pair('I', "slabOak"),
                    Pair('C', "Copper Cable")
                )
            )
        }

        private fun recipeWirelessSignal() {
            addRecipe(findItemStack("Wireless Signal Transmitter"),
                listOf(
                    " S ",
                    " R ",
                    "ICI"
                ),
                mapOf(
                    Pair('R', "redstone"),
                    Pair('I', "Iron Cable"),
                    Pair('C', EAU.dictCheapChip),
                    Pair('S', "Signal Antenna")
                )
            )
            addRecipe(findItemStack("Wireless Signal Repeater"),
                listOf(
                    "S S",
                    "R R",
                    "ICI"
                ),
                mapOf(
                    Pair('R', "redstone"),
                    Pair('I', "Iron Cable"),
                    Pair('C', EAU.dictCheapChip),
                    Pair('S', "Signal Antenna")
                )
            )
            addRecipe(findItemStack("Wireless Signal Receiver"),
                listOf(
                    " S ",
                    "ICI"
                ),
                mapOf(
                    Pair('I', "Iron Cable"),
                    Pair('C', EAU.dictCheapChip),
                    Pair('S', "Signal Antenna")
                )
            )
        }

        private fun recipeElectricalRelay() {
            addRecipe(findItemStack("Low Current Relay"),
                listOf(
                    "GGG",
                    "ICI",
                    "CRC"
                ),
                mapOf(
                    Pair('R', "redstone"),
                    Pair('I', "Iron Cable"),
                    Pair('G', "paneGlass"),
                    Pair('A', "itemRubber"),
                    Pair('C', "Copper Cable")
                )
            )
        }

        private fun recipeElectricalDataLogger() {
            addRecipe(findItemStack("Data Logger", 1),
                listOf(
                    "RRR",
                    "RGR",
                    "RCR"
                ),
                mapOf(
                    Pair('R', "itemRubber"),
                    Pair('C', EAU.dictCheapChip),
                    Pair('G', "paneGlass")
                )
            )
            addRecipe(findItemStack("Modern Data Logger", 1),
                listOf(
                    "RRR",
                    "RGR",
                    "RCR"
                ),
                mapOf(
                    Pair('R', "itemRubber"),
                    Pair('C', EAU.dictAdvancedChip),
                    Pair('G', "paneGlass")
                )
            )
            addRecipe(findItemStack("Industrial Data Logger", 1),
                listOf(
                    "RRR",
                    "GGG",
                    "RCR"
                ),
                mapOf(
                    Pair('R', "itemRubber"),
                    Pair('C', EAU.dictAdvancedChip),
                    Pair('G', "paneGlass")
                )
            )
        }

        private fun recipeElectricalGateSource() {
            addRecipe(findItemStack("Signal Trimmer", 1),
                listOf(
                    "RsR",
                    "rRr",
                    " c "
                ),
                mapOf(
                    Pair('c', "Copper Cable"),
                    Pair('r', "itemRubber"),
                    Pair('s', "stick"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("Signal Switch", 3),
                listOf(
                    " r ",
                    "rRr",
                    " c "
                ),
                mapOf(
                    Pair('c', "Copper Cable"),
                    Pair('r', "itemRubber"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("Signal Button", 3),
                listOf(
                    " R ",
                    "rRr",
                    " c "
                ),
                mapOf(
                    Pair('c', "Copper Cable"),
                    Pair('r', "itemRubber"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("Wireless Switch", 3),
                listOf(
                    " a ",
                    "rCr",
                    " r "
                ),
                mapOf(
                    Pair('C', EAU.dictCheapChip),
                    Pair('a', "Signal Antenna"),
                    Pair('r', "itemRubber")
                )
            )
            addRecipe(findItemStack("Wireless Button", 3),
                listOf(
                    " a ",
                    "rCr",
                    " R "
                ),
                mapOf(
                    Pair('C', EAU.dictCheapChip),
                    Pair('a', "Signal Antenna"),
                    Pair('r', "itemRubber"),
                    Pair('R', "redstone")
                )
            )
        }

        private fun recipeElectricalBreaker() {
            addRecipe(findItemStack("Electrical Breaker", 1),
                listOf("R"),
                mapOf(
                    Pair('R', "Low Current Relay")
                )
            )
        }

        private fun recipeFuses() {
            addRecipe(findItemStack("Electrical Fuse Holder", 1),
                listOf("i", " ", "i"),
                mapOf(Pair('i', "Iron Cable"))
            )
        }

        private fun recipeElectricalVuMeter() {
            addRecipe(findItemStack("Analog vuMeter", 1),
                listOf(
                    "WWW",
                    "RIr",
                    "WSW"
                ),
                mapOf(
                    Pair('W', "planksOak"),
                    Pair('R', "redstone"),
                    Pair('I', "Iron Cable"),
                    Pair('r', "dye1"),
                    Pair('S', "Copper Cable")
                )
            )
            addRecipe(findItemStack("LED vuMeter", 1),
                listOf(
                    " W ",
                    "WTW",
                    " S "
                ),
                mapOf(
                    Pair('W', "planksOak"),
                    Pair('T', "torchRedstone"),
                    Pair('S', "Copper Cable")
                )
            )
        }

        private fun recipeElectricalEnvironmentalSensor() {
            addShapelessRecipe(
                findItemStack("Electrical Daylight Sensor"),
                listOf(
                    ItemStack(Blocks.daylight_detector),
                    findItemStack("Redstone-to-Voltage Converter")
                )
            )
            addShapelessRecipe(
                findItemStack("Electrical Light Sensor"),
                listOf(
                    ItemStack(Blocks.daylight_detector),
                    ItemStack(Items.quartz),
                    findItemStack("Redstone-to-Voltage Converter")
                )
            )
            addRecipe(findItemStack("Electrical Weather Sensor"),
                listOf(
                    " r ",
                    "rRr",
                    " r "
                ),
                mapOf(
                    Pair('R', "redstone"),
                    Pair('r', "itemRubber")
                )
            )
            addRecipe(findItemStack("Electrical Anemometer Sensor"),
                listOf(
                    " I ",
                    " R ",
                    "I I"
                ),
                mapOf(
                    Pair('R', "redstone"),
                    Pair('I', "Iron Cable")
                )
            )
            addRecipe(findItemStack("Electrical Entity Sensor"),
                listOf(
                    " G ",
                    "GRG",
                    " G "
                ),
                mapOf(
                    Pair('G', "paneGlass"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("Electrical Fire Detector"),
                listOf(
                    "cbr",
                    "p p",
                    "r r"
                ),
                mapOf(
                    Pair('c', "Copper Cable"),
                    Pair('b', EAU.dictCheapChip),
                    Pair('r', "itemRubber"),
                    Pair('p', "plateCopper")
                )
            )
            addRecipe(findItemStack("Electrical Fire Buzzer"),
                listOf(
                    "rar",
                    "p p",
                    "r r"
                ),
                mapOf(
                    Pair('a', EAU.dictAdvancedChip),
                    Pair('r', "itemRubber"),
                    Pair('p', "plateCopper")
                )
            )
        }

        private fun recipeElectricalRedstone() {
            addRecipe(findItemStack("Redstone-to-Voltage Converter", 1),
                listOf("TCS"),
                mapOf(
                    Pair('S', "Copper Cable"),
                    Pair('C', EAU.dictCheapChip),
                    Pair('T', "torchRedstone")
                )
            )
            addRecipe(findItemStack("Voltage-to-Redstone Converter", 1),
                listOf("CTR"),
                mapOf(
                    Pair('C', EAU.dictCheapChip),
                    Pair('T', "torchRedstone"),
                    Pair('R', "redstone")
                )
            )
        }

        private fun recipeElectricalGate() {
            addShapelessRecipe(
                findItemStack("Electrical Timer"),
                listOf(
                    ItemStack(Items.repeater),
                    findItemStack(EAU.dictCheapChip)
                )
            )
            addRecipe(findItemStack("Signal Processor", 1),
                listOf(
                    "IcI",
                    "cCc",
                    "IcI"
                ),
                mapOf(
                    Pair('I', "ingotIron"),
                    Pair('c', "Copper Cable"),
                    Pair('C', EAU.dictCheapChip)
                )
            )
        }

        private fun recipeElectricalAlarm() {
            addRecipe(findItemStack("Nuclear Alarm", 1),
                listOf(
                    "ITI",
                    "IMI",
                    "IcI"
                ),
                mapOf(
                    Pair('c', "Copper Cable"),
                    Pair('T', "torchRedstone"),
                    Pair('I', "Iron Cable"),
                    Pair('M', "noteblock")
                )
            )
            addRecipe(findItemStack("Standard Alarm", 1),
                listOf(
                    "MTM",
                    "IcI",
                    "III"
                ),
                mapOf(
                    Pair('c', "Copper Cable"),
                    Pair('T', "torchRedstone"),
                    Pair('I', "Iron Cable"),
                    Pair('M', "noteblock")
                )
            )
        }

        private fun recipeElectricalSensor() {
            addShapelessRecipe(findItemStack("Voltage Probe", 1),
                listOf(
                    findItemStack("Electrical Probe Chip"),
                    findItemStack("Copper Cable")
                )
            )
            addShapelessRecipe(
                findItemStack("Electrical Probe", 1),
                listOf(findItemStack("Voltage Probe", 1))
            )
            addShapelessRecipe(
                findItemStack("Voltage Probe", 1),
                listOf(findItemStack("Electrical Probe", 1))
            )
        }

        private fun recipeThermalSensor() {
            addShapelessRecipe(findItemStack("Thermal Probe", 1),
                listOf(
                    findItemStack("Temperature Probe Chip"),
                    findItemStack("Copper Cable")
                )
            )
            addShapelessRecipe(
                findItemStack("Temperature Probe", 1),
                listOf(findItemStack("Thermal Probe", 1))
            )
            addShapelessRecipe(
                findItemStack("Thermal Probe", 1),
                listOf(findItemStack("Temperature Probe", 1))
            )
        }

        private fun recipeSixNodeMisc() {
            addRecipe(findItemStack("Hub"),
                listOf(
                    "I I",
                    " c ",
                    "I I"
                ),
                mapOf(
                    Pair('c', "Copper Cable"),
                    Pair('I', "Iron Cable")
                )
            )
            addRecipe(findItemStack("Energy Meter"),
                listOf(
                    "IcI",
                    "IRI",
                    "IcI"
                ),
                mapOf(
                    Pair('c', "Copper Cable"),
                    Pair('R', EAU.dictCheapChip),
                    Pair('I', "Iron Cable")
                )
            )
            addRecipe(findItemStack("Advanced Energy Meter"),
                listOf(
                    " c ",
                    "PRP",
                    " c "
                ),
                mapOf(
                    Pair('c', "Copper Cable"),
                    Pair('R', EAU.dictAdvancedChip),
                    Pair('P', "Iron Plate")
                )
            )
        }

        private fun recipeTurret() {
            addRecipe(findItemStack("Defence Turret", 1),
                listOf(
                    " R ",
                    "CMC",
                    " c "
                ),
                mapOf(
                    Pair('M', "Advanced Machine Block"),
                    Pair('C', EAU.dictAdvancedChip),
                    Pair('c', "Copper Cable"),
                    Pair('R', "blockRedstone")
                )
            )
        }

        private fun recipeMachine() {
            addRecipe(findItemStack("50V Macerator", 1),
                listOf(
                    "IRI",
                    "FMF",
                    "IcI"
                ),
                mapOf(
                    Pair('M', "Machine Block"),
                    Pair('c', "Electrical Motor"),
                    Pair('F', "flint"),
                    Pair('I', "Iron Cable"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("200V Macerator", 1),
                listOf(
                    "ICI",
                    "DMD",
                    "IcI"
                ),
                mapOf(
                    Pair('M', "Advanced Machine Block"),
                    Pair('C', EAU.dictAdvancedChip),
                    Pair('c', "Advanced Electrical Motor"),
                    Pair('D', "diamond"),
                    Pair('I', "ingotAlloy")
                )
            )
            addRecipe(findItemStack("50V Compressor", 1),
                listOf(
                    "IRI",
                    "FMF",
                    "IcI"
                ),
                mapOf(
                    Pair('M', "Machine Block"),
                    Pair('c', "Electrical Motor"),
                    Pair('F', "plateIron"),
                    Pair('I', "Iron Cable"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("200V Compressor", 1),
                listOf(
                    "ICI",
                    "DMD",
                    "IcI"
                ),
                mapOf(
                    Pair('M', "Advanced Machine Block"),
                    Pair('C', EAU.dictAdvancedChip),
                    Pair('c', "Advanced Electrical Motor"),
                    Pair('D', "plateAlloy"),
                    Pair('I', "ingotAlloy")
                )
            )
            addRecipe(findItemStack("50V Plate Machine", 1),
                listOf(
                    "IRI",
                    "IMI",
                    "IcI"
                ),
                mapOf(
                    Pair('M', "Machine Block"),
                    Pair('c', "Electrical Motor"),
                    Pair('I', "Iron Cable"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("200V Plate Machine", 1),
                listOf(
                    "DCD",
                    "DMD",
                    "DcD"
                ),
                mapOf(
                    Pair('M', "Advanced Machine Block"),
                    Pair('C', EAU.dictAdvancedChip),
                    Pair('c', "Advanced Electrical Motor"),
                    Pair('D', "plateAlloy"),
                    Pair('I', "ingotAlloy")
                )
            )
            addRecipe(findItemStack("50V Magnetizer", 1),
                listOf(
                    "IRI",
                    "cMc",
                    "III"
                ),
                mapOf(
                    Pair('M', "Machine Block"),
                    Pair('c', "Electrical Motor"),
                    Pair('I', "Iron Cable"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("200V Magnetizer", 1),
                listOf(
                    "ICI",
                    "cMc",
                    "III"
                ),
                mapOf(
                    Pair('M', "Advanced Machine Block"),
                    Pair('C', EAU.dictAdvancedChip),
                    Pair('c', "Advanced Electrical Motor"),
                    Pair('I', "ingotAlloy")
                )
            )
        }
/*
        private fun recipeChips() {
            addRecipe(findItemStack("NOT Chip"),
                "   ",
                "cCr",
                "   ",
                'C', EAU.dictCheapChip,
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Copper Cable"))
            addRecipe(findItemStack("AND Chip"),
                " c ",
                "cCc",
                " c ",
                'C', EAU.dictCheapChip,
                'c', findItemStack("Copper Cable"))
            addRecipe(findItemStack("NAND Chip"),
                " c ",
                "cCr",
                " c ",
                'C', EAU.dictCheapChip,
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Copper Cable"))
            addRecipe(findItemStack("OR Chip"),
                " r ",
                "rCr",
                " r ",
                'C', EAU.dictCheapChip,
                'r', ItemStack(Items.redstone))
            addRecipe(findItemStack("NOR Chip"),
                " r ",
                "rCc",
                " r ",
                'C', EAU.dictCheapChip,
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Copper Cable"))
            addRecipe(findItemStack("XOR Chip"),
                " rr",
                "rCr",
                " rr",
                'C', EAU.dictCheapChip,
                'r', ItemStack(Items.redstone))
            addRecipe(findItemStack("XNOR Chip"),
                " rr",
                "rCc",
                " rr",
                'C', EAU.dictCheapChip,
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Copper Cable"))
            addRecipe(findItemStack("PAL Chip"),
                "rcr",
                "cCc",
                "rcr",
                'C', EAU.dictAdvancedChip,
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Copper Cable"))
            addRecipe(findItemStack("Schmitt Trigger Chip"),
                "   ",
                "cCc",
                "   ",
                'C', EAU.dictAdvancedChip,
                'c', findItemStack("Copper Cable"))
            addRecipe(findItemStack("D Flip Flop Chip"),
                "   ",
                "cCc",
                " p ",
                'C', EAU.dictAdvancedChip,
                'p', findItemStack("Copper Plate"),
                'c', findItemStack("Copper Cable"))
            addRecipe(findItemStack("Oscillator Chip"),
                "pdp",
                "cCc",
                "   ",
                'C', EAU.dictAdvancedChip,
                'p', findItemStack("Copper Plate"),
                'c', findItemStack("Copper Cable"),
                'd', findItemStack("Dielectric"))
            addRecipe(findItemStack("JK Flip Flop Chip"),
                " p ",
                "cCc",
                " p ",
                'C', EAU.dictAdvancedChip,
                'p', findItemStack("Copper Plate"),
                'c', findItemStack("Copper Cable"))
            addRecipe(findItemStack("Amplifier"),
                "  r",
                "cCc",
                "   ",
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Copper Cable"),
                'C', EAU.dictAdvancedChip)
            addRecipe(findItemStack("OpAmp"),
                "  r",
                "cCc",
                " c ",
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Copper Cable"),
                'C', EAU.dictAdvancedChip)
            addRecipe(findItemStack("Configurable summing unit"),
                " cr",
                "cCc",
                " c ",
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Copper Cable"),
                'C', EAU.dictAdvancedChip)
            addRecipe(findItemStack("Sample and hold"),
                " rr",
                "cCc",
                " c ",
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Copper Cable"),
                'C', EAU.dictAdvancedChip)
            addRecipe(findItemStack("Voltage controlled sine oscillator"),
                "rrr",
                "cCc",
                "   ",
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Copper Cable"),
                'C', EAU.dictAdvancedChip)
            addRecipe(findItemStack("Voltage controlled sawtooth oscillator"),
                "   ",
                "cCc",
                "rrr",
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Copper Cable"),
                'C', EAU.dictAdvancedChip)
            addRecipe(findItemStack("PID Regulator"),
                "rrr",
                "cCc",
                "rcr",
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Copper Cable"),
                'C', EAU.dictAdvancedChip)
            addRecipe(findItemStack("Lowpass filter"),
                "CdC",
                "cDc",
                " s ",
                'd', findItemStack("Dielectric"),
                'c', findItemStack("Copper Cable"),
                'C', findItemStack("Copper Plate"),
                'D', findItemStack("Coal Dust"),
                's', EAU.dictCheapChip)
        }
        */

        private fun recipeTransformer() {
            addRecipe(findItemStack("DC-DC Converter"),
                listOf(
                    "C C",
                    "III"
                ),
                mapOf(
                    Pair('C', "Copper Cable"),
                    Pair('I', "ingotIron")
                )
            )
            addRecipe(findItemStack("Variable DC-DC Converter"),
                listOf(
                    "CBC",
                    "III"
                ),
                mapOf(
                    Pair('C', "Copper Cable"),
                    Pair('I', "ingotIron"),
                    Pair('B', EAU.dictCheapChip)
                )
            )
        }

        private fun recipeHeatFurnace() {
            addRecipe(findItemStack("Stone Heat Furnace"),
                listOf(
                    "BBB",
                    "B B",
                    "BiB"
                ),
                mapOf(
                    Pair('B', "stone"),
                    Pair('i', "Copper Thermal Cable")
                )
            )
            addRecipe(findItemStack("Fuel Heat Furnace"),
                listOf(
                    "IcI",
                    "mCI",
                    "IiI"
                ),
                mapOf(
                    Pair('c', EAU.dictCheapChip),
                    Pair('m', "Electrical Motor"),
                    Pair('C', "cauldron"),
                    Pair('I', "ingotIron"),
                    Pair('i', "Copper Thermal Cable")
                )
            )
        }

        private fun recipeTurbine() {
            addRecipe(findItemStack("Sterling Engine"),
                listOf(
                    " m ",
                    "HMH",
                    " E "
                ),
                mapOf(
                    Pair('M', "Machine Block"),
                    Pair('E', "Copper Cable"),
                    Pair('H', "Copper Thermal Cable"),
                    Pair('m', "Electrical Motor")
                )
            )
            /*
            TODO: Fix Thevenin systems first...
            addRecipe(findItemStack("Generator"),
                "mmm",
                "ama",
                " ME",
                'm', findItemStack("Advanced Electrical Motor"),
                'M', findItemStack("Advanced Machine Block"),
                'a', firstExistingOre(listOf("ingotAluminum", "ingotIron")),
                'E', findItemStack("High Voltage Cable")
            )
            addRecipe(findItemStack("Shaft Motor"),
                "imi",
                " ME",
                'i', "ingotIron",
                'M', findItemStack("Advanced Machine Block"),
                'm', findItemStack("Advanced Electrical Motor"),
                'E', findItemStack("Very High Voltage Cable")
            )
            addRecipe(findItemStack("Steam Turbine"),
                " a ",
                "aAa",
                " M ",
                'a', firstExistingOre(listOf("ingotAluminum", "ingotIron")),
                'A', firstExistingOre(listOf("blockAluminum", "blockIron")),
                'M', findItemStack("Advanced Machine Block")
            )
            addRecipe(findItemStack("Gas Turbine"),
                "msH",
                "sSs",
                " M ",
                'm', findItemStack("Advanced Electrical Motor"),
                'H', findItemStack("Copper Thermal Cable"),
                's', firstExistingOre(listOf("ingotSteel", "ingotIron")),
                'S', firstExistingOre(listOf("blockSteel", "blockIron")),
                'M', findItemStack("Advanced Machine Block")
            )
            addRecipe(findItemStack("Rotary Motor"),
                " r ",
                "rSr",
                " rM",
                'r', "plateAlloy",
                'S', firstExistingOre(listOf("blockSteel", "blockIron")),
                'M', findItemStack("Advanced Machine Block")
            )
            addRecipe(findItemStack("Joint"),
                "   ",
                "iii",
                " m ",
                'i', "ingotIron",
                'm', findItemStack("Machine Block")
            )
            addRecipe(findItemStack("Joint hub"),
                " i ",
                "iii",
                " m ",
                'i', "ingotIron",
                'm', findItemStack("Machine Block")
            )
            addRecipe(findItemStack("Flywheel"),
                "PPP",
                "PmP",
                "PPP",
                'P', "ingotLead",
                'm', findItemStack("Machine Block")
            )
            addRecipe(findItemStack("Tachometer"),
                "p  ",
                "iii",
                "cm ",
                'i', "ingotIron",
                'm', findItemStack("Machine Block"),
                'p', findItemStack("Electrical Probe Chip"),
                'c', findItemStack("Signal Cable")
            )
            addRecipe(findItemStack("Clutch"),
                "iIi",
                " c ",
                'i', "ingotIron",
                'I', "plateIron",
                'c', findItemStack("Machine Block")
            )
            addRecipe(findItemStack("Fixed Shaft"),
                "iBi",
                " c ",
                'i', "ingotIron",
                'B', "blockIron",
                'c', findItemStack("Machine Block")
            )
        }

        private fun recipeBattery() {
            addRecipe(findItemStack("12V Lead Acid Battery"),
                listOf(
                    "C C",
                    "PPP",
                    "PPP"
                ),
                mapOf(
                    Pair('C', "Copper Cable"),
                    Pair('P', "ingotLead")
                )
            )
        }

        private fun recipeElectricalFurnace() {
            addRecipe(findItemStack("Electrical Furnace"),
                listOf(
                    "III",
                    "IFI",
                    "ICI"
                ),
                mapOf(
                    Pair('C', "Copper Cable"),
                    Pair('F', "furnace"),
                    Pair('I', "ingotIron")
                )
            )
        }

        private fun recipeAutoMiner() {
            addRecipe(findItemStack("Auto Miner"),
                listOf(
                    "MCM",
                    "BOB",
                    " P "
                ),
                mapOf(
                    Pair('C', EAU.dictAdvancedChip),
                    Pair('O', "Ore Scanner"),
                    Pair('B', "Advanced Machine Block"),
                    Pair('M', "Advanced Electrical Motor"),
                    Pair('P', "Mining Pipe")
                )
            )
        }

        private fun recipeSolarPanel() {
            addRecipe(findItemStack("Small Solar Panel"),
                listOf(
                    "LLL",
                    "CSC",
                    "III"
                ),
                mapOf(
                    Pair('S', "plateSilicon"),
                    Pair('L', "dustLapis"),
                    Pair('I', "ingotIron"),
                    Pair('C', "Copper Cable")
                )
            )
            addRecipe(findItemStack("Small Rotating Solar Panel"),
                listOf(
                    "ISI",
                    "I I"
                ),
                mapOf(
                    Pair('S', "Small Solar Panel"),
                    Pair('M', "Electrical Motor"),
                    Pair('I', "ingotIron")
                )
            )
            for (metal in arrayOf("blockSteel", "blockAluminum", "blockAluminium", "casingMachineAdvanced")) {
                for (panel in arrayOf("Small Solar Panel", "Small Rotating Solar Panel")) {
                    addRecipe(findItemStack("2x3 Solar Panel"),
                        listOf(
                            "PPP",
                            "PPP",
                            "I I"
                        ),
                        mapOf(
                            Pair('P', panel),
                            Pair('I', metal)
                        )
                    )
                }
            }
            addRecipe(findItemStack("2x3 Rotating Solar Panel"),
                listOf(
                    "ISI",
                    "IMI",
                    "I I"
                ),
                mapOf(
                    Pair('S', "2x3 Solar Panel"),
                    Pair('M', "Electrical Motor"),
                    Pair('I', "ingotIron")
                )
            )
        }

        private fun recipeThermalDissipatorPassiveAndActive() {
            addRecipe(
                findItemStack("Heatsink"),
                listOf(
                    "I I",
                    "III",
                    "CIC"
                ),
                mapOf(
                    Pair('I', "ingotCopper"),
                    Pair('C', "Copper Thermal Cable")
                )
            )
            addRecipe(
                findItemStack("Heatsink with 12V Fan"),
                listOf(
                    "RMR",
                    " D "
                ),
                mapOf(
                    Pair('D', "Heatsink"),
                    Pair('M', "Electrical Motor"),
                    Pair('R', "itemRubber")
                )
            )
            addRecipe(
                findItemStack("Heatsink with 240V Fan"),
                listOf(
                    "RMR",
                    " D "
                ),
                mapOf(
                    Pair('D', "Heatsink"),
                    Pair('M', "Advanced Electrical Motor"),
                    Pair('R', "itemRubber")
                )
            )
        }

        private fun recipeElectricalAntenna() {
            addRecipe(findItemStack("Low Power Transmitter Antenna", 1),
                listOf(
                    "R i",
                    "CI ",
                    "R i"
                ),
                mapOf(
                    Pair('C', EAU.dictCheapChip),
                    Pair('i', "ingotIron"),
                    Pair('I', "plateIron"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("Low Power Receiver Antenna", 1),
                listOf(
                    "i  ",
                    " IC",
                    "i  "
                ),
                mapOf(
                    Pair('C', EAU.dictCheapChip),
                    Pair('i', "ingotIron"),
                    Pair('I', "plateIron")
                )
            )
            addRecipe(findItemStack("Medium Power Transmitter Antenna", 1),
                listOf(
                    "c I",
                    "CI ",
                    "c I"
                ),
                mapOf(
                    Pair('C', EAU.dictAdvancedChip),
                    Pair('c', EAU.dictCheapChip),
                    Pair('I', "plateIron"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("Medium Power Receiver Antenna", 1),
                listOf(
                    "I  ",
                    " IC",
                    "I  "
                ),
                mapOf(
                    Pair('C', EAU.dictAdvancedChip),
                    Pair('I', "plateIron")
                )
            )
            addRecipe(findItemStack("High Power Transmitter Antenna", 1),
                listOf(
                    "C I",
                    "CI ",
                    "C I"
                ),
                mapOf(
                    Pair('C', EAU.dictAdvancedChip),
                    Pair('I', "plateIron")
                )
            )
            addRecipe(findItemStack("High Power Receiver Antenna", 1),
                listOf(
                    "I D",
                    " IC",
                    "I D"
                ),
                mapOf(
                    Pair('C', EAU.dictAdvancedChip),
                    Pair('I', "plateIron"),
                    Pair('R', "redstone"),
                    Pair('D', "diamond")
                )
            )
        }

        private fun recipeEggIncubator() {
            addRecipe(findItemStack("50V Egg Incubator", 1),
                listOf(
                    "IGG",
                    "E G",
                    "CII"
                ),
                mapOf(
                    Pair('C', EAU.dictCheapChip),
                    Pair('E', "Small 50V Tungsten Heating Corp"),
                    Pair('I', "ingotIron"),
                    Pair('G', "paneGlass")
                )
            )
        }

        private fun recipeBatteryCharger() {
            addRecipe(findItemStack("Weak 50V Battery Charger", 1),
                "RIR",
                "III",
                "RcR",
                'c', findItemStack("Low Voltage Cable"),
                'I', findItemStack("Iron Cable"),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("50V Battery Charger", 1),
                "RIR",
                "ICI",
                "RcR",
                'C', EAU.dictCheapChip,
                'c', findItemStack("Low Voltage Cable"),
                'I', findItemStack("Iron Cable"),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("200V Battery Charger", 1),
                "RIR",
                "ICI",
                "RcR",
                'C', EAU.dictAdvancedChip,
                'c', findItemStack("Medium Voltage Cable"),
                'I', findItemStack("Iron Cable"),
                'R', ItemStack(Items.redstone))
        }

        private fun recipeTransporter() {
            addRecipe(findItemStack("Experimental Transporter", 1),
                "RMR",
                "RMR",
                " R ",
                'M', findItemStack("Advanced Machine Block"),
                'C', findItemStack("High Voltage Cable"),
                'R', EAU.dictAdvancedChip)
        }

        private fun recipeWindTurbine() {
            addRecipe(findItemStack("Wind Turbine"),
                " I ",
                "IMI",
                " B ",
                'B', findItemStack("Machine Block"),
                'I', "plateIron",
                'M', findItemStack("Electrical Motor"))
        }

        private fun recipeFuelGenerator() {
            addRecipe(findItemStack("50V Fuel Generator"),
                "III",
                " BA",
                "CMC",
                'I', "plateIron",
                'B', findItemStack("Machine Block"),
                'A', findItemStack("Analogic Regulator"),
                'C', findItemStack("Low Voltage Cable"),
                'M', findItemStack("Electrical Motor"))
            addRecipe(findItemStack("200V Fuel Generator"),
                "III",
                " BA",
                "CMC",
                'I', "plateIron",
                'B', findItemStack("Advanced Machine Block"),
                'A', findItemStack("Analogic Regulator"),
                'C', findItemStack("Medium Voltage Cable"),
                'M', findItemStack("Advanced Electrical Motor"))
        }

        private fun recipeGeneral() {
            Utils.addSmelting(EAU.treeResin.parentItem,
                EAU.treeResin.parentItemDamage, findItemStack("Rubber", 1), 0f)
        }

        private fun recipeHeatingCorp() {
            addRecipe(findItemStack("Small 50V Copper Heating Corp"),
                "C C",
                "CCC",
                "C C",
                'C', findItemStack("Copper Cable"))
            addRecipe(findItemStack("50V Copper Heating Corp"),
                "CC",
                'C', findItemStack("Small 50V Copper Heating Corp"))
            addRecipe(findItemStack("Small 200V Copper Heating Corp"),
                "CC",
                'C', findItemStack("50V Copper Heating Corp"))
            addRecipe(findItemStack("200V Copper Heating Corp"),
                "CC",
                'C', findItemStack("Small 200V Copper Heating Corp"))
            addRecipe(findItemStack("Small 50V Iron Heating Corp"),
                "C C",
                "CCC",
                "C C", 'C', findItemStack("Iron Cable"))
            addRecipe(findItemStack("50V Iron Heating Corp"),
                "CC",
                'C', findItemStack("Small 50V Iron Heating Corp"))
            addRecipe(findItemStack("Small 200V Iron Heating Corp"),
                "CC",
                'C', findItemStack("50V Iron Heating Corp"))
            addRecipe(findItemStack("200V Iron Heating Corp"),
                "CC",
                'C', findItemStack("Small 200V Iron Heating Corp"))
            addRecipe(findItemStack("Small 50V Tungsten Heating Corp"),
                "C C",
                "CCC",
                "C C",
                'C', findItemStack("Tungsten Cable"))
            addRecipe(findItemStack("50V Tungsten Heating Corp"),
                "CC",
                'C', findItemStack("Small 50V Tungsten Heating Corp"))
            addRecipe(findItemStack("Small 200V Tungsten Heating Corp"),
                "CC",
                'C', findItemStack("50V Tungsten Heating Corp"))
            addRecipe(findItemStack("200V Tungsten Heating Corp"),
                "CC",
                'C', findItemStack("Small 200V Tungsten Heating Corp"))
            addRecipe(findItemStack("Small 800V Tungsten Heating Corp"),
                "CC",
                'C', findItemStack("200V Tungsten Heating Corp"))
            addRecipe(findItemStack("800V Tungsten Heating Corp"),
                "CC",
                'C', findItemStack("Small 800V Tungsten Heating Corp"))
            addRecipe(findItemStack("Small 3.2kV Tungsten Heating Corp"),
                "CC",
                'C', findItemStack("800V Tungsten Heating Corp"))
            addRecipe(findItemStack("3.2kV Tungsten Heating Corp"),
                "CC",
                'C', findItemStack("Small 3.2kV Tungsten Heating Corp"))
        }

        private fun recipeRegulatorItem() {
            addRecipe(findItemStack("On/OFF Regulator 10 Percent", 1),
                "R R",
                " R ",
                " I ",
                'R', ItemStack(Items.redstone),
                'I', findItemStack("Iron Cable"))
            addRecipe(findItemStack("On/OFF Regulator 1 Percent", 1),
                "RRR",
                " I ",
                'R', ItemStack(Items.redstone),
                'I', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Analogic Regulator", 1),
                "R R",
                " C ",
                " I ",
                'R', ItemStack(Items.redstone),
                'I', findItemStack("Iron Cable"),
                'C', EAU.dictCheapChip)
        }

        private fun recipeLampItem() {
            // Tungsten
            addRecipe(
                findItemStack("Small 50V Incandescent Light Bulb", 4),
                " G ",
                "GFG",
                " S ",
                'G', ItemStack(Blocks.glass_pane),
                'F', EAU.dictTungstenIngot,
                'S', findItemStack("Copper Cable"))
            addRecipe(findItemStack("50V Incandescent Light Bulb", 4),
                " G ",
                "GFG",
                " S ",
                'G', ItemStack(Blocks.glass_pane),
                'F', EAU.dictTungstenIngot,
                'S', findItemStack("Low Voltage Cable"))
            addRecipe(findItemStack("200V Incandescent Light Bulb", 4),
                " G ",
                "GFG",
                " S ",
                'G', ItemStack(Blocks.glass_pane),
                'F', EAU.dictTungstenIngot,
                'S', findItemStack("Medium Voltage Cable"))

            // CARBON
            addRecipe(findItemStack("Small 50V Carbon Incandescent Light Bulb", 4),
                " G ",
                "GFG",
                " S ",
                'G', ItemStack(Blocks.glass_pane),
                'F', ItemStack(Items.coal),
                'S', findItemStack("Copper Cable"))
            addRecipe(findItemStack("Small 50V Carbon Incandescent Light Bulb", 4),
                " G ",
                "GFG",
                " S ",
                'G', ItemStack(Blocks.glass_pane),
                'F', ItemStack(Items.coal, 1, 1),
                'S', findItemStack("Copper Cable"))
            addRecipe(
                findItemStack("50V Carbon Incandescent Light Bulb", 4),
                " G ",
                "GFG",
                " S ",
                'G', ItemStack(Blocks.glass_pane),
                'F', ItemStack(Items.coal),
                'S', findItemStack("Low Voltage Cable"))
            addRecipe(findItemStack("50V Carbon Incandescent Light Bulb", 4),
                " G ",
                "GFG",
                " S ",
                'G', ItemStack(Blocks.glass_pane),
                'F', ItemStack(Items.coal, 1, 1),
                'S', findItemStack("Low Voltage Cable"))
            addRecipe(
                findItemStack("Small 50V Economic Light Bulb", 4),
                " G ",
                "GFG",
                " S ",
                'G', ItemStack(Blocks.glass_pane),
                'F', ItemStack(Items.glowstone_dust),
                'S', findItemStack("Copper Cable"))
            addRecipe(findItemStack("50V Economic Light Bulb", 4),
                " G ",
                "GFG",
                " S ",
                'G', ItemStack(Blocks.glass_pane),
                'F', ItemStack(Items.glowstone_dust),
                'S', findItemStack("Low Voltage Cable"))
            addRecipe(findItemStack("200V Economic Light Bulb", 4),
                " G ",
                "GFG",
                " S ",
                'G', ItemStack(Blocks.glass_pane),
                'F', ItemStack(Items.glowstone_dust),
                'S', findItemStack("Medium Voltage Cable"))
            addRecipe(findItemStack("50V Farming Lamp", 2),
                "GGG",
                "FFF",
                "GSG",
                'G', ItemStack(Blocks.glass_pane),
                'F', EAU.dictTungstenIngot,
                'S', findItemStack("Low Voltage Cable"))
            addRecipe(findItemStack("200V Farming Lamp", 2),
                "GGG",
                "FFF",
                "GSG",
                'G', ItemStack(Blocks.glass_pane),
                'F', EAU.dictTungstenIngot,
                'S', findItemStack("Medium Voltage Cable"))
            addRecipe(findItemStack("50V LED Bulb", 2),
                "GGG",
                "SSS",
                " C ",
                'G', ItemStack(Blocks.glass_pane),
                'S', findItemStack("Silicon Ingot"),
                'C', findItemStack("Low Voltage Cable"))
            addRecipe(findItemStack("200V LED Bulb", 2),
                "GGG",
                "SSS",
                " C ",
                'G', ItemStack(Blocks.glass_pane),
                'S', findItemStack("Silicon Ingot"),
                'C', findItemStack("Medium Voltage Cable"))
        }

        private fun recipeFerromagneticCore() {
            addRecipe(findItemStack("Cheap Ferromagnetic Core"),
                listOf(
                "LLL",
                "L  ",
                "LLL"
                ),
                mapOf(
                    Pair('L', "Iron Cable")
                )
            )
            addRecipe(findItemStack("Average Ferromagnetic Core"),
                listOf(
                    "PCP"
                ),
                mapOf(
                    Pair('C', "Cheap Ferromagnetic Core"),
                    Pair('P', "plateIron")
                )
            )
            addRecipe(findItemStack("Optimal Ferromagnetic Core"),
                listOf(
                    " P ",
                    "PCP",
                    " P "
                ),
                mapOf(
                    Pair('C', "Average Ferromagnetic Core"),
                    Pair('P', "plateIron")
                )
            )
        }

        private fun recipeDust() {
            addShapelessRecipe(findItemStack("Alloy Dust", 6),
                listOf(
                    findItemStack("dustIron"),
                    findItemStack("dustCoal"),
                    findItemStack(EAU.dictTungstenDust),
                    findItemStack(EAU.dictTungstenDust),
                    findItemStack(EAU.dictTungstenDust),
                    findItemStack(EAU.dictTungstenDust)
                )
            )
        }

        private fun recipeElectricalMotor() {
            addRecipe(findItemStack("Electrical Motor"),
                listOf(
                    " C ",
                    "III",
                    "C C"
                ),
                mapOf(
                    Pair('I', "Iron Cable"),
                    Pair('C', "Low Voltage Cable")
                )
            )
            addRecipe(findItemStack("Advanced Electrical Motor"),
                listOf(
                    "RCR",
                    "MIM",
                    "CRC"
                ),
                mapOf(
                    Pair('M', "Advanced Magnet"),
                    Pair('I', "ingotIron"),
                    Pair('R', "redstone"),
                    Pair('C', "Medium Voltage Cable")
                )
            )
        }

        private fun recipeSolarTracker() {
            addRecipe(findItemStack("Solar Tracker", 4),
                listOf(
                    "VVV",
                    "RQR",
                    "III"
                ),
                mapOf(
                    Pair('Q', "quartz"),
                    Pair('V', "glass_pane"),
                    Pair('R', "redstone"),
                    Pair('G', "gold_ingot"),
                    Pair('I', "iron_ingot")
                )
            )

        }

        private fun recipeMeter() {
            addRecipe(findItemStack("MultiMeter"),
                listOf(
                    "RGR",
                    "RER",
                    "RCR"
                ),
                mapOf(
                    Pair('G', "glass_pane"),
                    Pair('C', "Electrical Probe Chip"),
                    Pair('E', "redstone"),
                    Pair('R', "itemRubber")
                )
            )
            addRecipe(findItemStack("Thermometer"),
                listOf(
                    "RGR",
                    "RER",
                    "RCR"
                ),
                mapOf(
                    Pair('G', "glass_pane"),
                    Pair('C', "Thermal Probe Chip"),
                    Pair('E', "redstone"),
                    Pair('R', "itemRubber")
                )
            )
            addShapelessRecipe(
                findItemStack("AllMeter"),
                listOf(
                findItemStack("MultiMeter"),
                findItemStack("Thermometer")
                )
            )
            addRecipe(findItemStack("Wireless Analyser"),
                listOf(
                " S ",
                "RGR",
                "RER"
                ),
                'G', ItemStack(.glass_pane),
                'S', findItemStack("Signal Antenna"),
                'E', ItemStack(.redstone),
                'R', "itemRubber")
            addRecipe(findItemStack("Config Copy Tool"),
                listOf(
                "wR",
                "RC"
                ),
                'w', findItemStack("Wrench"),
                'R', ItemStack(.redstone),
                'C', EAU.dictAdvancedChip
            )
        }

        private fun recipeElectricalDrill() {
            addRecipe(findItemStack("Cheap Electrical Drill"),
                "CMC",
                " T ",
                " P ",
                'T', findItemStack("Mining Pipe"),
                'C', EAU.dictCheapChip,
                'M', findItemStack("Electrical Motor"),
                'P', ItemStack(.iron_pickaxe))
            addRecipe(findItemStack("Average Electrical Drill"),
                "RCR",
                " D ",
                " d ",
                'R', "redstone",
                'C', EAU.dictCheapChip,
                'D', findItemStack("Cheap Electrical Drill"),
                'd', ItemStack(.diamond))
            addRecipe(findItemStack("Fast Electrical Drill"),
                "MCM",
                " T ",
                " P ",
                'T', findItemStack("Mining Pipe"),
                'C', EAU.dictAdvancedChip,
                'M', findItemStack("Advanced Electrical Motor"),
                'P', ItemStack(.diamond_pickaxe))
            addRecipe(findItemStack("Turbo Electrical Drill"),
                "RCR",
                " F ",
                " D ",
                'F', findItemStack("Fast Electrical Drill"),
                'C', EAU.dictAdvancedChip,
                'R', findItemStack("Graphite Rod"),
                'D', findItemStack("Synthetic Diamond"))
            addRecipe(findItemStack("Irresponsible Electrical Drill"),
                "DDD",
                "DFD",
                "DDD",
                'F', findItemStack("Turbo Electrical Drill"),
                'D', findItemStack("Synthetic Diamond"))
        }

        private fun recipeOreScanner() {
            addRecipe(findItemStack("Ore Scanner"),
                "IGI",
                "RCR",
                "IGI",
                'C', EAU.dictCheapChip,
                'R', ItemStack(.redstone),
                'I', findItemStack("Iron Cable"),
                'G', ItemStack(.gold_ingot))
        }

        private fun recipeMiningPipe() {
            addRecipe(findItemStack("Mining Pipe", 12),
                "A",
                "A",
                'A', "ingotAlloy")
        }

        private fun recipeTreeResinAndRubber() {
            addRecipe(findItemStack("Tree Resin Collector"),
                "W W",
                "WW ", 'W', "plankWood")
            addRecipe(findItemStack("Tree Resin Collector"),
                "W W",
                " WW", 'W', "plankWood")
        }

        private fun recipeRawCable() {
            addRecipe(findItemStack("Copper Cable", 12),
                "III",
                'I', "ingotCopper")
            addRecipe(findItemStack("Iron Cable", 12),
                "III",
                'I', ItemStack(.iron_ingot))
            addRecipe(findItemStack("Tungsten Cable", 6),
                "III",
                'I', EAU.dictTungstenIngot)
        }

        private fun recipeGraphite() {
            addShapelessRecipe(
                findItemStack("Graphite Rod", 2),
                listOf(
                    findItemStack("2x Graphite Rods")
                )
            )
            addShapelessRecipe(
                findItemStack("Graphite Rod", 3),
                listOf(
                    findItemStack("3x Graphite Rods")
                )
            )
            addShapelessRecipe(
                findItemStack("Graphite Rod", 4),
                listOf(
                    findItemStack("4x Graphite Rods")
                )
            )
            addShapelessRecipe(
                findItemStack("2x Graphite Rods"),
                listOf(
                    findItemStack("Graphite Rod"),
                    findItemStack("Graphite Rod")
                )
            )
            addShapelessRecipe(
                findItemStack("3x Graphite Rods"),
                listOf(
                    findItemStack("Graphite Rod"),
                    findItemStack("Graphite Rod"),
                    findItemStack("Graphite Rod")
                )
            )
            addShapelessRecipe(
                findItemStack("3x Graphite Rods"),
                listOf(
                    findItemStack("Graphite Rod"),
                    findItemStack("2x Graphite Rods")
                )
            )
            addShapelessRecipe(
                findItemStack("4x Graphite Rods"),
                listOf(
                    findItemStack("Graphite Rod"),
                    findItemStack("Graphite Rod"),
                    findItemStack("Graphite Rod"),
                    findItemStack("Graphite Rod")
                )
            )
            addShapelessRecipe(
                findItemStack("4x Graphite Rods"),
                listOf(
                    findItemStack("2x Graphite Rods"),
                    findItemStack("Graphite Rod"),
                    findItemStack("Graphite Rod")
                )
            )
            addShapelessRecipe(
                findItemStack("4x Graphite Rods"),
                listOf(
                    findItemStack("2x Graphite Rods"),
                    findItemStack("2x Graphite Rods")
                )
            )
            addShapelessRecipe(
                findItemStack("4x Graphite Rods"),
                listOf(
                    findItemStack("3x Graphite Rods"),
                    findItemStack("Graphite Rod")
                )
            )
            addShapelessRecipe(
                findItemStack("diamond", 2),
                listOf(
                    findItemStack("Synthetic Diamond")
                )
            )
        }

        private fun recipeMiscItem() {
            addRecipe(findItemStack("Cheap Chip"),
                listOf(
                    " R ",
                    "RSR",
                    " R "),
                mapOf(
                    Pair('S', "ingotSilicon"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("Advanced Chip"),
                listOf(
                    "LRL",
                    "RCR",
                    "LRL"),
                mapOf(
                    Pair('C', EAU.dictCheapChip),
                    Pair('L', "ingotSilicon"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("Machine Block"),
                listOf(
                    "rLr",
                    "LcL",
                    "rLr"),
                mapOf(
                    Pair('L', "Iron Cable"),
                    Pair('c', "Copper Cable"),
                    Pair('r', "Tree Resin")
                )
            )
            addRecipe(findItemStack("Advanced Machine Block"),
                listOf(
                    "rCr",
                    "CcC",
                    "rCr"),
                mapOf(
                    Pair('C', "plateAlloy"),
                    Pair('r', "Tree Resin"),
                    Pair('c', "Copper Cable")
                )
            )
            addRecipe(findItemStack("Electrical Probe Chip"),
                listOf(
                    " R ",
                    "RCR",
                    " R "),
                mapOf(
                    Pair('C', "High Voltage Cable"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("Thermal Probe Chip"),
                listOf(
                    " C ",
                    "RIR",
                    " C "),
                mapOf(
                    Pair('G', "goldIngot"),
                    Pair('I', "Iron Cable"),
                    Pair('C', "ingotCopper"),
                    Pair('R', "redstone")
                )
            )
            addRecipe(findItemStack("Signal Antenna"),
                listOf(
                    "c",
                    "c"
                ),
                mapOf(
                    Pair('c',"Iron Cable")
                )
            )
            addRecipe(findItemStack("Machine Booster"),
                listOf(
                    "m",
                    "c",
                    "m"),
                mapOf(
                    Pair('m', "Electrical Motor"),
                    Pair('c', EAU.dictAdvancedChip)
                )
            )
            addRecipe(findItemStack("Wrench"),
                listOf(
                    " c ",
                    "cc ",
                    "  c"),
                mapOf(
                    Pair('c', "Iron Cable")
                )
            )
            addRecipe(findItemStack("Player Filter"),
                listOf(
                    " g",
                    "gc",
                    " g"),
                mapOf(
                    Pair('g', "glasspane"),
                    Pair('c', "dye", 1, 2)
                )
            )
            )
            addRecipe(findItemStack("Monster Filter"),
                listOf(
                    " g",
                    "gc",
                    " g"),
                mapOf(
                    Pair('g', "glasspane"),
                    Pair('c', "dye", 1, 1)
                )
            )
            addRecipe(findItemStack("Casing", 1),
                listOf(
                    "ppp",
                    "p p",
                    "ppp"),
                mapOf(
                    Pair('p', "Iron Cable")
                )
            )
            addRecipe(findItemStack("Iron Clutch Plate"),
                listOf(
                    " t ",
                    "tIt",
                    " t "),
                mapOf(
                    Pair('I', "plateIron"),
                    Pair('t', EAU.dictTungstenDust)
                )
            )
            addRecipe(findItemStack("Gold Clutch Plate"),
                listOf(
                    " t ",
                    "tGt",
                    " t "),
                mapOf(
                    Pair('G', "plateGold"),
                    Pair('t', EAU.dictTungstenDust)
                )
            )
            addRecipe(findItemStack("Copper Clutch Plate"),
                listOf(
                    " t ",
                    "tCt",
                    " t "),
                mapOf(
                    Pair('C', "plateCopper"),
                    Pair('t', EAU.dictTungstenDust)
                )
            )
            addRecipe(findItemStack("Lead Clutch Plate"),
                listOf(
                    " t ",
                    "tLt",
                    " t "),
                mapOf(
                    Pair('L', "plateLead"),
                    Pair('t', EAU.dictTungstenDust)
                )
            )
            addRecipe(findItemStack("Coal Clutch Plate"),
                listOf(
                    " t ",
                    "tCt",
                    " t "
                ),
                mapOf(
                    Pair('C', "plateCoal"),
                    Pair('t', EAU.dictTungstenDust)
                )
            )
            addRecipe(findItemStack("Clutch Pin", 4),
                listOf(
                    "s",
                    "s"
                ),
                mapOf(
                    Pair('s',firstExistingOre(listOf("ingotSteel", "ingotAlloy"))
                    )
                )
            )
        }

        private fun recipeBatteryItem() {
            addRecipe(findItemStack("Portable Battery"),
                listOf(
                    " I ",
                    "IPI",
                    "IPI"),
                mapOf(
                    Pair('P', "ingotLead"),
                    Pair('I', "ingotiron")
                )
            )
            addShapelessRecipe(
                findItemStack("Portable Battery Pack"),
                listOf(
                    findItemStack("Portable Battery"),
                    findItemStack("Portable Battery"),
                    findItemStack("Portable Battery"),
                    findItemStack("Portable Battery")
                )
            )
        }

        private fun recipeElectricalTool() {
            addRecipe(findItemStack("Small Flashlight"),
                listOf(
                    "GLG",
                    "IBI",
                    " I "),
                mapOf(
                    Pair('L', "50V Incandescent Light Bulb"),
                    Pair('B', "Portable Battery"),
                    Pair('G', "glasspane"),
                    Pair('I', "ironingot")
                )
            )
            addRecipe(findItemStack("Portable Electrical Mining Drill"),
                listOf(
                    " T ",
                    "IBI",
                    " I "),
                mapOf(
                    Pair('T', "Average Electrical Drill"),
                    Pair('B', "Portable Battery"),
                    Pair('I', "ironingot")
                )
            )
            addRecipe(findItemStack("Portable Electrical Axe"),
                listOf(
                    " T ",
                    "IMI",
                    "IBI"
                ),
                mapOf(
                    Pair('T', "ironAxe"),
                    Pair('B', "Portable Battery"),
                    Pair('M', "Electrical Motor"),
                    Pair('I', "ironIngot")
                )
            )
            if (EAU.xRayScannerCanBeCrafted) {
                addRecipe(findItemStack("X-Ray Scanner"),
                    listOf(
                        "PGP",
                        "PCP",
                        "PBP"
                    ),
                    mapOf(
                        Pair('C', EAU.dictAdvancedChip),
                        Pair('B', "Portable Battery"),
                        Pair('P', "Iron Cable"),
                        Pair('G', "Ore Scanner")
                    )
                )
            }
        }

        private fun recipePortableCapacitor() {
            addRecipe(findItemStack("Portable Condensator"),
                listOf(
                    " r ",
                    "cDc",
                    " r "),
                mapOf(
                    Pair('r', "redstone"),
                    Pair('c', "Iron Cable"),
                    Pair('D', "Dielectric")
                )
            )


            addShapelessRecipe(findItemStack("Portable Condensator Pack"),
                listOf(
                    findItemStack("Portable Condensator"),
                    findItemStack("Portable Condensator"),
                    findItemStack("Portable Condensator"),
                    findItemStack("Portable Condensator")
                )
            )
        }
        */

        private fun recipeFurnace() {
            var `in`: ItemStack?
            `in` = findItemStack("Copper Ore")
            Utils.addSmelting(`in`, findItemStack("Copper Ingot"))
            `in` = findItemStack("dustCopper")
            Utils.addSmelting(`in`, findItemStack("Copper Ingot"))
            `in` = findItemStack("Lead Ore")
            Utils.addSmelting(`in`, findItemStack("ingotLead"))
            `in` = findItemStack("dustLead")
            Utils.addSmelting(`in`, findItemStack("ingotLead"))
            `in` = findItemStack("Tungsten Ore")
            Utils.addSmelting(`in`, findItemStack("Tungsten Ingot"))
            `in` = findItemStack("Tungsten Dust")
            Utils.addSmelting(`in`, findItemStack("Tungsten Ingot"))
            `in` = findItemStack("dustIron")
            Utils.addSmelting(`in`, ItemStack(Items.iron_ingot))
            `in` = findItemStack("dustGold")
            Utils.addSmelting(`in`, ItemStack(Items.gold_ingot))
            `in` = findItemStack("Tree Resin")
            Utils.addSmelting(`in`, findItemStack("Rubber", 2))
            `in` = findItemStack("Alloy Dust")
            Utils.addSmelting(`in`, findItemStack("Alloy Ingot"))
            `in` = findItemStack("Silicon Dust")
            Utils.addSmelting(`in`, findItemStack("Silicon Ingot"))
            `in` = findItemStack("dustCinnabar")
            Utils.addSmelting(`in`, findItemStack("Mercury"))
        }

        private fun recipeMacerator() {
            val f = 4000f
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Blocks.coal_ore, 1),
                ItemStack(Items.coal, 3, 0), 1.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Copper Ore"), arrayOf(findItemStack("Copper Dust", 2)), 1.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Blocks.iron_ore), arrayOf(findItemStack("Iron Dust", 2)), 1.5 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Blocks.gold_ore), arrayOf(findItemStack("Gold Dust", 2)), 3.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Lead Ore"), arrayOf(findItemStack("Lead Dust", 2)), 2.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Tungsten Ore"), arrayOf(findItemStack("Tungsten Dust", 2)), 2.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Items.coal, 1, 0), arrayOf(findItemStack("Coal Dust", 1)), 1.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Items.coal, 1, 1), arrayOf(findItemStack("Coal Dust", 1)), 1.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Blocks.sand, 1), arrayOf(findItemStack("Silicon Dust", 1)), 3.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Cinnabar Ore"), arrayOf(findItemStack("Cinnabar Dust", 1)), 2.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Items.dye, 1, 4), arrayOf(findItemStack("Lapis Dust", 1)), 2.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Items.diamond, 1), arrayOf(findItemStack("Diamond Dust", 1)), 2.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Copper Ingot"), arrayOf(findItemStack("Copper Dust", 1)), 0.5 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Items.iron_ingot), arrayOf(findItemStack("Iron Dust", 1)), 0.5 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Items.gold_ingot), arrayOf(findItemStack("Gold Dust", 1)), 0.5 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Lead Ingot"), arrayOf(findItemStack("Lead Dust", 1)), 0.5 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Tungsten Ingot"), arrayOf(findItemStack("Tungsten Dust", 1)), 0.5 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Blocks.cobblestone), arrayOf(ItemStack(Blocks.gravel)), 1.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Blocks.gravel), arrayOf(ItemStack(Items.flint)), 1.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(ItemStack(Blocks.dirt), arrayOf(ItemStack(Blocks.sand)), 1.0 * f))
            //recycling recipes
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("E-Coal Helmet"), arrayOf(findItemStack("Coal Dust", 16)), 10.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("E-Coal Boots"), arrayOf(findItemStack("Coal Dust", 12)), 10.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("E-Coal Chestplate"), arrayOf(findItemStack("Coal Dust", 24)), 10.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("E-Coal Leggings"), arrayOf(findItemStack("Coal Dust", 24)), 10.0 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Cost Oriented Battery"), arrayOf(findItemStack("Lead Dust", 6)), 12.5 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Life Oriented Battery"), arrayOf(findItemStack("Lead Dust", 6)), 12.5 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Current Oriented Battery"), arrayOf(findItemStack("Lead Dust", 6)), 12.5 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Voltage Oriented Battery"), arrayOf(findItemStack("Lead Dust", 6)), 12.5 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Capacity Oriented Battery"), arrayOf(findItemStack("Lead Dust", 6)), 12.5 * f))
            EAU.maceratorRecipes.addRecipe(Recipe(findItemStack("Single-use Battery"), arrayOf(findItemStack("Copper Dust", 3)), 10.0 * f))
        }

        private fun recipeCompressor() {
            EAU.compressorRecipes.addRecipe(Recipe(findItemStack("4x Graphite Rods", 1),
                findItemStack("Synthetic Diamond"), 80000.0))
            // extractorRecipes.addRecipe(new
            // Recipe("dustCinnabar",new
            // ItemStack[]{findItemStack("Purified Cinnabar Dust",1)}, 1000.0));
            EAU.compressorRecipes.addRecipe(Recipe(findItemStack("Coal Dust", 4),
                findItemStack("Coal Plate"), 40000.0))
            EAU.compressorRecipes.addRecipe(Recipe(findItemStack("Coal Plate", 4),
                findItemStack("Graphite Rod"), 80000.0))
            EAU.compressorRecipes.addRecipe(Recipe(ItemStack(Blocks.sand),
                findItemStack("Dielectric"), 2000.0))
            EAU.compressorRecipes.addRecipe(Recipe(ItemStack(Blocks.log),
                findItemStack("Tree Resin"), 3000.0))
        }

        private fun recipePlateMachine() {
            val f = 10000f
            EAU.plateMachineRecipes.addRecipe(Recipe(
                findItemStack("Copper Ingot", EAU.plateConversionRatio),
                findItemStack("Copper Plate"), 1.0 * f))
            EAU.plateMachineRecipes.addRecipe(Recipe(findItemStack("Lead Ingot", EAU.plateConversionRatio),
                findItemStack("Lead Plate"), 1.0 * f))
            EAU.plateMachineRecipes.addRecipe(Recipe(
                findItemStack("Silicon Ingot", 4),
                findItemStack("Silicon Plate"), 1.0 * f))
            EAU.plateMachineRecipes.addRecipe(Recipe(findItemStack("Alloy Ingot", EAU.plateConversionRatio),
                findItemStack("Alloy Plate"), 1.0 * f))
            EAU.plateMachineRecipes.addRecipe(Recipe(ItemStack(Items.iron_ingot, EAU.plateConversionRatio,
                0), findItemStack("Iron Plate"), 1.0 * f))
            EAU.plateMachineRecipes.addRecipe(Recipe(ItemStack(Items.gold_ingot, EAU.plateConversionRatio,
                0), findItemStack("Gold Plate"), 1.0 * f))
        }

        private fun recipeMagnetizer() {
            EAU.magnetiserRecipes.addRecipe(Recipe(ItemStack(Items.iron_ingot, 2), arrayOf(findItemStack("Basic Magnet")), 5000.0))
            EAU.magnetiserRecipes.addRecipe(Recipe(findItemStack("Alloy Ingot", 2), arrayOf(findItemStack("Advanced Magnet")), 15000.0))
            EAU.magnetiserRecipes.addRecipe(Recipe(findItemStack("Copper Dust", 1), arrayOf(ItemStack(Items.redstone)), 5000.0))
            EAU.magnetiserRecipes.addRecipe(Recipe(findItemStack("Basic Magnet", 3), arrayOf(findItemStack("Optimal Ferromagnetic Core")), 5000.0))
            EAU.magnetiserRecipes.addRecipe(Recipe(findItemStack("Inert Canister", 1), arrayOf(ItemStack(Items.ender_pearl)), 150000.0))
        }

        private fun recipeFuelBurnerItem() {
            addRecipe(findItemStack("Small Fuel Burner"),
                listOf(
                    "   ",
                    " Cc",
                    "   "
                ),
                mapOf(
                    Pair('C', "Combustion Chamber"),
                    Pair('c', "Copper Thermal Cable")
                )
            )
            addRecipe(findItemStack("Medium Fuel Burner"),
                listOf(
                    "   ",
                    " Cc",
                    " C "
                ),
                mapOf(
                    Pair('C', "Combustion Chamber"),
                    Pair('c', "Copper Thermal Cable")
                )
            )
            addRecipe(findItemStack("Big Fuel Burner"),
                listOf(
                    "   ",
                    "CCc",
                    "CC "
                ),
                mapOf(
                    Pair('C', "Combustion Chamber"),
                    Pair('c', "Copper Thermal Cable")
                )
            )
        }

        private fun recipeDisplays() {
            addRecipe(findItemStack("Digital Display", 1),
                listOf(
                    "   ",
                    "rrr",
                    "iii"
                ),
                mapOf(
                    Pair('r', "redstone"),
                    Pair('i', "Iron Cable")
                )
            )
            addRecipe(findItemStack("Nixie Tube", 1),
                listOf(
                    " g ",
                    "grg",
                    "iii"
                ),
                mapOf(
                    Pair('g', "Glass Pane"),
                    Pair('r', "redstone"),
                    Pair('i', "Iron Cable")
                )
            )
        }

        private fun recipeECoal() {
            addRecipe(findItemStack("E-Coal Helmet"),
                listOf(
                    "PPP",
                    "PCP"
                ),
                mapOf(
                    Pair('P', "plateCoal"),
                    Pair('C', "Portable Condensator")
                )
            )

            addRecipe(findItemStack("E-Coal Boots"),
                listOf(
                    " C ",
                    "P P",
                    "P P"
                ),
                mapOf(
                    Pair('P', "plateCoal"),
                    Pair('C', "Portable Condensator")
                )
            )

            addRecipe(findItemStack("E-Coal Chestplate"),
                listOf(
                    "P P",
                    "PCP",
                    "PPP"
                ),
                mapOf(
                    Pair('P', "plateCoal"),
                    Pair('C', "Portable Condensator")
                )
            )

            addRecipe(findItemStack("E-Coal Leggings"),
                listOf(
                    "PPP",
                    "PCP",
                    "P P"
                ),
                mapOf(
                    Pair('P', "plateCoal"),
                    Pair('C', "Portable Condensator")
                )
            )
        }

        private fun recipeGridDevices() {
            var poleRecipes = 0
            for (oreName in arrayOf(
                "ingotAluminum",
                "ingotAluminium",
                "ingotSteel")) {
                if (EAU.oreNames.contains(oreName)) {
                    addRecipe(findItemStack("Utility Pole"),
                        listOf(
                            "WWW",
                            "IWI",
                            " W "
                        ),
                        mapOf(
                            Pair('W', "logWood"),
                            Pair('I', "oreName")
                        )
                    )
                    poleRecipes++
                }
            }
            if (poleRecipes == 0) {
                // Really?
                addRecipe(findItemStack("Utility Pole"),
                    listOf(
                        "WWW",
                        "IWI",
                        " W "
                    ),
                    mapOf(
                        Pair('I', "ingotIron"),
                        Pair('W', "logWood")
                    )
                )
            }
            addRecipe(findItemStack("Utility Pole w/DC-DC Converter"),
                listOf(
                    "HHH",
                    " TC",
                    " PH"
                ),
                mapOf(
                    Pair('P', "Utility Pole"),
                    Pair('H', "High Voltage Cable"),
                    Pair('C', "Optimal Ferromagnetic Core"),
                    Pair('T', "DC-DC Converter")
                )
            )

            // I don't care what you think, if your modpack lacks steel then you don't *need* this much power.
            // Or just use the new Arc furnace. Other mod's steel methods are slow and tedious and require huge multiblocks.
            // Feel free to add alternate non-iron recipes, though. Here, or by minetweaker.
            for (type in arrayOf(
                "Aluminum",
                "Aluminium",
                "Steel"
            )) {
                val blockType = "block$type"
                val ingotType = "ingot$type"
                if (EAU.oreNames.contains(blockType)) {
                    addRecipe(findItemStack("Transmission Tower"),
                        listOf(
                            "ii ",
                            "mi ",
                            " B "
                        ),
                        mapOf(
                            Pair('i',ingotType),
                            Pair('B',blockType),
                            Pair('m', "Machine Block")
                        )
                    )
                    addRecipe(findItemStack("Grid DC-DC Converter"),
                        listOf(
                            "i i",
                            "mtm",
                            "imi"
                        ),
                        mapOf(
                            Pair('i',ingotType),
                            Pair('t',"DC-DC Converter"),
                            Pair('m',"Advanced Machine Block"))
                    )
                }
            }
        }
    }
}
