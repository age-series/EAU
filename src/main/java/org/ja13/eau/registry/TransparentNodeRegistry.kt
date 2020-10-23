package org.ja13.eau.registry

import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import org.ja13.eau.EAU
import org.ja13.eau.EAU.obj
import org.ja13.eau.gridnode.electricalpole.ElectricalPoleDescriptor
import org.ja13.eau.gridnode.transformer.GridTransformerDescriptor
import org.ja13.eau.mechanical.ClutchDescriptor
import org.ja13.eau.mechanical.FixedShaftDescriptor
import org.ja13.eau.mechanical.FlywheelDescriptor
import org.ja13.eau.mechanical.GasTurbineDescriptor
import org.ja13.eau.mechanical.GeneratorDescriptor
import org.ja13.eau.mechanical.MotorDescriptor
import org.ja13.eau.mechanical.RotaryMotorDescriptor
import org.ja13.eau.mechanical.SteamTurbineDescriptor
import org.ja13.eau.mechanical.StraightJointDescriptor
import org.ja13.eau.mechanical.TachometerDescriptor
import org.ja13.eau.mechanical.VerticalHubDescriptor
import org.ja13.eau.misc.Coordonate
import org.ja13.eau.misc.FunctionTable
import org.ja13.eau.misc.FunctionTableYProtect
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.transparentnode.ArcFurnaceDescriptor
import org.ja13.eau.transparentnode.FuelGeneratorDescriptor
import org.ja13.eau.transparentnode.FuelHeatFurnaceDescriptor
import org.ja13.eau.transparentnode.LargeRheostatDescriptor
import org.ja13.eau.transparentnode.NixieTubeDescriptor
import org.ja13.eau.transparentnode.dcdc.DcDcDescriptor
import org.ja13.eau.transparentnode.festive.ChristmasTreeDescriptor
import org.ja13.eau.transparentnode.festive.HolidayCandleDescriptor
import org.ja13.eau.transparentnode.festive.StringLightsDescriptor
import org.ja13.eau.transparentnode.heatsink.FanHeatsinkDescriptor
import org.ja13.eau.transparentnode.heatsink.HeatsinkDescriptor
import org.ja13.eau.transparentnode.sterlingengine.SterlingEngineDescriptor
import org.ja13.eau.transparentnode.variabledcdc.VariableDcDcDescriptor

class TransparentNodeRegistry {
    companion object {

        enum class TNID (val id: Int) {
            TRANSFORMER(2),
            HEAT_FURNACE(3),
            TURBINE(4),
            ELECTRICAL_ANTENNA(5),
            BATTERY(6),
            ELECTRICAL_FURNACE(7),
            MACERATOR(8),
            ARC_FURNACE(9),
            COMPRESSOR(10),
            MAGNETIZER(11),
            PLATE_MACHINE(12),
            EGG_INCUBATOR(13),
            AUTO_MINER(14),
            SOLAR_PANEL(15),
            WIND_TURBINE(16),
            HEATSINK(17),
            MISC(18),
            TURRET(19),
            FUEL_GENERATOR(20),
            GRID_DEVICES(21),
            FESTIVE(22),
            NIXIE(23)
        }

        fun register() {
            registerTransformer(TNID.TRANSFORMER.id)
            registerHeatFurnace(TNID.HEAT_FURNACE.id)
            registerTurbine(TNID.TURBINE.id)
            registerElectricalAntenna(TNID.ELECTRICAL_ANTENNA.id)
            registerBattery(TNID.BATTERY.id)
            registerElectricalFurnace(TNID.ELECTRICAL_FURNACE.id)
            registerMacerator(TNID.MACERATOR.id)
            registerArcFurnace(TNID.ARC_FURNACE.id)
            registerCompressor(TNID.COMPRESSOR.id)
            registerMagnetizer(TNID.MAGNETIZER.id)
            registerPlateMachine(TNID.PLATE_MACHINE.id)
            registerEggIncubator(TNID.EGG_INCUBATOR.id)
            registerAutoMiner(TNID.AUTO_MINER.id)
            registerSolarPanel(TNID.SOLAR_PANEL.id)
            registerWindTurbine(TNID.WIND_TURBINE.id)
            registerHeatsinks(TNID.HEATSINK.id)
            registerTransparentNodeMisc(TNID.MISC.id)
            registerTurret(TNID.TURRET.id)
            registerFuelGenerator(TNID.FUEL_GENERATOR.id)
            registerGridDevices(TNID.GRID_DEVICES.id)
            //registerFloodlight(68);
            registerFestive(TNID.FESTIVE.id)
            registerNixieTube(TNID.NIXIE.id)
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun registerTransparentNode(group: Int, subId: Int, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor) {
            EAU.transparentNodeItem.addDescriptor(subId + (group shl 6), descriptor)
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun registerHiddenTransparentNode(group: Int, subId: Int, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor) {
            EAU.transparentNodeItem.addWithoutRegistry(subId + (group shl 6), descriptor)
        }

        /*
        private fun registerFloodlight(id: Int) {
            var name: String
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Basic Floodlight")
                val desc = BasicFloodlightDescriptor(name, obj.getObj("Floodlight"))
                registerTransparentNode(id, 0, desc)
            }
            run {
                name = I18N.TR_NAME(I18N.Type.NONE, "Motorized Floodlight")
                val desc = MotorizedFloodlightDescriptor(name, obj.getObj("FloodlightMotor"))
                registerTransparentNode(id, 1, desc)
            }
        }
         */

        private fun registerTransformer(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Variable DC-DC Converter")
                val desc = VariableDcDcDescriptor(name, obj.getObj("variabledcdc"),
                    obj.getObj("feromagneticcorea"), obj.getObj("transformatorCase"))
                registerTransparentNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "DC-DC Converter")
                val desc = DcDcDescriptor(name, obj.getObj("transformator"),
                    obj.getObj("feromagneticcorea"), obj.getObj("transformatorCase"), 0.5f)
                registerTransparentNode(id, 1, desc)
            }
        }

        private fun registerHeatFurnace(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Stone Heat Furnace")
                val desc = org.ja13.eau.transparentnode.heatfurnace.HeatFurnaceDescriptor(name,
                    "stonefurnace", 4000.0,
                    Utils.coalEnergyReference * 2 / 3,
                    8, 500.0,
                    org.ja13.eau.sim.ThermalLoadInitializerByPowerDrop(780.0, (-100).toDouble(), 10.0, 2.0)
                )
                registerTransparentNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Fuel Heat Furnace")
                val desc = FuelHeatFurnaceDescriptor(name,
                    obj.getObj("FuelHeater"), org.ja13.eau.sim.ThermalLoadInitializerByPowerDrop(780.0, (-100).toDouble(), 10.0, 2.0))
                registerTransparentNode(id, 1, desc)
            }
        }

