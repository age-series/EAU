package org.ja13.eau.crafting

import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.launchwrapper.LogWrapper
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.ShapedOreRecipe
import org.ja13.eau.EAU
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
            /*
            recipeTool()
            recipeGround()
            recipeElectricalSource()
            recipeElectricalCable()
            recipeThermalCable()
            recipeLampSocket()
            recipeLampSupply()
            recipePowerSocket()
            recipePassiveComponent()
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
            recipeChips()
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
            recipeGridDevices()*/
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

        /*
        private fun recipeTool() {
            addRecipe(ItemStack(EAU.shovelCopper),
                "i",
                "s",
                "s",
                'i', "ingotCopper",
                's', ItemStack(Items.stick))
            addRecipe(ItemStack(EAU.axeCopper),
                "ii",
                "is",
                " s",
                'i', "ingotCopper",
                's', ItemStack(Items.stick))
            addRecipe(ItemStack(EAU.hoeCopper),
                "ii",
                " s",
                " s",
                'i', "ingotCopper",
                's', ItemStack(Items.stick))
            addRecipe(ItemStack(EAU.pickaxeCopper),
                "iii",
                " s ",
                " s ",
                'i', "ingotCopper",
                's', ItemStack(Items.stick))
            addRecipe(ItemStack(EAU.swordCopper),
                "i",
                "i",
                "s",
                'i', "ingotCopper",
                's', ItemStack(Items.stick))
        }

        private fun recipeGround() {
            addRecipe(findItemStack("Ground Cable"),
                " C ",
                " C ",
                "CCC",
                'C', findItemStack("Copper Cable"))
        }

        private fun recipeElectricalSource() {
            // Trololol
        }

        private fun recipeElectricalCable() {
            addRecipe(EAU.signalCableDescriptor.newItemStack(2),  //signal wire
                "R",  //rubber
                "C",  //iron cable
                "C",
                'C', findItemStack("Iron Cable"),
                'R', "itemRubber")
            addRecipe(EAU.lowVoltageCableDescriptor.newItemStack(2),  //Low Voltage Cable
                "R",
                "C",
                "C",
                'C', findItemStack("Copper Cable"),
                'R', "itemRubber")
            addRecipe(EAU.meduimVoltageCableDescriptor.newItemStack(1),  //Meduim Voltage Cable (Medium Voltage Cable)
                "R",
                "C",
                'C', EAU.lowVoltageCableDescriptor.newItemStack(1),
                'R', "itemRubber")
            addRecipe(EAU.highVoltageCableDescriptor.newItemStack(1),  //High Voltage Cable
                "R",
                "C",
                'C', EAU.meduimVoltageCableDescriptor.newItemStack(1),
                'R', "itemRubber")
            addRecipe(EAU.signalCableDescriptor.newItemStack(12),  //Signal Wire
                "RRR",
                "CCC",
                "RRR",
                'C', ItemStack(Items.iron_ingot),
                'R', "itemRubber")
            addRecipe(EAU.signalBusCableDescriptor.newItemStack(1),
                "R",
                "C",
                'C', EAU.signalCableDescriptor.newItemStack(1),
                'R', "itemRubber")
            addRecipe(EAU.lowVoltageCableDescriptor.newItemStack(12),
                "RRR",
                "CCC",
                "RRR",
                'C', "ingotCopper",
                'R', "itemRubber")
            addRecipe(EAU.veryHighVoltageCableDescriptor.newItemStack(12),
                "RRR",
                "CCC",
                "RRR",
                'C', "ingotAlloy",
                'R', "itemRubber")
        }

        private fun recipeThermalCable() {
            addRecipe(findItemStack("Copper Thermal Cable", 12),
                "SSS",
                "CCC",
                "SSS",
                'S', ItemStack(Blocks.cobblestone),
                'C', "ingotCopper")
            addRecipe(findItemStack("Copper Thermal Cable", 1),
                "S",
                "C",
                'S', ItemStack(Blocks.cobblestone),
                'C', findItemStack("Copper Cable"))
        }

        private fun recipeLampSocket() {
            addRecipe(findItemStack("Lamp Socket A", 3),
                "G ",
                "IG",
                "G ",
                'G', ItemStack(Blocks.glass_pane),
                'I', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Lamp Socket B Projector", 3),
                " G",
                "GI",
                " G",
                'G', ItemStack(Blocks.glass_pane),
                'I', ItemStack(Items.iron_ingot))
            addRecipe(findItemStack("Street Light", 1),
                "G",
                "I",
                "I",
                'G', ItemStack(Blocks.glass_pane),
                'I', ItemStack(Items.iron_ingot))
            addRecipe(findItemStack("Robust Lamp Socket", 3),
                "GIG",
                'G', ItemStack(Blocks.glass_pane),
                'I', ItemStack(Items.iron_ingot))
            addRecipe(findItemStack("Flat Lamp Socket", 3),
                "IGI",
                'G', ItemStack(Blocks.glass_pane),
                'I', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Simple Lamp Socket", 3),
                " I ",
                "GGG",
                'G', ItemStack(Blocks.glass_pane),
                'I', ItemStack(Items.iron_ingot))
            addRecipe(findItemStack("Fluorescent Lamp Socket", 3),
                " I ",
                "G G",
                'G', findItemStack("Iron Cable"),
                'I', ItemStack(Items.iron_ingot))
            addRecipe(findItemStack("Suspended Lamp Socket", 2),
                "I",
                "G",
                'G', findItemStack("Robust Lamp Socket"),
                'I', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Long Suspended Lamp Socket", 2),
                "I",
                "I",
                "G",
                'G', findItemStack("Robust Lamp Socket"),
                'I', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Suspended Lamp Socket (No Swing)", 4),
                "I",
                "G",
                'G', findItemStack("Robust Lamp Socket"),
                'I', ItemStack(Items.iron_ingot))
            addRecipe(findItemStack("Long Suspended Lamp Socket (No Swing)", 4),
                "I",
                "I",
                "G",
                'G', findItemStack("Robust Lamp Socket"),
                'I', ItemStack(Items.iron_ingot))
            addRecipe(findItemStack("Sconce Lamp Socket", 2),
                "GCG",
                "GIG",
                'G', ItemStack(Blocks.glass_pane),
                'C', "dustCoal",
                'I', ItemStack(Items.iron_ingot))
            addRecipe(findItemStack("50V Emergency Lamp"),
                "cbc",
                " l ",
                " g ",
                'c', findItemStack("Low Voltage Cable"),
                'b', findItemStack("Portable Battery Pack"),
                'l', findItemStack("50V LED Bulb"),
                'g', ItemStack(Blocks.glass_pane))
            addRecipe(findItemStack("200V Emergency Lamp"),
                "cbc",
                " l ",
                " g ",
                'c', findItemStack("Medium Voltage Cable"),
                'b', findItemStack("Portable Battery Pack"),
                'l', findItemStack("200V LED Bulb"),
                'g', ItemStack(Blocks.glass_pane))
        }

        private fun recipeLampSupply() {
            addRecipe(findItemStack("Lamp Supply", 1),
                " I ",
                "ICI",
                " I ",
                'C', "ingotCopper",
                'I', ItemStack(Items.iron_ingot))
        }

        private fun recipePowerSocket() {
            addRecipe(findItemStack("50V Power Socket", 16),
                "RUR",
                "ACA",
                'R', "itemRubber",
                'U', findItemStack("Copper Plate"),
                'A', findItemStack("Alloy Plate"),
                'C', findItemStack("Low Voltage Cable"))
            addRecipe(findItemStack("200V Power Socket", 16),
                "RUR",
                "ACA",
                'R', "itemRubber",
                'U', findItemStack("Copper Plate"),
                'A', findItemStack("Alloy Plate"),
                'C', findItemStack("Medium Voltage Cable"))
        }

        private fun recipePassiveComponent() {
            addRecipe(findItemStack("Signal Diode", 4),
                " RB",
                " IR",
                " RB",
                'R', ItemStack(Items.redstone),
                'I', findItemStack("Iron Cable"),
                'B', "itemRubber")
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

        private fun recipeSwitch() {
            addRecipe(findItemStack("Low Voltage Switch"),
                "  I",
                " I ",
                "CAC",
                'R', ItemStack(Items.redstone),
                'A', "itemRubber",
                'I', findItemStack("Copper Cable"),
                'C', findItemStack("Low Voltage Cable"))
            addRecipe(findItemStack("Medium Voltage Switch"),
                "  I",
                "AIA",
                "CAC",
                'R', ItemStack(Items.redstone),
                'A', "itemRubber",
                'I', findItemStack("Copper Cable"),
                'C', findItemStack("Medium Voltage Cable"))
            addRecipe(findItemStack("High Voltage Switch"),
                "AAI",
                "AIA",
                "CAC",
                'R', ItemStack(Items.redstone),
                'A', "itemRubber",
                'I', findItemStack("Copper Cable"),
                'C', findItemStack("High Voltage Cable"))
        }

        private fun recipeWirelessSignal() {
            addRecipe(findItemStack("Wireless Signal Transmitter"),
                " S ",
                " R ",
                "ICI",
                'R', ItemStack(Items.redstone),
                'I', findItemStack("Iron Cable"),
                'C', EAU.dictCheapChip,
                'S', findItemStack("Signal Antenna"))
            addRecipe(findItemStack("Wireless Signal Repeater"),
                "S S",
                "R R",
                "ICI",
                'R', ItemStack(Items.redstone),
                'I', findItemStack("Iron Cable"),
                'C', EAU.dictCheapChip,
                'S', findItemStack("Signal Antenna"))
            addRecipe(findItemStack("Wireless Signal Receiver"),
                " S ",
                "ICI",
                'R', ItemStack(Items.redstone),
                'I', findItemStack("Iron Cable"),
                'C', EAU.dictCheapChip,
                'S', findItemStack("Signal Antenna"))
        }

        private fun recipeElectricalRelay() {
            addRecipe(findItemStack("Low Voltage Relay"),
                "GGG",
                "OIO",
                "CRC",
                'R', ItemStack(Items.redstone),
                'O', findItemStack("Iron Cable"),
                'G', ItemStack(Blocks.glass_pane),
                'A', "itemRubber",
                'I', findItemStack("Copper Cable"),
                'C', findItemStack("Low Voltage Cable"))
            addRecipe(findItemStack("Medium Voltage Relay"),
                "GGG",
                "OIO",
                "CRC",
                'R', ItemStack(Items.redstone),
                'O', findItemStack("Iron Cable"),
                'G', ItemStack(Blocks.glass_pane),
                'A', "itemRubber",
                'I', findItemStack("Copper Cable"),
                'C', findItemStack("Medium Voltage Cable"))
            addRecipe(findItemStack("High Voltage Relay"),
                "GGG",
                "OIO",
                "CRC",
                'R', ItemStack(Items.redstone),
                'O', findItemStack("Iron Cable"),
                'G', ItemStack(Blocks.glass_pane),
                'A', "itemRubber",
                'I', findItemStack("Copper Cable"),
                'C', findItemStack("High Voltage Cable"))
        }

        private fun recipeElectricalDataLogger() {
            addRecipe(findItemStack("Data Logger", 1),
                "RRR",
                "RGR",
                "RCR",
                'R', "itemRubber",
                'C', EAU.dictCheapChip,
                'G', ItemStack(Blocks.glass_pane))
            addRecipe(findItemStack("Modern Data Logger", 1),
                "RRR",
                "RGR",
                "RCR",
                'R', "itemRubber",
                'C', EAU.dictAdvancedChip,
                'G', ItemStack(Blocks.glass_pane))
            addRecipe(findItemStack("Industrial Data Logger", 1),
                "RRR",
                "GGG",
                "RCR",
                'R', "itemRubber",
                'C', EAU.dictAdvancedChip,
                'G', ItemStack(Blocks.glass_pane))
        }

        private fun recipeElectricalGateSource() {
            addRecipe(findItemStack("Signal Trimmer", 1),
                "RsR",
                "rRr",
                " c ",
                'M', findItemStack("Machine Block"),
                'c', findItemStack("Signal Cable"),
                'r', "itemRubber",
                's', ItemStack(Items.stick),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("Signal Switch", 3),
                " r ",
                "rRr",
                " c ",
                'M', findItemStack("Machine Block"),
                'c', findItemStack("Signal Cable"),
                'r', "itemRubber",
                'I', findItemStack("Iron Cable"),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("Signal Button", 3),
                " R ",
                "rRr",
                " c ",
                'M', findItemStack("Machine Block"),
                'c', findItemStack("Signal Cable"),
                'r', "itemRubber",
                'I', findItemStack("Iron Cable"),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("Wireless Switch", 3),
                " a ",
                "rCr",
                " r ",
                'M', findItemStack("Machine Block"),
                'c', findItemStack("Signal Cable"),
                'C', EAU.dictCheapChip,
                'a', findItemStack("Signal Antenna"),
                'r', "itemRubber",
                'I', findItemStack("Iron Cable"),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("Wireless Button", 3),
                " a ",
                "rCr",
                " R ",
                'M', findItemStack("Machine Block"),
                'c', findItemStack("Signal Cable"),
                'C', EAU.dictCheapChip,
                'a', findItemStack("Signal Antenna"),
                'r', "itemRubber",
                'I', findItemStack("Iron Cable"),
                'R', ItemStack(Items.redstone))

            // Wireless Switch
            // Wireless Button
        }

        private fun recipeElectricalBreaker() {
            addRecipe(findItemStack("Electrical Breaker", 1),
                "crC",
                'c', findItemStack("Overvoltage Protection"),
                'C', findItemStack("Overheating Protection"),
                'r', findItemStack("High Voltage Relay"))
        }

        private fun recipeFuses() {
            addRecipe(findItemStack("Electrical Fuse Holder", 1),
                "i",
                " ",
                "i",
                'i', findItemStack("Iron Cable"))
            /*
            addRecipe(findItemStack("Lead Fuse for low voltage cables", 4),
                "rcr",
                'r', findItemStack("itemRubber"),
                'c', findItemStack("Low Voltage Cable"))
             */
        }

        private fun recipeElectricalVuMeter() {
            for (idx in 0..3) {
                addRecipe(findItemStack("Analog vuMeter", 1),
                    "WWW",
                    "RIr",
                    "WSW",
                    'W', ItemStack(Blocks.planks, 1, idx),
                    'R', ItemStack(Items.redstone),
                    'I', findItemStack("Iron Cable"),
                    'r', ItemStack(Items.dye, 1, 1),
                    'S', findItemStack("Signal Cable"))
            }
            for (idx in 0..3) {
                addRecipe(findItemStack("LED vuMeter", 1),
                    " W ",
                    "WTW",
                    " S ",
                    'W', ItemStack(Blocks.planks, 1, idx),
                    'T', ItemStack(Blocks.redstone_torch),
                    'S', findItemStack("Signal Cable"))
            }
        }

        private fun recipeElectricalEnvironmentalSensor() {
            addShapelessRecipe(findItemStack("Electrical Daylight Sensor"),
                ItemStack(Blocks.daylight_detector),
                findItemStack("Redstone-to-Voltage Converter"))
            addShapelessRecipe(findItemStack("Electrical Light Sensor"),
                ItemStack(Blocks.daylight_detector),
                ItemStack(Items.quartz),
                findItemStack("Redstone-to-Voltage Converter"))
            addRecipe(findItemStack("Electrical Weather Sensor"),
                " r ",
                "rRr",
                " r ",
                'R', ItemStack(Items.redstone),
                'r', "itemRubber")
            addRecipe(findItemStack("Electrical Anemometer Sensor"),
                " I ",
                " R ",
                "I I",
                'R', ItemStack(Items.redstone),
                'I', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Electrical Entity Sensor"),
                " G ",
                "GRG",
                " G ",
                'G', ItemStack(Blocks.glass_pane),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("Electrical Fire Detector"),
                "cbr",
                "p p",
                "r r",
                'c', findItemStack("Signal Cable"),
                'b', EAU.dictCheapChip,
                'r', "itemRubber",
                'p', "plateCopper")
            addRecipe(findItemStack("Electrical Fire Buzzer"),
                "rar",
                "p p",
                "r r",
                'a', EAU.dictAdvancedChip,
                'r', "itemRubber",
                'p', "plateCopper")
            addShapelessRecipe(findItemStack("Scanner"),
                ItemStack(Items.comparator),
                EAU.dictAdvancedChip)
        }

        private fun recipeElectricalRedstone() {
            addRecipe(findItemStack("Redstone-to-Voltage Converter", 1),
                "TCS",
                'S', findItemStack("Signal Cable"),
                'C', EAU.dictCheapChip,
                'T', ItemStack(Blocks.redstone_torch))
            addRecipe(findItemStack("Voltage-to-Redstone Converter", 1),
                "CTR",
                'R', ItemStack(Items.redstone),
                'C', EAU.dictCheapChip,
                'T', ItemStack(Blocks.redstone_torch))
        }

        private fun recipeElectricalGate() {
            addShapelessRecipe(findItemStack("Electrical Timer"),
                ItemStack(Items.repeater),
                EAU.dictCheapChip)
            addRecipe(findItemStack("Signal Processor", 1),
                "IcI",
                "cCc",
                "IcI",
                'I', ItemStack(Items.iron_ingot),
                'c', findItemStack("Signal Cable"),
                'C', EAU.dictCheapChip)
        }

        private fun recipeElectricalAlarm() {
            addRecipe(findItemStack("Nuclear Alarm", 1),
                "ITI",
                "IMI",
                "IcI",
                'c', findItemStack("Signal Cable"),
                'T', ItemStack(Blocks.redstone_torch),
                'I', findItemStack("Iron Cable"),
                'M', ItemStack(Blocks.noteblock))
            addRecipe(findItemStack("Standard Alarm", 1),
                "MTM",
                "IcI",
                "III",
                'c', findItemStack("Signal Cable"),
                'T', ItemStack(Blocks.redstone_torch),
                'I', findItemStack("Iron Cable"),
                'M', ItemStack(Blocks.noteblock))
        }

        private fun recipeElectricalSensor() {
            addRecipe(findItemStack("Voltage Probe", 1),
                "SC",
                'S', findItemStack("Electrical Probe Chip"),
                'C', findItemStack("Signal Cable"))
            addRecipe(findItemStack("Electrical Probe", 1),
                "SCS",
                'S', findItemStack("Electrical Probe Chip"),
                'C', findItemStack("Signal Cable"))
        }

        private fun recipeThermalSensor() {
            addRecipe(findItemStack("Thermal Probe", 1),
                "SCS",
                'S', findItemStack("Thermal Probe Chip"),
                'C', findItemStack("Signal Cable"))
            addRecipe(findItemStack("Temperature Probe", 1),
                "SC",
                'S', findItemStack("Thermal Probe Chip"),
                'C', findItemStack("Signal Cable"))
        }

        private fun recipeSixNodeMisc() {
            addRecipe(findItemStack("Analog Watch"),
                "crc",
                "III",
                'c', findItemStack("Iron Cable"),
                'r', ItemStack(Items.redstone),
                'I', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Digital Watch"),
                "rcr",
                "III",
                'c', findItemStack("Iron Cable"),
                'r', ItemStack(Items.redstone),
                'I', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Hub"),
                "I I",
                " c ",
                "I I",
                'c', findItemStack("Copper Cable"),
                'I', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Energy Meter"),
                "IcI",
                "IRI",
                "IcI",
                'c', findItemStack("Copper Cable"),
                'R', EAU.dictCheapChip,
                'I', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Advanced Energy Meter"),
                " c ",
                "PRP",
                " c ",
                'c', findItemStack("Copper Cable"),
                'R', EAU.dictAdvancedChip,
                'P', findItemStack("Iron Plate"))
        }

        private fun recipeTurret() {
            addRecipe(findItemStack("Defence Turret", 1),
                " R ",
                "CMC",
                " c ",
                'M', findItemStack("Advanced Machine Block"),
                'C', EAU.dictAdvancedChip,
                'c', EAU.highVoltageCableDescriptor.newItemStack(),
                'R', ItemStack(Blocks.redstone_block))
        }

        private fun recipeMachine() {
            addRecipe(findItemStack("50V Macerator", 1),
                "IRI",
                "FMF",
                "IcI",
                'M', findItemStack("Machine Block"),
                'c', findItemStack("Electrical Motor"),
                'F', ItemStack(Items.flint),
                'I', findItemStack("Iron Cable"),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("200V Macerator", 1),
                "ICI",
                "DMD",
                "IcI",
                'M', findItemStack("Advanced Machine Block"),
                'C', EAU.dictAdvancedChip,
                'c', findItemStack("Advanced Electrical Motor"),
                'D', ItemStack(Items.diamond),
                'I', "ingotAlloy")
            addRecipe(findItemStack("50V Compressor", 1),
                "IRI",
                "FMF",
                "IcI",
                'M', findItemStack("Machine Block"),
                'c', findItemStack("Electrical Motor"),
                'F', "plateIron",
                'I', findItemStack("Iron Cable"),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("200V Compressor", 1),
                "ICI",
                "DMD",
                "IcI",
                'M', findItemStack("Advanced Machine Block"),
                'C', EAU.dictAdvancedChip,
                'c', findItemStack("Advanced Electrical Motor"),
                'D', "plateAlloy",
                'I', "ingotAlloy")
            addRecipe(findItemStack("50V Plate Machine", 1),
                "IRI",
                "IMI",
                "IcI",
                'M', findItemStack("Machine Block"),
                'c', findItemStack("Electrical Motor"),
                'I', findItemStack("Iron Cable"),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("200V Plate Machine", 1),
                "DCD",
                "DMD",
                "DcD",
                'M', findItemStack("Advanced Machine Block"),
                'C', EAU.dictAdvancedChip,
                'c', findItemStack("Advanced Electrical Motor"),
                'D', "plateAlloy",
                'I', "ingotAlloy")
            addRecipe(findItemStack("50V Magnetizer", 1),
                "IRI",
                "cMc",
                "III",
                'M', findItemStack("Machine Block"),
                'c', findItemStack("Electrical Motor"),
                'I', findItemStack("Iron Cable"),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("200V Magnetizer", 1),
                "ICI",
                "cMc",
                "III",
                'M', findItemStack("Advanced Machine Block"),
                'C', EAU.dictAdvancedChip,
                'c', findItemStack("Advanced Electrical Motor"),
                'I', "ingotAlloy")
        }

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

        private fun recipeTransformer() {
            addRecipe(findItemStack("DC-DC Converter"),
                "C C",
                "III",
                'C', findItemStack("Copper Cable"),
                'I', ItemStack(Items.iron_ingot))
            addRecipe(findItemStack("Variable DC-DC Converter"),
                "CBC",
                "III",
                'C', findItemStack("Copper Cable"),
                'I', ItemStack(Items.iron_ingot),
                'B', EAU.dictCheapChip)
        }

        private fun recipeHeatFurnace() {
            addRecipe(findItemStack("Stone Heat Furnace"),
                "BBB",
                "BIB",
                "BiB",
                'B', ItemStack(Blocks.stone),
                'i', findItemStack("Copper Thermal Cable"),
                'I', findItemStack("Combustion Chamber"))
            addRecipe(findItemStack("Fuel Heat Furnace"),
                "IcI",
                "mCI",
                "IiI",
                'c', findItemStack("Cheap Chip"),
                'm', findItemStack("Electrical Motor"),
                'C', ItemStack(Items.cauldron),
                'I', ItemStack(Items.iron_ingot),
                'i', findItemStack("Copper Thermal Cable"))
        }

        private fun recipeTurbine() {
            addRecipe(findItemStack("50V Turbine"),
                " m ",
                "HMH",
                " E ",
                'M', findItemStack("Machine Block"),
                'E', findItemStack("Low Voltage Cable"),
                'H', findItemStack("Copper Thermal Cable"),
                'm', findItemStack("Electrical Motor")
            )
            addRecipe(findItemStack("200V Turbine"),
                "ImI",
                "HMH",
                "IEI",
                'I', "itemRubber",
                'M', findItemStack("Advanced Machine Block"),
                'E', findItemStack("Medium Voltage Cable"),
                'H', findItemStack("Copper Thermal Cable"),
                'm', findItemStack("Advanced Electrical Motor"))
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
            addRecipe(findItemStack("Cost Oriented Battery"),
                "C C",
                "PPP",
                "PPP",
                'C', findItemStack("Low Voltage Cable"),
                'P', "ingotLead",
                'I', ItemStack(Items.iron_ingot))
        }

        private fun recipeElectricalFurnace() {
            addRecipe(findItemStack("Electrical Furnace"),
                "III",
                "IFI",
                "ICI",
                'C', findItemStack("Low Voltage Cable"),
                'F', ItemStack(Blocks.furnace),
                'I', ItemStack(Items.iron_ingot))
            addShapelessRecipe(findItemStack("Canister of Water", 1),
                findItemStack("Inert Canister"),
                ItemStack(Items.water_bucket))
        }

        private fun recipeAutoMiner() {
            addRecipe(findItemStack("Auto Miner"),
                "MCM",
                "BOB",
                " P ",
                'C', EAU.dictAdvancedChip,
                'O', findItemStack("Ore Scanner"),
                'B', findItemStack("Advanced Machine Block"),
                'M', findItemStack("Advanced Electrical Motor"),
                'P', findItemStack("Mining Pipe"))
        }

        private fun recipeSolarPanel() {
            addRecipe(findItemStack("Small Solar Panel"),
                "LLL",
                "CSC",
                "III",
                'S', "plateSilicon",
                'L', findItemStack("Lapis Dust"),
                'I', ItemStack(Items.iron_ingot),
                'C', findItemStack("Low Voltage Cable"))
            addRecipe(findItemStack("Small Rotating Solar Panel"),
                "ISI",
                "I I",
                'S', findItemStack("Small Solar Panel"),
                'M', findItemStack("Electrical Motor"),
                'I', ItemStack(Items.iron_ingot))
            for (metal in arrayOf("blockSteel", "blockAluminum", "blockAluminium", "casingMachineAdvanced")) {
                for (panel in arrayOf("Small Solar Panel", "Small Rotating Solar Panel")) {
                    addRecipe(findItemStack("2x3 Solar Panel"),
                        "PPP",
                        "PPP",
                        "I I",
                        'P', findItemStack(panel),
                        'I', metal)
                }
            }
            addRecipe(findItemStack("2x3 Rotating Solar Panel"),
                "ISI",
                "IMI",
                "I I",
                'S', findItemStack("2x3 Solar Panel"),
                'M', findItemStack("Electrical Motor"),
                'I', ItemStack(Items.iron_ingot))
        }

        private fun recipeThermalDissipatorPassiveAndActive() {
            addRecipe(
                findItemStack("Small Passive Thermal Dissipator"),
                "I I",
                "III",
                "CIC",
                'I', "ingotCopper",
                'C', findItemStack("Copper Thermal Cable"))
            addRecipe(
                findItemStack("Small Active Thermal Dissipator"),
                "RMR",
                " D ",
                'D', findItemStack("Small Passive Thermal Dissipator"),
                'M', findItemStack("Electrical Motor"),
                'R', "itemRubber")
            addRecipe(
                findItemStack("200V Active Thermal Dissipator"),
                "RMR",
                " D ",
                'D', findItemStack("Small Passive Thermal Dissipator"),
                'M', findItemStack("Advanced Electrical Motor"),
                'R', "itemRubber")
        }

        private fun recipeElectricalAntenna() {
            addRecipe(findItemStack("Low Power Transmitter Antenna", 1),
                "R i",
                "CI ",
                "R i",
                'C', EAU.dictCheapChip,
                'i', ItemStack(Items.iron_ingot),
                'I', "plateIron",
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("Low Power Receiver Antenna", 1),
                "i  ",
                " IC",
                "i  ",
                'C', EAU.dictCheapChip,
                'I', "plateIron",
                'i', ItemStack(Items.iron_ingot),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("Medium Power Transmitter Antenna", 1),
                "c I",
                "CI ",
                "c I",
                'C', EAU.dictAdvancedChip,
                'c', EAU.dictCheapChip,
                'I', "plateIron",
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("Medium Power Receiver Antenna", 1),
                "I  ",
                " IC",
                "I  ",
                'C', EAU.dictAdvancedChip,
                'I', "plateIron",
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("High Power Transmitter Antenna", 1),
                "C I",
                "CI ",
                "C I",
                'C', EAU.dictAdvancedChip,
                'c', EAU.dictCheapChip,
                'I', "plateIron",
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("High Power Receiver Antenna", 1),
                "I D",
                " IC",
                "I D",
                'C', EAU.dictAdvancedChip,
                'I', "plateIron",
                'R', ItemStack(Items.redstone),
                'D', ItemStack(Items.diamond))
        }

        private fun recipeEggIncubator() {
            addRecipe(findItemStack("50V Egg Incubator", 1),
                "IGG",
                "E G",
                "CII",
                'C', EAU.dictCheapChip,
                'E', findItemStack("Small 50V Tungsten Heating Corp"),
                'I', ItemStack(Items.iron_ingot),
                'G', ItemStack(Blocks.glass_pane))
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
                "LLL",
                "L  ",
                "LLL",
                'L', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Average Ferromagnetic Core"),
                "PCP",
                'C', findItemStack("Cheap Ferromagnetic Core"),
                'P', "plateIron")
            addRecipe(findItemStack("Optimal Ferromagnetic Core"),
                " P ",
                "PCP",
                " P ",
                'C', findItemStack("Average Ferromagnetic Core"),
                'P', "plateIron")
        }

        private fun recipeDust() {
            addShapelessRecipe(findItemStack("Alloy Dust", 6),
                "dustIron",
                "dustCoal",
                EAU.dictTungstenDust,
                EAU.dictTungstenDust,
                EAU.dictTungstenDust,
                EAU.dictTungstenDust)
        }

        private fun recipeElectricalMotor() {
            addRecipe(findItemStack("Electrical Motor"),
                " C ",
                "III",
                "C C",
                'I', findItemStack("Iron Cable"),
                'C', findItemStack("Low Voltage Cable"))
            addRecipe(findItemStack("Advanced Electrical Motor"),
                "RCR",
                "MIM",
                "CRC",
                'M', findItemStack("Advanced Magnet"),
                'I', ItemStack(Items.iron_ingot),
                'R', ItemStack(Items.redstone),
                'C', findItemStack("Medium Voltage Cable"))
        }

        private fun recipeSolarTracker() {
            addRecipe(findItemStack("Solar Tracker", 4),
                "VVV",
                "RQR",
                "III",
                'Q', ItemStack(Items.quartz),
                'V', ItemStack(Blocks.glass_pane),
                'R', ItemStack(Items.redstone),
                'G', ItemStack(Items.gold_ingot),
                'I', ItemStack(Items.iron_ingot))
        }

        private fun recipeMeter() {
            addRecipe(findItemStack("MultiMeter"),
                "RGR",
                "RER",
                "RCR",
                'G', ItemStack(Blocks.glass_pane),
                'C', findItemStack("Electrical Probe Chip"),
                'E', ItemStack(Items.redstone),
                'R', "itemRubber")
            addRecipe(findItemStack("Thermometer"),
                "RGR",
                "RER",
                "RCR",
                'G', ItemStack(Blocks.glass_pane),
                'C', findItemStack("Thermal Probe Chip"),
                'E', ItemStack(Items.redstone),
                'R', "itemRubber")
            addShapelessRecipe(findItemStack("AllMeter"),
                findItemStack("MultiMeter"),
                findItemStack("Thermometer"))
            addRecipe(findItemStack("Wireless Analyser"),
                " S ",
                "RGR",
                "RER",
                'G', ItemStack(Blocks.glass_pane),
                'S', findItemStack("Signal Antenna"),
                'E', ItemStack(Items.redstone),
                'R', "itemRubber")
            addRecipe(findItemStack("Config Copy Tool"),
                "wR",
                "RC",
                'w', findItemStack("Wrench"),
                'R', ItemStack(Items.redstone),
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
                'P', ItemStack(Items.iron_pickaxe))
            addRecipe(findItemStack("Average Electrical Drill"),
                "RCR",
                " D ",
                " d ",
                'R', Items.redstone,
                'C', EAU.dictCheapChip,
                'D', findItemStack("Cheap Electrical Drill"),
                'd', ItemStack(Items.diamond))
            addRecipe(findItemStack("Fast Electrical Drill"),
                "MCM",
                " T ",
                " P ",
                'T', findItemStack("Mining Pipe"),
                'C', EAU.dictAdvancedChip,
                'M', findItemStack("Advanced Electrical Motor"),
                'P', ItemStack(Items.diamond_pickaxe))
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
                'R', ItemStack(Items.redstone),
                'I', findItemStack("Iron Cable"),
                'G', ItemStack(Items.gold_ingot))
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
                'I', ItemStack(Items.iron_ingot))
            addRecipe(findItemStack("Tungsten Cable", 6),
                "III",
                'I', EAU.dictTungstenIngot)
        }

        private fun recipeGraphite() {
            addRecipe(findItemStack("Graphite Rod", 2),
                "I",
                'I', findItemStack("2x Graphite Rods"))
            addRecipe(findItemStack("Graphite Rod", 3),
                "I",
                'I', findItemStack("3x Graphite Rods"))
            addRecipe(findItemStack("Graphite Rod", 4),
                "I",
                'I', findItemStack("4x Graphite Rods"))
            addShapelessRecipe(
                findItemStack("2x Graphite Rods"),
                findItemStack("Graphite Rod"),
                findItemStack("Graphite Rod"))
            addShapelessRecipe(
                findItemStack("3x Graphite Rods"),
                findItemStack("Graphite Rod"),
                findItemStack("Graphite Rod"),
                findItemStack("Graphite Rod"))
            addShapelessRecipe(
                findItemStack("3x Graphite Rods"),
                findItemStack("Graphite Rod"),
                findItemStack("2x Graphite Rods"))
            addShapelessRecipe(
                findItemStack("4x Graphite Rods"),
                findItemStack("Graphite Rod"),
                findItemStack("Graphite Rod"),
                findItemStack("Graphite Rod"),
                findItemStack("Graphite Rod"))
            addShapelessRecipe(
                findItemStack("4x Graphite Rods"),
                findItemStack("2x Graphite Rods"),
                findItemStack("Graphite Rod"),
                findItemStack("Graphite Rod"))
            addShapelessRecipe(
                findItemStack("4x Graphite Rods"),
                findItemStack("2x Graphite Rods"),
                findItemStack("2x Graphite Rods"))
            addShapelessRecipe(
                findItemStack("4x Graphite Rods"),
                findItemStack("3x Graphite Rods"),
                findItemStack("Graphite Rod"))
            addShapelessRecipe(
                ItemStack(Items.diamond, 2),
                findItemStack("Synthetic Diamond"))
        }

        private fun recipeMiscItem() {
            addRecipe(findItemStack("Cheap Chip"),
                " R ",
                "RSR",
                " R ",
                'S', "ingotSilicon",
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("Advanced Chip"),
                "LRL",
                "RCR",
                "LRL",
                'C', EAU.dictCheapChip,
                'L', "ingotSilicon",
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("Machine Block"),
                "rLr",
                "LcL",
                "rLr",
                'L', findItemStack("Iron Cable"),
                'c', findItemStack("Copper Cable"),
                'r', findItemStack("Tree Resin")
            )
            addRecipe(findItemStack("Advanced Machine Block"),
                "rCr",
                "CcC",
                "rCr",
                'C', "plateAlloy",
                'r', findItemStack("Tree Resin"),
                'c', findItemStack("Copper Cable"))
            addRecipe(findItemStack("Electrical Probe Chip"),
                " R ",
                "RCR",
                " R ",
                'C', findItemStack("High Voltage Cable"),
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("Thermal Probe Chip"),
                " C ",
                "RIR",
                " C ",
                'G', ItemStack(Items.gold_ingot),
                'I', findItemStack("Iron Cable"),
                'C', "ingotCopper",
                'R', ItemStack(Items.redstone))
            addRecipe(findItemStack("Signal Antenna"),
                "c",
                "c",
                'c', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Machine Booster"),
                "m",
                "c",
                "m",
                'm', findItemStack("Electrical Motor"),
                'c', EAU.dictAdvancedChip)
            addRecipe(findItemStack("Wrench"),
                " c ",
                "cc ",
                "  c",
                'c', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Player Filter"),
                " g",
                "gc",
                " g",
                'g', ItemStack(Blocks.glass_pane),
                'c', ItemStack(Items.dye, 1, 2))
            addRecipe(findItemStack("Monster Filter"),
                " g",
                "gc",
                " g",
                'g', ItemStack(Blocks.glass_pane),
                'c', ItemStack(Items.dye, 1, 1))
            addRecipe(findItemStack("Casing", 1),
                "ppp",
                "p p",
                "ppp",
                'p', findItemStack("Iron Cable"))
            addRecipe(findItemStack("Iron Clutch Plate"),
                " t ",
                "tIt",
                " t ",
                'I', "plateIron",
                't', EAU.dictTungstenDust
            )
            addRecipe(findItemStack("Gold Clutch Plate"),
                " t ",
                "tGt",
                " t ",
                'G', "plateGold",
                't', EAU.dictTungstenDust
            )
            addRecipe(findItemStack("Copper Clutch Plate"),
                " t ",
                "tCt",
                " t ",
                'C', "plateCopper",
                't', EAU.dictTungstenDust
            )
            addRecipe(findItemStack("Lead Clutch Plate"),
                " t ",
                "tLt",
                " t ",
                'L', "plateLead",
                't', EAU.dictTungstenDust
            )
            addRecipe(findItemStack("Coal Clutch Plate"),
                " t ",
                "tCt",
                " t ",
                'C', "plateCoal",
                't', EAU.dictTungstenDust
            )
            addRecipe(findItemStack("Clutch Pin", 4),
                "s",
                "s",
                's', firstExistingOre(listOf("ingotSteel", "ingotAlloy"))
            )
        }

        private fun recipeBatteryItem() {
            addRecipe(findItemStack("Portable Battery"),
                " I ",
                "IPI",
                "IPI",
                'P', "ingotLead",
                'I', ItemStack(Items.iron_ingot))
            addShapelessRecipe(
                findItemStack("Portable Battery Pack"),
                findItemStack("Portable Battery"),
                findItemStack("Portable Battery"),
                findItemStack("Portable Battery"),
                findItemStack("Portable Battery"))
        }

        private fun recipeElectricalTool() {
            addRecipe(findItemStack("Small Flashlight"),
                "GLG",
                "IBI",
                " I ",
                'L', findItemStack("50V Incandescent Light Bulb"),
                'B', findItemStack("Portable Battery"),
                'G', ItemStack(Blocks.glass_pane),
                'I', ItemStack(Items.iron_ingot))
            addRecipe(findItemStack("Portable Electrical Mining Drill"),
                " T ",
                "IBI",
                " I ",
                'T', findItemStack("Average Electrical Drill"),
                'B', findItemStack("Portable Battery"),
                'I', ItemStack(Items.iron_ingot))
            addRecipe(findItemStack("Portable Electrical Axe"),
                " T ",
                "IMI",
                "IBI",
                'T', ItemStack(Items.iron_axe),
                'B', findItemStack("Portable Battery"),
                'M', findItemStack("Electrical Motor"),
                'I', ItemStack(Items.iron_ingot))
            if (EAU.xRayScannerCanBeCrafted) {
                addRecipe(findItemStack("X-Ray Scanner"),
                    "PGP",
                    "PCP",
                    "PBP",
                    'C', EAU.dictAdvancedChip,
                    'B', findItemStack("Portable Battery"),
                    'P', findItemStack("Iron Cable"),
                    'G', findItemStack("Ore Scanner"))
            }
        }

        private fun recipePortableCapacitor() {
            addRecipe(findItemStack("Portable Condensator"),
                " r ",
                "cDc",
                " r ",
                'r', ItemStack(Items.redstone),
                'c', findItemStack("Iron Cable"),
                'D', findItemStack("Dielectric"))
            addShapelessRecipe(findItemStack("Portable Condensator Pack"),
                findItemStack("Portable Condensator"),
                findItemStack("Portable Condensator"),
                findItemStack("Portable Condensator"),
                findItemStack("Portable Condensator"))
        }

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
                "   ",
                " Cc",
                "   ",
                'C', findItemStack("Combustion Chamber"),
                'c', findItemStack("Copper Thermal Cable"))
            addRecipe(findItemStack("Medium Fuel Burner"),
                "   ",
                " Cc",
                " C ",
                'C', findItemStack("Combustion Chamber"),
                'c', findItemStack("Copper Thermal Cable"))
            addRecipe(findItemStack("Big Fuel Burner"),
                "   ",
                "CCc",
                "CC ",
                'C', findItemStack("Combustion Chamber"),
                'c', findItemStack("Copper Thermal Cable"))
        }

        private fun recipeDisplays() {
            addRecipe(findItemStack("Digital Display", 1),
                "   ",
                "rrr",
                "iii",
                'r', ItemStack(Items.redstone),
                'i', findItemStack("Iron Cable")
            )
            addRecipe(findItemStack("Nixie Tube", 1),
                " g ",
                "grg",
                "iii",
                'g', ItemStack(Blocks.glass_pane),
                'r', ItemStack(Items.redstone),
                'i', findItemStack("Iron Cable")
            )
        }

        private fun recipeECoal() {
            addRecipe(findItemStack("E-Coal Helmet"),
                "PPP",
                "PCP",
                'P', "plateCoal",
                'C', findItemStack("Portable Condensator"))
            addRecipe(findItemStack("E-Coal Boots"),
                " C ",
                "P P",
                "P P",
                'P', "plateCoal",
                'C', findItemStack("Portable Condensator"))
            addRecipe(findItemStack("E-Coal Chestplate"),
                "P P",
                "PCP",
                "PPP",
                'P', "plateCoal",
                'C', findItemStack("Portable Condensator"))
            addRecipe(findItemStack("E-Coal Leggings"),
                "PPP",
                "PCP",
                "P P",
                'P', "plateCoal",
                'C', findItemStack("Portable Condensator"))
        }

        private fun recipeGridDevices() {
            var poleRecipes = 0
            for (oreName in arrayOf(
                "ingotAluminum",
                "ingotAluminium",
                "ingotSteel")) {
                if (EAU.oreNames.contains(oreName)) {
                    addRecipe(findItemStack("Utility Pole"),
                        "WWW",
                        "IWI",
                        " W ",
                        'W', "logWood",
                        'I', oreName
                    )
                    poleRecipes++
                }
            }
            if (poleRecipes == 0) {
                // Really?
                addRecipe(findItemStack("Utility Pole"),
                    "WWW",
                    "IWI",
                    " W ",
                    'I', "ingotIron",
                    'W', "logWood"
                )
            }
            addRecipe(findItemStack("Utility Pole w/DC-DC Converter"),
                "HHH",
                " TC",
                " PH",
                'P', findItemStack("Utility Pole"),
                'H', findItemStack("High Voltage Cable"),
                'C', findItemStack("Optimal Ferromagnetic Core"),
                'T', findItemStack("DC-DC Converter")
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
                        "ii ",
                        "mi ",
                        " B ",
                        Character.valueOf('i'), ingotType,
                        Character.valueOf('B'), blockType,
                        Character.valueOf('m'), findItemStack("Machine Block"))
                    addRecipe(findItemStack("Grid DC-DC Converter"),
                        "i i",
                        "mtm",
                        "imi",
                        Character.valueOf('i'), ingotType,
                        Character.valueOf('t'), findItemStack("DC-DC Converter"),
                        Character.valueOf('m'), findItemStack("Advanced Machine Block"))
                }
            }
        }

         */
    }
}
