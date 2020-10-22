package org.ja13.eau

import cpw.mods.fml.common.event.FMLPreInitializationEvent
import org.ja13.eau.misc.Utils
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.common.config.Property
import java.util.*

class ConfigHelper {
    companion object {
        fun loadConfiguration(event: FMLPreInitializationEvent) {
            val config = Configuration(event.suggestedConfigurationFile)
            config.load()
            EAU.modbusEnable = config.get("modbus", "enable", false).getBoolean(false)
            EAU.modbusPort = config.get("modbus", "port", 1502).getInt(1502)
            EAU.debugEnabled = config.get("debug", "enable", false).getBoolean(false)

            EAU.explosionEnable = config.get("gameplay", "explosion", true).getBoolean(true)

            //explosionEnable = false;


            //explosionEnable = false;
            EAU.versionCheckEnabled = config.get("general", "versionCheckEnable", true, "Enable version checker").getBoolean(true)
            EAU.analyticsEnabled = config.get("general", "analyticsEnable", true, "Enable Analytics").getBoolean(true)
            EAU.analyticsURL = config.get("general", "analyticsURL", "http://eau.ja13.org/eau_stats", "Set update checker URL").string
            EAU.analyticsPlayerUUIDOptIn = config.get("general", "analyticsPlayerOptIn", false, "Opt into sending player UUID when sending analytics (default DISABLED)").getBoolean(false)
            EAU.enableFestivities = config.get("general", "enableFestiveItems", true, "Set this to false to disable holiday themed items").boolean

            if (EAU.analyticsEnabled) {
                val p: Property = config.get("general", "playerUUID", "")
                if (p.string.length == 0) {
                    EAU.playerUUID = UUID.randomUUID().toString()
                    p.set(EAU.playerUUID)
                } else EAU.playerUUID = p.string
            }

            EAU.heatTurbinePowerFactor = config.get("balancing", "heatTurbinePowerFactor", 1).getDouble(1.0)
            EAU.solarPanelPowerFactor = config.get("balancing", "solarPanelPowerFactor", 1).getDouble(1.0)
            EAU.windTurbinePowerFactor = config.get("balancing", "windTurbinePowerFactor", 1).getDouble(1.0)
            EAU.fuelGeneratorPowerFactor = config.get("balancing", "fuelGeneratorPowerFactor", 1).getDouble(1.0)
            EAU.fuelHeatFurnacePowerFactor = config.get("balancing", "fuelHeatFurnacePowerFactor", 1.0).double
            EAU.autominerRange = config.get("balancing", "autominerRange", 10, "Maximum horizontal distance from autominer that will be mined").getInt(10)

            Other.elnToIc2ConversionRatio = config.get("balancing", "ElnToIndustrialCraftConversionRatio", 1.0 / 3.0).getDouble(1.0 / 3.0)
            Other.elnToOcConversionRatio = config.get("balancing", "ElnToOpenComputerConversionRatio", 1.0 / 3.0 / 2.5).getDouble(1.0 / 3.0 / 2.5)
            Other.elnToTeConversionRatio = config.get("balancing", "ElnToThermalExpansionConversionRatio", 1.0 / 3.0 * 4).getDouble(1.0 / 3.0 * 4)
            EAU.plateConversionRatio = config.get("balancing", "platesPerIngot", 1).getInt(1)
            EAU.shaftEnergyFactor = config.get("balancing", "shaftEnergyFactor", 0.05).getDouble(0.05)

            EAU.stdBatteryHalfLife = config.get("battery", "batteryHalfLife", 2, "How many days it takes for a battery to decay half way").getDouble(2.0) * Utils.minecraftDay
            EAU.batteryCapacityFactor = config.get("balancing", "batteryCapacityFactor", 1.0).getDouble(1.0)

            EAU.ComputerProbeEnable = config.get("compatibility", "ComputerProbeEnable", true).getBoolean(true)
            EAU.ElnToOtherEnergyConverterEnable = config.get("compatibility", "ElnToOtherEnergyConverterEnable", true).getBoolean(true)

            EAU.killMonstersAroundLamps = config.get("entity", "killMonstersAroundLamps", true).getBoolean(true)
            EAU.killMonstersAroundLampsRange = config.get("entity", "killMonstersAroundLampsRange", 9).getInt(9)

            EAU.forceOreRegen = config.get("mapGenerate", "forceOreRegen", false).getBoolean(false)
            EAU.genCopper = config.get("mapGenerate", "copper", true).getBoolean(true)
            EAU.genLead = config.get("mapGenerate", "lead", true).getBoolean(true)
            EAU.genTungsten = config.get("mapGenerate", "tungsten", true).getBoolean(true)
            EAU.oredictTungsten = config.get("dictionary", "tungsten", false).getBoolean(false)
            if (EAU.oredictTungsten) {
                EAU.dictTungstenOre = "oreTungsten"
                EAU.dictTungstenDust = "dustTungsten"
                EAU.dictTungstenIngot = "ingotTungsten"
            } else {
                EAU.dictTungstenOre = "oreElnTungsten"
                EAU.dictTungstenDust = "dustElnTungsten"
                EAU.dictTungstenIngot = "ingotElnTungsten"
            }
            EAU.oredictChips = config.get("dictionary", "chips", true).getBoolean(true)
            if (EAU.oredictChips) {
                EAU.dictCheapChip = "circuitBasic"
                EAU.dictAdvancedChip = "circuitAdvanced"
            } else {
                EAU.dictCheapChip = "circuitElnBasic"
                EAU.dictAdvancedChip = "circuitElnAdvanced"
            }

            EAU.incandescentLampLife = config.get("lamp", "incandescentLifeInHours", 16.0).getDouble(16.0)
            EAU.economicLampLife = config.get("lamp", "economicLifeInHours", 64.0).getDouble(64.0)
            EAU.carbonLampLife = config.get("lamp", "carbonLifeInHours", 6.0).getDouble(6.0)
            EAU.ledLampLife = config.get("lamp", "ledLifeInHours", 512.0).getDouble(512.0)
            EAU.ledLampInfiniteLife = config.get("lamp", "infiniteLedLife", false).boolean
            EAU.allowSwingingLamps = config.get("lamp", "swingingLamps", true).boolean

            EAU.fuelGeneratorTankCapacity = config.get("fuelGenerator",
                "tankCapacityInSecondsAtNominalPower", 20 * 60).getDouble(20 * 60.toDouble())

            EAU.addOtherModOreToXRay = config.get("xrayscannerconfig", "addOtherModOreToXRay", true).getBoolean(true)
            EAU.xRayScannerRange = config.get("xrayscannerconfig", "rangeInBloc", 5.0).getDouble(5.0)
            EAU.xRayScannerRange = Math.max(Math.min(EAU.xRayScannerRange, 10.0), 4.0)
            EAU.xRayScannerCanBeCrafted = config.get("xrayscannerconfig", "canBeCrafted", true).getBoolean(true)

            EAU.electricalFrequency = config.get("simulation", "electricalFrequency", 20).getDouble(20.0)
            EAU.electricalInterSystemOverSampling = config.get("simulation", "electricalInterSystemOverSampling", 50).getInt(50)
            EAU.thermalFrequency = config.get("simulation", "thermalFrequency", 400).getDouble(400.0)

            EAU.wirelessTxRange = config.get("wireless", "txRange", 32).int

            EAU.wailaEasyMode = config.get("balancing", "wailaEasyMode", false, "Display more detailed WAILA info on some machines").getBoolean(false)
            EAU.cablePowerFactor = config.get("balancing", "cablePowerFactor", 1.0, "Multiplication factor for cable power capacity. We recommend 2.0 to 4.0 for larger modpacks, but 1.0 for Eln standalone, or if you like a challenge.", 0.5, 4.0).getDouble(1.0)

            EAU.fuelHeatValueFactor = config.get("balancing", "fuelHeatValueFactor", 0.0000675,
                "Factor to apply when converting real word heat values to Minecraft heat values (1mB = 1l).").double

            EAU.noSymbols = config.get("general", "noSymbols", false).boolean
            EAU.noVoltageBackground = config.get("general", "noVoltageBackground", false).boolean

            EAU.maxSoundDistance = config.get("debug", "maxSoundDistance", 16.0).double
            EAU.cableConnectionNodes = config.get("general", "cableNodes", false).boolean
            config.save()
        }
    }
}