        private fun registerTurbine(id: Int) {
            run {
                val desc = SteamTurbineDescriptor(
                    org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Steam Turbine"),
                    obj.getObj("Turbine")
                )
                registerTransparentNode(id, 2, desc)
            }
            run {
                val nominalRads = 200f
                val nominalU = 480f
                val nominalP = 4800f
                val desc = GeneratorDescriptor(
                    org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Generator"),
                    obj.getObj("Generator"),
                    nominalRads, nominalU,
                    50f,
                    nominalP,
                    EAU.sixNodeThermalLoadInitializer.copy()
                )
                registerTransparentNode(id, 3, desc)
            }
            run {
                val desc = GasTurbineDescriptor(
                    org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Gas Turbine"),
                    obj.getObj("GasTurbine")
                )
                registerTransparentNode(id, 4, desc)
            }
            run {
                val desc = StraightJointDescriptor(
                    org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Joint"),
                    obj.getObj("StraightJoint"))
                registerTransparentNode(id, 5, desc)
            }
            run {
                val desc = VerticalHubDescriptor(
                    org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Joint hub"),
                    obj.getObj("VerticalHub"))
                registerTransparentNode(id, 6, desc)
            }
            run {
                val desc = FlywheelDescriptor(
                    org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Flywheel"),
                    obj.getObj("Flywheel"))
                registerTransparentNode(id, 7, desc)
            }
            run {
                val desc = TachometerDescriptor(
                    org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Tachometer"),
                    obj.getObj("Tachometer"))
                registerTransparentNode(id, 8, desc)
            }
            run {
                val nominalRads = 200f
                val nominalU = 480f
                val nominalP = 4800f
                val desc = MotorDescriptor(
                    org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Shaft Motor"),
                    obj.getObj("Motor"),
                    EAU.mediumInsulationMediumCurrentCopperCable,
                    nominalRads,
                    nominalU,
                    nominalP,
                    300f,
                    EAU.sixNodeThermalLoadInitializer.copy()
                )
                registerTransparentNode(id, 9, desc)
                EAU.blockTabIcon = desc.newItemStack().item
            }
            run {
                val desc = ClutchDescriptor(
                    org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Clutch"),
                    obj.getObj("Clutch")
                )
                registerTransparentNode(id, 10, desc)
            }
            run {
                val desc = FixedShaftDescriptor(
                    org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Fixed Shaft"),
                    obj.getObj("FixedShaft")
                )
                registerTransparentNode(id, 11, desc)
            }
            run {
                val desc = RotaryMotorDescriptor(
                    org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Rotary Motor"),
                    obj.getObj("Starter_Motor")
                )
                val g = org.ja13.eau.ghost.GhostGroup()
                for (x in -1..1) {
                    for (y in -1..1) {
                        for (z in -1 downTo -3 + 1) {
                            g.addElement(x, y, z)
                        }
                    }
                }
                g.removeElement(0, 0, 0)
                desc.setGhostGroup(g)
                registerTransparentNode(id, 12, desc)
            }
            run {
                val temperatureToVoltage = FunctionTable(doubleArrayOf(0.0, 0.1, 0.85,
                    1.0, 1.1, 1.15, 1.18, 1.19, 1.25), 8.0 / 5.0)
                val desc = SterlingEngineDescriptor("Sterling Engine", obj.getObj("StirlingEngine"), EAU.smallInsulationMediumCurrentRender, temperatureToVoltage)
                registerTransparentNode(id, 13, desc)
            }

        }

        private fun registerElectricalAntenna(id: Int) {
            var name: String
            run {
                val desc: org.ja13.eau.transparentnode.electricalantennatx.ElectricalAntennaTxDescriptor
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Low Power Transmitter Antenna")
                val power = 250.0
                desc = org.ja13.eau.transparentnode.electricalantennatx.ElectricalAntennaTxDescriptor(name,
                    obj.getObj("lowpowertransmitterantenna"), 200,
                    0.9, 0.7,
                    VoltageTier.LOW.voltage, power,
                    VoltageTier.LOW.voltage * 1.3, power * 1.3,
                    EAU.smallInsulationLowCurrentCopperCable)
                registerTransparentNode(id, 0, desc)
            }
            run {
                val desc: org.ja13.eau.transparentnode.electricalantennarx.ElectricalAntennaRxDescriptor
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Low Power Receiver Antenna")
                val power = 250.0
                desc = org.ja13.eau.transparentnode.electricalantennarx.ElectricalAntennaRxDescriptor(name,
                    obj.getObj("lowpowerreceiverantenna"), VoltageTier.LOW.voltage, power,
                    VoltageTier.LOW.voltage * 1.3, power * 1.3,
                    EAU.smallInsulationLowCurrentCopperCable)
                registerTransparentNode(id, 1, desc)
            }
            run {
                val desc: org.ja13.eau.transparentnode.electricalantennatx.ElectricalAntennaTxDescriptor
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Medium Power Transmitter Antenna")
                val power = 1000.0
                desc = org.ja13.eau.transparentnode.electricalantennatx.ElectricalAntennaTxDescriptor(name,
                    obj.getObj("lowpowertransmitterantenna"), 250,
                    0.9, 0.75,
                    VoltageTier.LOW_HOUSEHOLD.voltage, power,
                    VoltageTier.LOW_HOUSEHOLD.voltage * 1.3, power * 1.3,
                    EAU.smallInsulationMediumCurrentCopperCable)
                registerTransparentNode(id, 2, desc)
            }
            run {
                val desc: org.ja13.eau.transparentnode.electricalantennarx.ElectricalAntennaRxDescriptor
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Medium Power Receiver Antenna")
                val power = 1000.0
                desc = org.ja13.eau.transparentnode.electricalantennarx.ElectricalAntennaRxDescriptor(name,
                    obj.getObj("lowpowerreceiverantenna"), VoltageTier.LOW_HOUSEHOLD.voltage, power,
                    VoltageTier.LOW_HOUSEHOLD.voltage * 1.3, power * 1.3,
                    EAU.smallInsulationMediumCurrentCopperCable)
                registerTransparentNode(id, 3, desc)
            }
            run {
                val desc: org.ja13.eau.transparentnode.electricalantennatx.ElectricalAntennaTxDescriptor
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "High Power Transmitter Antenna")
                val power = 2000.0
                desc = org.ja13.eau.transparentnode.electricalantennatx.ElectricalAntennaTxDescriptor(name,
                    obj.getObj("lowpowertransmitterantenna"), 300,
                    0.95, 0.8,
                    VoltageTier.HIGH_HOUSEHOLD.voltage, power,
                    VoltageTier.HIGH_HOUSEHOLD.voltage * 1.3, power * 1.3,
                    EAU.smallInsulationHighCurrentCopperCable)
                registerTransparentNode(id, 4, desc)
            }
            run {
                val desc: org.ja13.eau.transparentnode.electricalantennarx.ElectricalAntennaRxDescriptor
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "High Power Receiver Antenna")
                val power = 2000.0
                desc = org.ja13.eau.transparentnode.electricalantennarx.ElectricalAntennaRxDescriptor(name,
                    obj.getObj("lowpowerreceiverantenna"), VoltageTier.HIGH_HOUSEHOLD.voltage, power,
                    VoltageTier.HIGH_HOUSEHOLD.voltage * 1.3, power * 1.3,
                    EAU.smallInsulationHighCurrentCopperCable)
                registerTransparentNode(id, 5, desc)
            }
        }

        private fun registerBattery(id: Int) {
            var name: String
            val heatTIme = 30.0
            val voltageFunctionTable = doubleArrayOf(0.000, 0.9, 1.0, 1.025, 1.04, 1.05,
                2.0)
            val voltageFunction = FunctionTable(voltageFunctionTable,
                6.0 / 5)
            Utils.printFunction(voltageFunction, -0.2, 1.2, 0.1)
            val stdDischargeTime = 60 * 8.toDouble()
            val stdU = VoltageTier.LOW.voltage
            val stdP = 2_000.0
            val stdEfficiency = 1.0 - 2.0 / 50.0
            EAU.batteryVoltageFunctionTable = voltageFunction
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Cost Oriented Battery")
                val desc = org.ja13.eau.transparentnode.battery.BatteryDescriptor(name, "BatteryBig", EAU.smallInsulationMediumCurrentCopperCable,
                    0.5,
                    true, true,
                    voltageFunction,
                    stdU,
                    stdP * 1.2,
                    0.00,
                    stdP,
                    stdDischargeTime * EAU.batteryCapacityFactor, stdEfficiency, EAU.stdBatteryHalfLife,
                    heatTIme, 60.0, (-100).toDouble(),
                    "Cheap battery"
                )
                desc.setRenderSpec("lowcost")
                desc.setCurrentDrop(desc.electricalU * 1.2, desc.electricalStdP * 1.0)
                registerTransparentNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Capacity Oriented Battery")
                val desc = org.ja13.eau.transparentnode.battery.BatteryDescriptor(name,
                    "BatteryBig", EAU.smallInsulationMediumCurrentCopperCable, 0.5, true, true, voltageFunction,
                    stdU / 4, stdP / 2 * 1.2, 0.000,
                    stdP / 2, stdDischargeTime * 8 * EAU.batteryCapacityFactor, stdEfficiency, EAU.stdBatteryHalfLife,
                    heatTIme, 60.0, (-100).toDouble(),
                    "the battery"
                )
                desc.setRenderSpec("capacity")
                desc.setCurrentDrop(desc.electricalU * 1.2, desc.electricalStdP * 1.0)
                registerTransparentNode(id, 1, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Voltage Oriented Battery")
                val desc = org.ja13.eau.transparentnode.battery.BatteryDescriptor(name,
                    "BatteryBig", EAU.smallInsulationMediumCurrentCopperCable, 0.5, true, true, voltageFunction, stdU * 4,
                    stdP * 1.2, 0.000,
                    stdP, stdDischargeTime * EAU.batteryCapacityFactor, stdEfficiency, EAU.stdBatteryHalfLife,
                    heatTIme, 60.0, (-100).toDouble(),
                    "the battery"
                )
                desc.setRenderSpec("highvoltage")
                desc.setCurrentDrop(desc.electricalU * 1.2, desc.electricalStdP * 1.0)
                registerTransparentNode(id, 2, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Current Oriented Battery")
                val desc = org.ja13.eau.transparentnode.battery.BatteryDescriptor(name,
                    "BatteryBig", EAU.smallInsulationMediumCurrentCopperCable, 0.5, true, true, voltageFunction, stdU,
                    stdP * 1.2 * 4, 0.000,
                    stdP * 4, stdDischargeTime / 6 * EAU.batteryCapacityFactor, stdEfficiency, EAU.stdBatteryHalfLife,
                    heatTIme, 60.0, (-100).toDouble(),
                    "the battery"
                )
                desc.setRenderSpec("current")
                desc.setCurrentDrop(desc.electricalU * 1.2, desc.electricalStdP * 1.0)
                registerTransparentNode(id, 3, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Life Oriented Battery")
                val desc = org.ja13.eau.transparentnode.battery.BatteryDescriptor(name,
                    "BatteryBig", EAU.smallInsulationMediumCurrentCopperCable, 0.5, true, false, voltageFunction, stdU,
                    stdP * 1.2, 0.000,
                    stdP, stdDischargeTime * EAU.batteryCapacityFactor, stdEfficiency, EAU.stdBatteryHalfLife * 8,
                    heatTIme, 60.0, (-100).toDouble(),
                    "the battery"
                )
                desc.setRenderSpec("life")
                desc.setCurrentDrop(desc.electricalU * 1.2, desc.electricalStdP * 1.0)
                registerTransparentNode(id, 4, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Single-use Battery")
                val desc = org.ja13.eau.transparentnode.battery.BatteryDescriptor(name,
                    "BatteryBig", EAU.smallInsulationMediumCurrentCopperCable, 1.0, false, false, voltageFunction, stdU,
                    stdP * 1.2 * 2, 0.000,
                    stdP * 2, stdDischargeTime / 4 * EAU.batteryCapacityFactor, stdEfficiency, EAU.stdBatteryHalfLife * 8,
                    heatTIme, 60.0, (-100).toDouble(),
                    "the battery"
                )
                desc.setRenderSpec("coal")
                registerTransparentNode(id, 5, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Experimental Battery")
                val desc = org.ja13.eau.transparentnode.battery.BatteryDescriptor(name,
                    "BatteryBig", EAU.smallInsulationMediumCurrentCopperCable, 0.5, true, false, voltageFunction, stdU * 2,
                    stdP * 1.2 * 8, 0.025,
                    stdP * 8, stdDischargeTime / 4 * EAU.batteryCapacityFactor, stdEfficiency, EAU.stdBatteryHalfLife * 8,
                    heatTIme, 60.0, (-100).toDouble(),
                    "You were unable to fix the power leaking problem, though."
                )
                desc.setRenderSpec("highvoltage")
                desc.setCurrentDrop(desc.electricalU * 1.2, desc.electricalStdP * 1.0)
                registerTransparentNode(id, 6, desc)
            }
        }

        private fun registerElectricalFurnace(id: Int) {
            var name: String
            EAU.furnaceList.add(ItemStack(Blocks.furnace))
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Furnace")
                val powerFromTemperatureTable = doubleArrayOf(0.0, 20.0, 40.0, 80.0, 160.0, 240.0, 360.0, 540.0, 756.0, 1058.4, 1481.76)
                val thermalPlostfTTable = DoubleArray(powerFromTemperatureTable.size)
                for (idx in thermalPlostfTTable.indices) {
                    thermalPlostfTTable[idx] = (powerFromTemperatureTable[idx]
                        * Math.pow((idx + 1.0) / thermalPlostfTTable.size, 2.0)
                        * 2)
                }
                val powerFromTemperature = FunctionTableYProtect(powerFromTemperatureTable,
                    800.0, 0.0, 100000.0)
                val thermalPlostfT = FunctionTableYProtect(
                    thermalPlostfTTable, 800.0, 0.001, 10000000.0)
                val desc = org.ja13.eau.transparentnode.electricalfurnace.ElectricalFurnaceDescriptor(
                    name, powerFromTemperature, thermalPlostfT,
                    40.0
                )
                EAU.electricalFurnace = desc
                registerTransparentNode(id, 0, desc)
                EAU.furnaceList.add(desc.newItemStack())
            }
        }

        private fun registerMacerator(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "12V Macerator")
                val desc = org.ja13.eau.transparentnode.electricalmachine.MaceratorDescriptor(name,
                    "maceratora", VoltageTier.LOW.voltage, 200.0,  // double nominalU,double nominalP,
                    VoltageTier.LOW.voltage * 1.25,  // double maximalU,
                    org.ja13.eau.sim.ThermalLoadInitializer(80.0, (-100).toDouble(), 10.0, 100000.0),  // thermal,
                    EAU.smallInsulationLowCurrentCopperCable,
                    EAU.maceratorRecipes)
                desc.setRunningSound("eln:macerator")
                registerTransparentNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "240V Macerator")
                val desc = org.ja13.eau.transparentnode.electricalmachine.MaceratorDescriptor(name,
                    "maceratorb", VoltageTier.LOW_HOUSEHOLD.voltage, 2000.0,  // double nominalU,double nominalP,
                    VoltageTier.LOW_HOUSEHOLD.voltage * 1.25,  // double maximalU,
                    org.ja13.eau.sim.ThermalLoadInitializer(80.0, (-100).toDouble(), 10.0, 100000.0),  // thermal,
                    EAU.smallInsulationMediumCurrentCopperCable,
                    // cable
                    EAU.maceratorRecipes)
                desc.setRunningSound("eln:macerator")
                registerTransparentNode(id, 1, desc)
            }
        }

        private fun registerArcFurnace(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "480V Arc Furnace")
                val desc = ArcFurnaceDescriptor(name, obj.getObj("arcfurnace"))
                registerTransparentNode(id, 0, desc)
            }
        }

        private fun registerCompressor(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "12V Compressor")
                val desc = org.ja13.eau.transparentnode.electricalmachine.CompressorDescriptor(
                    name,
                    obj.getObj("compressora"),
                    VoltageTier.LOW.voltage, 200.0,
                    VoltageTier.LOW.voltage * 1.25,
                    org.ja13.eau.sim.ThermalLoadInitializer(80.0, (-100).toDouble(), 10.0, 100000.0),
                    EAU.smallInsulationLowCurrentCopperCable,
                    EAU.compressorRecipes)
                desc.setRunningSound("eln:compressor_run")
                desc.setEndSound(org.ja13.eau.sound.SoundCommand("eln:compressor_end"))
                registerTransparentNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "240V Compressor")
                val desc = org.ja13.eau.transparentnode.electricalmachine.CompressorDescriptor(
                    name,
                    obj.getObj("compressorb"),
                    VoltageTier.LOW_HOUSEHOLD.voltage, 2000.0,
                    VoltageTier.LOW_HOUSEHOLD.voltage * 1.25,
                    org.ja13.eau.sim.ThermalLoadInitializer(80.0, (-100).toDouble(), 10.0, 100000.0),
                    EAU.smallInsulationMediumCurrentCopperCable,
                    EAU.compressorRecipes)
                desc.setRunningSound("eln:compressor_run")
                desc.setEndSound(org.ja13.eau.sound.SoundCommand("eln:compressor_end"))
                registerTransparentNode(id, 1, desc)
            }
        }

        private fun registerMagnetizer(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "12V Magnetizer")
                val desc = org.ja13.eau.transparentnode.electricalmachine.MagnetizerDescriptor(
                    name,
                    obj.getObj("magnetizera"),
                    VoltageTier.LOW.voltage, 200.0,
                    VoltageTier.LOW.voltage * 1.25,
                    org.ja13.eau.sim.ThermalLoadInitializer(80.0, (-100).toDouble(), 10.0, 100000.0),
                    EAU.smallInsulationLowCurrentCopperCable,
                    EAU.magnetiserRecipes)
                desc.setRunningSound("eln:Motor")
                registerTransparentNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "240V Magnetizer")
                val desc = org.ja13.eau.transparentnode.electricalmachine.MagnetizerDescriptor(
                    name,
                    obj.getObj("magnetizerb"),
                    VoltageTier.LOW_HOUSEHOLD.voltage, 2000.0,
                    VoltageTier.LOW_HOUSEHOLD.voltage * 1.25,
                    org.ja13.eau.sim.ThermalLoadInitializer(80.0, (-100).toDouble(), 10.0, 100000.0),
                    EAU.smallInsulationMediumCurrentCopperCable,
                    EAU.magnetiserRecipes)
                desc.setRunningSound("eln:Motor")
                registerTransparentNode(id, 1, desc)
            }
        }

        private fun registerPlateMachine(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "12V Plate Machine")
                val desc = org.ja13.eau.transparentnode.electricalmachine.PlateMachineDescriptor(
                    name,
                    obj.getObj("platemachinea"),
                    VoltageTier.LOW.voltage, 200.0,
                    VoltageTier.LOW.voltage * 1.25,
                    org.ja13.eau.sim.ThermalLoadInitializer(80.0, (-100).toDouble(), 10.0, 100000.0),
                    EAU.smallInsulationLowCurrentCopperCable,
                    EAU.plateMachineRecipes)
                desc.setRunningSound("eln:plate_machine")
                registerTransparentNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "240V Plate Machine")
                val desc = org.ja13.eau.transparentnode.electricalmachine.PlateMachineDescriptor(
                    name,
                    obj.getObj("platemachineb"),
                    VoltageTier.LOW_HOUSEHOLD.voltage, 2000.0,
                    VoltageTier.LOW_HOUSEHOLD.voltage * 1.25,
                    org.ja13.eau.sim.ThermalLoadInitializer(80.0, (-100).toDouble(), 10.0, 100000.0),
                    EAU.smallInsulationMediumCurrentCopperCable,
                    EAU.plateMachineRecipes)
                desc.setRunningSound("eln:plate_machine")
                registerTransparentNode(id, 1, desc)
            }
        }

        private fun registerEggIncubator(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "12V Egg Incubator")
                val desc = org.ja13.eau.transparentnode.eggincubator.EggIncubatorDescriptor(
                    name, obj.getObj("eggincubator"),
                    EAU.smallInsulationLowCurrentCopperCable,
                    VoltageTier.LOW.voltage, 12.0)
                registerTransparentNode(id, 0, desc)
            }
        }

        private fun registerAutoMiner(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Auto Miner")
                val powerLoad = arrayOfNulls<Coordonate>(2)
                powerLoad[0] = Coordonate(-2, -1, 1, 0)
                powerLoad[1] = Coordonate(-2, -1, -1, 0)
                val lightCoord = Coordonate(-3, 0, 0, 0)
                val miningCoord = Coordonate(-1, 0, 1, 0)
                val desc = org.ja13.eau.transparentnode.autominer.AutoMinerDescriptor(name,
                    obj.getObj("AutoMiner"),
                    powerLoad, lightCoord, miningCoord,
                    2, 1, 0,
                    EAU.smallInsulationMediumCurrentCopperCable,
                    1.0, 50.0
                )
                val ghostGroup = org.ja13.eau.ghost.GhostGroup()
                ghostGroup.addRectangle(-2, -1, -1, 0, -1, 1)
                ghostGroup.addRectangle(1, 1, -1, 0, 1, 1)
                ghostGroup.addRectangle(1, 1, -1, 0, -1, -1)
                ghostGroup.addElement(1, 0, 0)
                ghostGroup.addElement(0, 0, 1)
                ghostGroup.addElement(0, 1, 0)
                ghostGroup.addElement(0, 0, -1)
                ghostGroup.removeElement(-1, -1, 0)
                desc.setGhostGroup(ghostGroup)
                registerTransparentNode(id, 0, desc)
            }
        }

        private fun registerSolarPanel(id: Int) {
            var ghostGroup: org.ja13.eau.ghost.GhostGroup
            var name: String
            val solarVoltage = 59.0
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Small Solar Panel")
                ghostGroup = org.ja13.eau.ghost.GhostGroup()
                val desc = org.ja13.eau.transparentnode.solarpanel.SolarPanelDescriptor(name,
                    obj.getObj("smallsolarpannel"), null,
                    ghostGroup, 0, 1, 0,
                    null, solarVoltage / 4, 65.0 * EAU.solarPanelPowerFactor,
                    0.01,
                    Math.PI / 2, Math.PI / 2
                )
                registerTransparentNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Small Rotating Solar Panel")
                ghostGroup = org.ja13.eau.ghost.GhostGroup()
                val desc = org.ja13.eau.transparentnode.solarpanel.SolarPanelDescriptor(name,
                    obj.getObj("smallsolarpannelrot"), EAU.smallInsulationLowCurrentRender,
                    ghostGroup, 0, 1, 0,
                    null, solarVoltage / 4, EAU.solarPanelBasePower * EAU.solarPanelPowerFactor,
                    0.01,
                    Math.PI / 4, Math.PI / 4 * 3
                )
                registerTransparentNode(id, 1, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "2x3 Solar Panel")
                val groundCoordinate = Coordonate(1, 0, 0, 0)
                ghostGroup = org.ja13.eau.ghost.GhostGroup()
                ghostGroup.addRectangle(0, 1, 0, 0, -1, 1)
                ghostGroup.removeElement(0, 0, 0)
                val desc = org.ja13.eau.transparentnode.solarpanel.SolarPanelDescriptor(name,
                    obj.getObj("bigSolarPanel"), EAU.smallInsulationMediumCurrentRender,
                    ghostGroup, 1, 1, 0,
                    groundCoordinate,
                    solarVoltage * 2, EAU.solarPanelBasePower * EAU.solarPanelPowerFactor * 8,
                    0.01,
                    Math.PI / 2, Math.PI / 2
                )
                registerTransparentNode(id, 2, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "2x3 Rotating Solar Panel")
                val groundCoordinate = Coordonate(1, 0, 0, 0)
                ghostGroup = org.ja13.eau.ghost.GhostGroup()
                ghostGroup.addRectangle(0, 1, 0, 0, -1, 1)
                ghostGroup.removeElement(0, 0, 0)
                val desc = org.ja13.eau.transparentnode.solarpanel.SolarPanelDescriptor(name,
                    obj.getObj("bigSolarPanelrot"), EAU.smallInsulationMediumCurrentRender,
                    ghostGroup, 1, 1, 1,
                    groundCoordinate,
                    solarVoltage * 2, EAU.solarPanelBasePower * EAU.solarPanelPowerFactor * 8,
                    0.01,
                    Math.PI / 8 * 3, Math.PI / 8 * 5
                )
                registerTransparentNode(id, 3, desc)
            }
        }

        private fun registerWindTurbine(id: Int) {
            var name: String
            val powerFromWind = FunctionTable(doubleArrayOf(0.0, 0.1, 0.3, 0.5, 0.8, 1.0, 1.1, 1.15, 1.2),
                8.0 / 5.0)
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Wind Turbine")
                val desc = org.ja13.eau.transparentnode.windturbine.WindTurbineDescriptor(
                    name, obj.getObj("WindTurbineMini"),
                    EAU.smallInsulationLowCurrentCopperCable,
                    powerFromWind,
                    160 * EAU.windTurbinePowerFactor, 10.0,
                    VoltageTier.LOW.voltage * 1.18, 22.0,
                    3,
                    7, 2, 2,
                    2, 0.07,
                    "eln:WINDTURBINE_BIG_SF", 1f
                )
                val g = org.ja13.eau.ghost.GhostGroup()
                g.addElement(0, 1, 0)
                g.addElement(0, 2, -1)
                g.addElement(0, 2, 1)
                g.addElement(0, 3, -1)
                g.addElement(0, 3, 1)
                g.addRectangle(0, 0, 1, 3, 0, 0)
                desc.setGhostGroup(g)
                registerTransparentNode(id, 0, desc)
            }
        }

        private fun registerHeatsinks(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Heatsink")
                val desc = HeatsinkDescriptor(name, obj.getObj("passivethermaldissipatora"))
                registerTransparentNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Heatsink with 12V Fan")
                val desc = org.ja13.eau.transparentnode.thermaldissipatoractive.ThermalDissipatorActiveDescriptor(
                    name,
                    obj.getObj("activethermaldissipatora"),
                    VoltageTier.LOW.voltage, 50.0,
                    800.0,
                    EAU.smallInsulationLowCurrentCopperCable,
                    130.0, (-100).toDouble(),
                    200.0, 30.0,
                    10.0, 1.0
                )
                registerTransparentNode(id, 1, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Heatsink with 240V Fan")
                val desc = org.ja13.eau.transparentnode.thermaldissipatoractive.ThermalDissipatorActiveDescriptor(
                    name,
                    obj.getObj("200vactivethermaldissipatora"),
                    VoltageTier.LOW_HOUSEHOLD.voltage, 60.0,
                    1200.0,
                    EAU.smallInsulationMediumCurrentCopperCable,
                    130.0, (-100).toDouble(),
                    200.0, 30.0,
                    10.0, 1.0
                )
                registerTransparentNode(id, 2, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "1k Large Rheostat")
                val heatsink = HeatsinkDescriptor(name, obj.getObj("LargeRheostat"), 4000.0)
                val desc = LargeRheostatDescriptor(name, heatsink, EAU.mediumInsulationMediumCurrentCopperCable, 1_000.0)
                registerTransparentNode(id, 3, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "10k Large Rheostat")
                val heatsink = HeatsinkDescriptor(name, obj.getObj("LargeRheostat"), 4000.0)
                val desc = LargeRheostatDescriptor(name, heatsink, EAU.mediumInsulationMediumCurrentCopperCable, 10_000.0)
                registerTransparentNode(id, 4, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "New Heatsink with 12V Fan")
                val desc = FanHeatsinkDescriptor(name, obj.getObj("activethermaldissipatora"), 12.0)
                registerTransparentNode(id, 5, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "New Heatsink with 240V Fan")
                val desc = FanHeatsinkDescriptor(name, obj.getObj("200vactivethermaldissipatora"), 240.0)
                registerTransparentNode(id, 6, desc)
            }
        }

        private fun registerTransparentNodeMisc(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Experimental Transporter")
                val powerLoad = arrayOfNulls<Coordonate>(2)
                powerLoad[0] = Coordonate(-1, 0, 1, 0)
                powerLoad[1] = Coordonate(-1, 0, -1, 0)
                val doorOpen = org.ja13.eau.ghost.GhostGroup()
                doorOpen.addRectangle(-4, -3, 2, 2, 0, 0)
                val doorClose = org.ja13.eau.ghost.GhostGroup()
                doorClose.addRectangle(-2, -2, 0, 1, 0, 0)
                val desc = org.ja13.eau.transparentnode.teleporter.TeleporterDescriptor(
                    name, obj.getObj("Transporter"),
                    EAU.smallInsulationMediumCurrentCopperCable,
                    Coordonate(-1, 0, 0, 0), Coordonate(-1, 1, 0, 0),
                    2,
                    powerLoad,
                    doorOpen, doorClose
                )
                desc.setChargeSound("eln:transporter", 0.5f)
                val g = org.ja13.eau.ghost.GhostGroup()
                g.addRectangle(-2, 0, 0, 1, -1, -1)
                g.addRectangle(-2, 0, 0, 1, 1, 1)
                g.addRectangle(-4, -1, 2, 2, 0, 0)
                g.addElement(0, 1, 0)
                g.addElement(-1, 0, 0, EAU.ghostBlock, org.ja13.eau.ghost.GhostBlock.tFloor)
                g.addRectangle(-3, -3, 0, 1, -1, -1)
                g.addRectangle(-3, -3, 0, 1, 1, 1)
                desc.setGhostGroup(g)
                registerTransparentNode(id, 0, desc)
            }
        }

        private fun registerTurret(id: Int) {
            run {
                val name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Defense Turret")
                val desc = org.ja13.eau.transparentnode.turret.TurretDescriptor(name, "Turret")
                registerTransparentNode(id, 0, desc)
            }
        }

        private fun registerFuelGenerator(id: Int) {
            run {
                val desc = FuelGeneratorDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "120V Fuel Generator"), obj.getObj("FuelGenerator50V"),
                    120.0, EAU.fuelGeneratorPowerFactor * 1200, VoltageTier.LOW.voltage * 1.25, EAU.fuelGeneratorTankCapacity)
                registerTransparentNode(id, 0, desc)
            }
            run {
                val desc = FuelGeneratorDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "240V Fuel Generator"), obj.getObj("FuelGenerator200V"),
                    240.0, EAU.fuelGeneratorPowerFactor * 6000, VoltageTier.LOW_HOUSEHOLD.voltage * 1.25,
                    EAU.fuelGeneratorTankCapacity)
                registerTransparentNode(id, 1, desc)
            }
        }

        private fun registerGridDevices(id: Int) {
            run {
                val desc = GridTransformerDescriptor("Grid DC-DC Converter", obj.getObj("GridConverter"), "textures/wire.png", EAU.smallInsulationMediumCurrentCopperCable)
                val g = org.ja13.eau.ghost.GhostGroup()
                g.addElement(1, 0, 0)
                g.addElement(0, 0, -1)
                g.addElement(1, 0, -1)
                g.addElement(1, 1, 0)
                g.addElement(0, 1, 0)
                g.addElement(1, 1, -1)
                g.addElement(0, 1, -1)
                desc.setGhostGroup(g)
                registerTransparentNode(id, 0, desc)
            }
            run {
                val desc = ElectricalPoleDescriptor(
                    "Utility Pole",
                    obj.getObj("UtilityPole"),
                    "textures/wire.png",
                    EAU.uninsulatedMediumCurrentAluminumCable,
                    false,
                    40,
                    51200.0)
                val g = org.ja13.eau.ghost.GhostGroup()
                g.addElement(0, 1, 0)
                g.addElement(0, 2, 0)
                g.addElement(0, 3, 0)
                desc.setGhostGroup(g)
                registerTransparentNode(id, 1, desc)
            }
            run {
                val desc = ElectricalPoleDescriptor(
                    // TODO: This freaks out our texture list getter thing with the slash in it.
                    // textures/blocks/utilitypolew/dc-dcconverter.png
                    "Utility Pole w/DC-DC Converter",
                    obj.getObj("UtilityPole"),
                    "textures/wire.png",
                    EAU.uninsulatedMediumCurrentAluminumCable,
                    true,
                    40,
                    12800.0)
                val g = org.ja13.eau.ghost.GhostGroup()
                g.addElement(0, 1, 0)
                g.addElement(0, 2, 0)
                g.addElement(0, 3, 0)
                desc.setGhostGroup(g)
                registerTransparentNode(id, 2, desc)
            }
            run {
                val desc = ElectricalPoleDescriptor("Transmission Tower",
                    obj.getObj("TransmissionTower"),
                    "textures/wire.png",
                    EAU.uninsulatedMediumCurrentAluminumCable,
                    false,
                    96,
                    51200.0)
                val g = org.ja13.eau.ghost.GhostGroup()
                g.addRectangle(-1, 1, 0, 0, -1, 1)
                g.addRectangle(0, 0, 1, 8, 0, 0)
                g.removeElement(0, 0, 0)
                desc.setGhostGroup(g)
                registerTransparentNode(id, 3, desc)
            }
        }

        private fun registerFestive(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Christmas Tree")
                val desc = ChristmasTreeDescriptor(name, obj.getObj("Christmas_Tree"))
                if (EAU.enableFestivities) {
                    registerTransparentNode(id, 0, desc)
                } else {
                    registerHiddenTransparentNode(id, 0, desc)
                }
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Holiday Candle")
                val desc = HolidayCandleDescriptor(name, obj.getObj("Candle_Light"))
                if (EAU.enableFestivities) {
                    registerTransparentNode(id, 1, desc)
                } else {
                    registerHiddenTransparentNode(id, 1, desc)
                }
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "String Lights")
                val desc = StringLightsDescriptor(name, obj.getObj("Christmas_Lights"))
                if (EAU.enableFestivities) {
                    registerTransparentNode(id, 2, desc)
                } else {
                    registerHiddenTransparentNode(id, 2, desc)
                }
            }
        }

        private fun registerNixieTube(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Nixie Tube")
                val desc = NixieTubeDescriptor(
                    name,
                    obj.getObj("NixieTube")
                )
                registerTransparentNode(id, 0, desc)
            }
        }
    }
}
