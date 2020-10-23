package org.ja13.eau.registry

import org.ja13.eau.EAU
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.FunctionTableYProtect
import org.ja13.eau.misc.IFunction
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.misc.series.SeriesMap
import org.ja13.eau.node.six.SixNodeDescriptor
import org.ja13.eau.sixnode.Amplifier
import org.ja13.eau.sixnode.AmplifierElement
import org.ja13.eau.sixnode.AmplifierRender
import org.ja13.eau.sixnode.AnalogChipDescriptor
import org.ja13.eau.sixnode.ElectricalFuseHolderDescriptor
import org.ja13.eau.sixnode.EmergencyLampDescriptor
import org.ja13.eau.sixnode.Filter
import org.ja13.eau.sixnode.FilterElement
import org.ja13.eau.sixnode.FilterRender
import org.ja13.eau.sixnode.OpAmp
import org.ja13.eau.sixnode.PIDRegulator
import org.ja13.eau.sixnode.PIDRegulatorElement
import org.ja13.eau.sixnode.PIDRegulatorRender
import org.ja13.eau.sixnode.PortableNaNDescriptor
import org.ja13.eau.sixnode.SampleAndHold
import org.ja13.eau.sixnode.ScannerDescriptor
import org.ja13.eau.sixnode.SummingUnit
import org.ja13.eau.sixnode.SummingUnitElement
import org.ja13.eau.sixnode.SummingUnitRender
import org.ja13.eau.sixnode.TreeResinCollector.TreeResinCollectorDescriptor
import org.ja13.eau.sixnode.VoltageControlledAmplifier
import org.ja13.eau.sixnode.VoltageControlledSawtoothOscillator
import org.ja13.eau.sixnode.VoltageControlledSineOscillator
import org.ja13.eau.sixnode.batterycharger.BatteryChargerDescriptor
import org.ja13.eau.sixnode.diode.DiodeDescriptor
import org.ja13.eau.sixnode.electricalalarm.ElectricalAlarmDescriptor
import org.ja13.eau.sixnode.electricalbreaker.ElectricalBreakerDescriptor
import org.ja13.eau.sixnode.electricaldatalogger.ElectricalDataLoggerDescriptor
import org.ja13.eau.sixnode.electricaldigitaldisplay.ElectricalDigitalDisplayDescriptor
import org.ja13.eau.sixnode.electricalentitysensor.ElectricalEntitySensorDescriptor
import org.ja13.eau.sixnode.electricalfiredetector.ElectricalFireDetectorDescriptor
import org.ja13.eau.sixnode.electricalgatesource.ElectricalGateSourceDescriptor
import org.ja13.eau.sixnode.electricalgatesource.ElectricalGateSourceRenderObj
import org.ja13.eau.sixnode.electricallightsensor.ElectricalLightSensorDescriptor
import org.ja13.eau.sixnode.electricalmath.ElectricalMathDescriptor
import org.ja13.eau.sixnode.electricalredstoneinput.ElectricalRedstoneInputDescriptor
import org.ja13.eau.sixnode.electricalredstoneoutput.ElectricalRedstoneOutputDescriptor
import org.ja13.eau.sixnode.electricalrelay.ElectricRelayDescriptor
import org.ja13.eau.sixnode.electricalsensor.ElectricalSensorDescriptor
import org.ja13.eau.sixnode.electricalsource.ElectricalSourceDescriptor
import org.ja13.eau.sixnode.electricalswitch.ElectricalSwitchDescriptor
import org.ja13.eau.sixnode.electricaltimeout.ElectricalTimeoutDescriptor
import org.ja13.eau.sixnode.electricalvumeter.ElectricalVuMeterDescriptor
import org.ja13.eau.sixnode.electricalwatch.ElectricalWatchDescriptor
import org.ja13.eau.sixnode.electricalweathersensor.ElectricalWeatherSensorDescriptor
import org.ja13.eau.sixnode.electricalwindsensor.ElectricalWindSensorDescriptor
import org.ja13.eau.sixnode.electriccable.ElectricCableDescriptor
import org.ja13.eau.sixnode.energymeter.EnergyMeterDescriptor
import org.ja13.eau.sixnode.groundcable.GroundCableDescriptor
import org.ja13.eau.sixnode.hub.HubDescriptor
import org.ja13.eau.sixnode.lampsocket.LampSocketDescriptor
import org.ja13.eau.sixnode.lampsocket.LampSocketStandardObjRender
import org.ja13.eau.sixnode.lampsocket.LampSocketSuspendedObjRender
import org.ja13.eau.sixnode.lampsocket.LampSocketType
import org.ja13.eau.sixnode.lampsupply.LampSupplyDescriptor
import org.ja13.eau.sixnode.logicgate.And
import org.ja13.eau.sixnode.logicgate.DFlipFlop
import org.ja13.eau.sixnode.logicgate.JKFlipFlop
import org.ja13.eau.sixnode.logicgate.LogicGateDescriptor
import org.ja13.eau.sixnode.logicgate.Nand
import org.ja13.eau.sixnode.logicgate.Nor
import org.ja13.eau.sixnode.logicgate.Not
import org.ja13.eau.sixnode.logicgate.Or
import org.ja13.eau.sixnode.logicgate.Oscillator
import org.ja13.eau.sixnode.logicgate.PalDescriptor
import org.ja13.eau.sixnode.logicgate.SchmittTrigger
import org.ja13.eau.sixnode.logicgate.XNor
import org.ja13.eau.sixnode.logicgate.Xor
import org.ja13.eau.sixnode.modbusrtu.ModbusRtuDescriptor
import org.ja13.eau.sixnode.powercapacitorsix.PowerCapacitorSixDescriptor
import org.ja13.eau.sixnode.powerinductorsix.PowerInductorSixDescriptor
import org.ja13.eau.sixnode.resistor.ResistorDescriptor
import org.ja13.eau.sixnode.thermalcable.ThermalCableDescriptor
import org.ja13.eau.sixnode.thermalsensor.ThermalSensorDescriptor
import org.ja13.eau.sixnode.tutorialsign.TutorialSignDescriptor
import org.ja13.eau.sixnode.wirelesssignal.repeater.WirelessSignalRepeaterDescriptor
import org.ja13.eau.sixnode.wirelesssignal.rx.WirelessSignalRxDescriptor
import org.ja13.eau.sixnode.wirelesssignal.source.WirelessSignalSourceDescriptor
import org.ja13.eau.sixnode.wirelesssignal.tx.WirelessSignalTxDescriptor

class SixNodeRegistry {
    companion object {

        enum class SNID (val id: Int) {
            GROUND(1),
            ELECTRICAL_SOURCE(2),
            ELECTRICAL_CABLE(3),
            THERMAL_CABLE(4),
            LAMP_SOCKET(5),
            LAMP_SUPPLY(6),
            BATTERY_CHARGER(7),
            WIRELESS_SIGNAL(8),
            ELECTRICAL_DATALOGGER(9),
            ELECTRICAL_RELAY(10),
            ELECTRICAL_GATE_SOURCE(11),
            PASSIVE_COMPONENT(12),
            SWITCH(13),
            CIRCUIT_BREAKER(14),
            ELECTRICAL_SENSOR(15),
            THERMAL_SENSOR(16),
            ELECTRICAL_VU_METER(17),
            ELECTRICAL_ALARM(18),
            ENVIRONMENTAL_SENSOR(19),
            ELECTRICAL_REDSTONE(20),
            ELECTRICAL_GATE(21),
            TREE_RESIN_COLLECTOR(22),
            MISC(23),
            LOGIC_GATES(24),
            ANALOG_CHIPS(25),
            DEV_TRASH(26)
        }

        fun register() {
            registerGround(SNID.GROUND.id)
            registerElectricalSource(SNID.ELECTRICAL_SOURCE.id)
            registerElectricalCable(SNID.ELECTRICAL_CABLE.id)
            registerThermalCable(SNID.THERMAL_CABLE.id)
            registerLampSocket(SNID.LAMP_SOCKET.id)
            registerLampSupply(SNID.LAMP_SUPPLY.id)
            registerBatteryCharger(SNID.BATTERY_CHARGER.id)
            registerWirelessSignal(SNID.WIRELESS_SIGNAL.id)
            registerElectricalDataLogger(SNID.ELECTRICAL_DATALOGGER.id)
            registerElectricalRelay(SNID.ELECTRICAL_RELAY.id)
            registerElectricalGateSource(SNID.ELECTRICAL_GATE_SOURCE.id)
            registerPassiveComponent(SNID.PASSIVE_COMPONENT.id)
            registerSwitch(SNID.SWITCH.id)
            registerElectricalManager(SNID.CIRCUIT_BREAKER.id)
            registerElectricalSensor(SNID.ELECTRICAL_SENSOR.id)
            registerThermalSensor(SNID.THERMAL_SENSOR.id)
            registerElectricalVuMeter(SNID.ELECTRICAL_VU_METER.id)
            registerElectricalAlarm(SNID.ELECTRICAL_ALARM.id)
            registerElectricalEnvironmentalSensor(SNID.ENVIRONMENTAL_SENSOR.id)
            registerElectricalRedstone(SNID.ELECTRICAL_REDSTONE.id)
            registerElectricalGate(SNID.ELECTRICAL_GATE.id)
            registerTreeResinCollector(SNID.TREE_RESIN_COLLECTOR.id)
            registerSixNodeMisc(SNID.MISC.id)
            registerLogicalGates(SNID.LOGIC_GATES.id)
            registerAnalogChips(SNID.ANALOG_CHIPS.id)
            registerDevStuff(SNID.DEV_TRASH.id)
        }

        val registeredList = mutableMapOf<Int, String>()

        @Suppress("MemberVisibilityCanBePrivate")
        fun registerSixNode(group: Int, subId: Int, descriptor: SixNodeDescriptor) {
            //println("Registering SixNode: ${descriptor.name}")
            val full = subId + (group shl 6)
            if (registeredList[full] != null) {
                println("${descriptor.name} tried to register on top of ${registeredList[full]}!")
            }
            registeredList[full] = descriptor.name
            EAU.sixNodeItem.addDescriptor(subId + (group shl 6), descriptor)
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun registerHiddenSixNode(group: Int, subId: Int, descriptor: SixNodeDescriptor) {
            //println("Shadow registering SixNode: ${descriptor.name}")
            val full = subId + (group shl 6)
            if (registeredList[full] != null) {
                println("${descriptor.name} tried to register on top of ${registeredList[full]}!")
            }
            registeredList[full] = descriptor.name
            EAU.sixNodeItem.addWithoutRegistry(subId + (group shl 6), descriptor)
        }

        private fun registerGround(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Ground Cable")
                val desc = GroundCableDescriptor(name, EAU.obj.getObj("groundcable"))
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Hub")
                val desc = HubDescriptor(name, EAU.obj.getObj("hub"))
                registerSixNode(id, 1, desc)
            }
        }

        private fun registerElectricalSource(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Source")
                val desc = ElectricalSourceDescriptor(
                    name, EAU.obj.getObj("voltagesource"), false)
                registerSixNode(id, 0, desc)
            }
        }

        private fun registerElectricalCable(id: Int) {

            fun createCableRenderDescriptor(size: Double): org.ja13.eau.cable.CableRenderDescriptor {
                return org.ja13.eau.cable.CableRenderDescriptor("eau", "sprites/cable.png", (0.5 * 1.5 * size).toFloat(), (0.5 * size).toFloat())
            }

            EAU.uninsulatedLowCurrentRender = createCableRenderDescriptor(1.0)
            EAU.uninsulatedMediumCurrentRender = createCableRenderDescriptor(2.0)
            EAU.uninsulatedHighCurrentRender = createCableRenderDescriptor(3.0)
            EAU.smallInsulationLowCurrentRender = createCableRenderDescriptor(1.5)
            EAU.smallInsulationMediumCurrentRender = createCableRenderDescriptor(2.5)
            EAU.smallInsulationHighCurrentRender = createCableRenderDescriptor(3.5)
            EAU.mediumInsulationLowCurrentRender = createCableRenderDescriptor(2.0)
            EAU.mediumInsulationMediumCurrentRender = createCableRenderDescriptor(3.0)
            EAU.bigInsulationLowCurrentRender = createCableRenderDescriptor(3.0)

            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Low Current Copper Uninsulated Cable")
                EAU.uninsulatedLowCurrentCopperCable = ElectricCableDescriptor(name, EAU.uninsulatedLowCurrentRender)
                EAU.uninsulatedLowCurrentCopperCable.insulationVoltage = 0.0
                registerSixNode(id, 0, EAU.uninsulatedLowCurrentCopperCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Medium Current Copper Uninsulated Cable")
                EAU.uninsulatedMediumCurrentCopperCable = ElectricCableDescriptor(name, EAU.uninsulatedMediumCurrentRender)
                EAU.uninsulatedMediumCurrentCopperCable.insulationVoltage = 0.0
                registerSixNode(id, 1, EAU.uninsulatedMediumCurrentCopperCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "High Current Copper Uninsulated Cable")
                EAU.uninsulatedHighCurrentCopperCable = ElectricCableDescriptor(name, EAU.uninsulatedHighCurrentRender)
                EAU.uninsulatedHighCurrentCopperCable.insulationVoltage = 0.0
                registerSixNode(id, 2, EAU.uninsulatedHighCurrentCopperCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Low Current Copper 300V Insulated Cable")
                EAU.smallInsulationLowCurrentCopperCable = ElectricCableDescriptor(name, EAU.smallInsulationLowCurrentRender)
                EAU.smallInsulationLowCurrentCopperCable.insulationVoltage = 300.0
                registerSixNode(id, 3, EAU.smallInsulationLowCurrentCopperCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Medium Current Copper 300V Insulated Cable")
                EAU.smallInsulationMediumCurrentCopperCable = ElectricCableDescriptor(name, EAU.smallInsulationMediumCurrentRender)
                EAU.smallInsulationMediumCurrentCopperCable.insulationVoltage = 300.0
                registerSixNode(id, 4, EAU.smallInsulationMediumCurrentCopperCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "High Current Copper 300V Insulated Cable")
                EAU.smallInsulationHighCurrentCopperCable = ElectricCableDescriptor(name, EAU.smallInsulationHighCurrentRender)
                EAU.smallInsulationHighCurrentCopperCable.insulationVoltage = 300.0
                registerSixNode(id, 5, EAU.smallInsulationHighCurrentCopperCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Low Current Copper 1kV Insulated Cable")
                EAU.mediumInsulationLowCurrentCopperCable = ElectricCableDescriptor(name, EAU.mediumInsulationLowCurrentRender)
                EAU.mediumInsulationLowCurrentCopperCable.insulationVoltage = 1_000.0
                registerSixNode(id, 6, EAU.mediumInsulationLowCurrentCopperCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Medium Current Copper 1kV Insulated Cable")
                EAU.mediumInsulationMediumCurrentCopperCable = ElectricCableDescriptor(name, EAU.mediumInsulationMediumCurrentRender)
                EAU.mediumInsulationMediumCurrentCopperCable.insulationVoltage = 1_000.0
                registerSixNode(id, 7, EAU.mediumInsulationMediumCurrentCopperCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Low Current Copper 20kV Insulated Cable")
                EAU.bigInsulationLowCurrentCopperCable = ElectricCableDescriptor(name, EAU.bigInsulationLowCurrentRender)
                EAU.bigInsulationLowCurrentCopperCable.insulationVoltage = 20_000.0
                registerSixNode(id, 8, EAU.bigInsulationLowCurrentCopperCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Low Current Aluminum Uninsulated Cable")
                EAU.uninsulatedLowCurrentAluminumCable = ElectricCableDescriptor(name, EAU.uninsulatedLowCurrentRender, material = "aluminum")
                EAU.uninsulatedLowCurrentAluminumCable.insulationVoltage = 0.0
                registerSixNode(id, 9, EAU.uninsulatedLowCurrentAluminumCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Medium Current Aluminum Uninsulated Cable")
                EAU.uninsulatedMediumCurrentAluminumCable = ElectricCableDescriptor(name, EAU.uninsulatedMediumCurrentRender, material = "aluminum")
                EAU.uninsulatedMediumCurrentAluminumCable.insulationVoltage = 0.0
                registerSixNode(id, 10, EAU.uninsulatedMediumCurrentAluminumCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "High Current Aluminum Uninsulated Cable")
                EAU.uninsulatedHighCurrentAluminumCable = ElectricCableDescriptor(name, EAU.uninsulatedHighCurrentRender, material = "aluminum")
                EAU.uninsulatedHighCurrentAluminumCable.insulationVoltage = 0.0
                registerSixNode(id, 11, EAU.uninsulatedHighCurrentAluminumCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Low Current Aluminum 300V Insulated Cable")
                EAU.smallInsulationLowCurrentAluminumCable = ElectricCableDescriptor(name, EAU.smallInsulationLowCurrentRender, material = "aluminum")
                EAU.smallInsulationLowCurrentAluminumCable.insulationVoltage = 300.0
                registerSixNode(id, 12, EAU.smallInsulationLowCurrentAluminumCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Medium Current Aluminum 300V Insulated Cable")
                EAU.smallInsulationMediumCurrentAluminumCable = ElectricCableDescriptor(name, EAU.smallInsulationMediumCurrentRender, material = "aluminum")
                EAU.smallInsulationMediumCurrentAluminumCable.insulationVoltage = 300.0
                registerSixNode(id, 13, EAU.smallInsulationMediumCurrentAluminumCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "High Current Aluminum 300V Insulated Cable")
                EAU.smallInsulationHighCurrentAluminumCable = ElectricCableDescriptor(name, EAU.smallInsulationHighCurrentRender, material = "aluminum")
                EAU.smallInsulationHighCurrentAluminumCable.insulationVoltage = 300.0
                registerSixNode(id, 14, EAU.smallInsulationHighCurrentAluminumCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Low Current Aluminum 1kV Insulated Cable")
                EAU.mediumInsulationLowCurrentAluminumCable = ElectricCableDescriptor(name, EAU.mediumInsulationLowCurrentRender, material = "aluminum")
                EAU.mediumInsulationLowCurrentAluminumCable.insulationVoltage = 1_000.0
                registerSixNode(id, 15, EAU.mediumInsulationLowCurrentAluminumCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Medium Current Aluminum 1kV Insulated Cable")
                EAU.mediumInsulationMediumCurrentAluminumCable= ElectricCableDescriptor(name, EAU.mediumInsulationMediumCurrentRender, material = "aluminum")
                EAU.mediumInsulationMediumCurrentAluminumCable.insulationVoltage = 1_000.0
                registerSixNode(id, 16, EAU.mediumInsulationMediumCurrentAluminumCable)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Low Current Aluminum 20kV Insulated Cable")
                EAU.bigInsulationLowCurrentAluminumCable = ElectricCableDescriptor(name, EAU.bigInsulationLowCurrentRender, material = "aluminum")
                EAU.bigInsulationLowCurrentAluminumCable.insulationVoltage = 20_000.0
                registerSixNode(id, 17, EAU.bigInsulationLowCurrentAluminumCable)
            }
        }

        private fun registerThermalCable(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Copper Thermal Cable")
                val desc = ThermalCableDescriptor(name,
                    (1000 - 20).toDouble(), (-200).toDouble(),
                    500.0, 2000.0,
                    2.0, 10.0, 0.1,
                    org.ja13.eau.cable.CableRenderDescriptor("eau",
                        "sprites/tex_thermalcablebase.png", 4f, 4f),
                    "Miaou !")
                registerSixNode(id, 1, desc)
            }
        }

        private fun registerLampSocket(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Lamp Socket A")
                val desc = LampSocketDescriptor(name, LampSocketStandardObjRender(EAU.obj.getObj("ClassicLampSocket"), false),
                    LampSocketType.Douille,
                    false,
                    4, 0f, 0f, 0f)
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Lamp Socket B Projector")
                val desc = LampSocketDescriptor(name, LampSocketStandardObjRender(EAU.obj.getObj("ClassicLampSocket"), false),
                    LampSocketType.Douille,
                    false,
                    10, (-90).toFloat(), 90f, 0f)
                registerSixNode(id, 1, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Robust Lamp Socket")
                val desc = LampSocketDescriptor(name, LampSocketStandardObjRender(EAU.obj.getObj("RobustLamp"), true),
                    LampSocketType.Douille,
                    false,
                    3, 0f, 0f, 0f)
                desc.setInitialOrientation(-90f)
                registerSixNode(id, 2, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Flat Lamp Socket")
                val desc = LampSocketDescriptor(name, LampSocketStandardObjRender(EAU.obj.getObj("FlatLamp"), true),
                    LampSocketType.Douille,
                    false,
                    3, 0f, 0f, 0f)
                registerSixNode(id, 3, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Simple Lamp Socket")
                val desc = LampSocketDescriptor(name, LampSocketStandardObjRender(EAU.obj.getObj("SimpleLamp"), true),
                    LampSocketType.Douille,
                    false,
                    3, 0f, 0f, 0f)
                registerSixNode(id, 4, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Fluorescent Lamp Socket")
                val desc = LampSocketDescriptor(name, LampSocketStandardObjRender(EAU.obj.getObj("FluorescentLamp"), true),
                    LampSocketType.Douille,
                    false,
                    4, 0f, 0f, 0f)
                desc.cableLeft = false
                desc.cableRight = false
                registerSixNode(id, 5, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Street Light")
                val desc = LampSocketDescriptor(name, LampSocketStandardObjRender(EAU.obj.getObj("StreetLight"), true),
                    LampSocketType.Douille,
                    false,
                    0, 0f, 0f, 0f)
                desc.setPlaceDirection(Direction.YN)
                val g = org.ja13.eau.ghost.GhostGroup()
                g.addElement(1, 0, 0)
                g.addElement(2, 0, 0)
                desc.setGhostGroup(g)
                desc.renderIconInHand = true
                desc.cameraOpt = false
                registerSixNode(id, 6, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Sconce Lamp Socket")
                val desc = LampSocketDescriptor(name, LampSocketStandardObjRender(EAU.obj.getObj("SconceLamp"), true),
                    LampSocketType.Douille,
                    true,
                    3, 0f, 0f, 0f)
                desc.setPlaceDirection(arrayOf(Direction.XP, Direction.XN, Direction.ZP, Direction.ZN))
                desc.setInitialOrientation(-90f)
                desc.setUserRotationLibertyDegrees(true)
                registerSixNode(id, 7, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Suspended Lamp Socket")
                val desc = LampSocketDescriptor(name,
                    LampSocketSuspendedObjRender(EAU.obj.getObj("RobustLampSuspended"), true, 3),
                    LampSocketType.Douille,
                    false,
                    3, 0f, 0f, 0f)
                desc.setPlaceDirection(Direction.YP)
                desc.cameraOpt = false
                registerSixNode(id, 8, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Long Suspended Lamp Socket")
                val desc = LampSocketDescriptor(name,
                    LampSocketSuspendedObjRender(EAU.obj.getObj("RobustLampSuspended"), true, 7),
                    LampSocketType.Douille,
                    false,
                    4, 0f, 0f, 0f)
                desc.setPlaceDirection(Direction.YP)
                desc.cameraOpt = false
                registerSixNode(id, 9, desc)
            }
            run {
                val desc = EmergencyLampDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "50V Emergency Lamp"),
                    EAU.smallInsulationLowCurrentCopperCable, (10 * 60 * 10).toDouble(), 10.0, 5.0, 6, EAU.obj.getObj("EmergencyExitLighting"))
                registerSixNode(id, 10, desc)
            }
            run {
                val desc = EmergencyLampDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "200V Emergency Lamp"),
                    EAU.smallInsulationLowCurrentCopperCable, (10 * 60 * 20).toDouble(), 25.0, 10.0, 8, EAU.obj.getObj("EmergencyExitLighting"))
                registerSixNode(id, 11, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Suspended Lamp Socket (No Swing)")
                val desc = LampSocketDescriptor(name,
                    LampSocketSuspendedObjRender(EAU.obj.getObj("RobustLampSuspended"), true, 3, false),
                    LampSocketType.Douille,
                    false,
                    3, 0f, 0f, 0f)
                desc.setPlaceDirection(Direction.YP)
                desc.cameraOpt = false
                registerSixNode(id, 12, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Long Suspended Lamp Socket (No Swing)")
                val desc = LampSocketDescriptor(name,
                    LampSocketSuspendedObjRender(EAU.obj.getObj("RobustLampSuspended"), true, 7, false),
                    LampSocketType.Douille,
                    false,
                    4, 0f, 0f, 0f)
                desc.setPlaceDirection(Direction.YP)
                desc.cameraOpt = false
                registerSixNode(id, 13, desc)
            }
        }

        private fun registerLampSupply(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Lamp Supply")
                val desc = LampSupplyDescriptor(
                    name, EAU.obj.getObj("DistributionBoard"),
                    32
                )
                registerSixNode(id, 0, desc)
            }
        }

        private fun registerBatteryCharger(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Weak 12V Battery Charger")
                val desc = BatteryChargerDescriptor(
                    name, EAU.obj.getObj("batterychargera"),
                    EAU.smallInsulationLowCurrentCopperCable,
                    VoltageTier.LOW.voltage, 200.0
                )
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "12V Battery Charger")
                val desc = BatteryChargerDescriptor(
                    name, EAU.obj.getObj("batterychargera"),
                    EAU.smallInsulationLowCurrentCopperCable,
                    VoltageTier.LOW.voltage, 400.0
                )
                registerSixNode(id, 1, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "120V Battery Charger")
                val desc = BatteryChargerDescriptor(
                    name, EAU.obj.getObj("batterychargera"),
                    EAU.smallInsulationMediumCurrentCopperCable,
                    VoltageTier.LOW_HOUSEHOLD.voltage, 1000.0
                )
                registerSixNode(id, 2, desc)
            }
        }

        private fun registerWirelessSignal(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Wireless Signal Receiver")
                val desc = WirelessSignalRxDescriptor(
                    name,
                    EAU.obj.getObj("wirelesssignalrx")
                )
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Wireless Signal Transmitter")
                val desc = WirelessSignalTxDescriptor(
                    name,
                    EAU.obj.getObj("wirelesssignaltx"),
                    EAU.wirelessTxRange
                )
                registerSixNode(id, 1, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Wireless Signal Repeater")
                val desc = WirelessSignalRepeaterDescriptor(
                    name,
                    EAU.obj.getObj("wirelesssignalrepeater"),
                    EAU.wirelessTxRange
                )
                registerSixNode(id, 2, desc)
            }
        }

        private fun registerElectricalDataLogger(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Data Logger")
                val desc = ElectricalDataLoggerDescriptor(name, true,
                    "DataloggerCRTFloor", 1f, 0.5f, 0f, "\u00a76")
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Modern Data Logger")
                val desc = ElectricalDataLoggerDescriptor(name, true,
                    "FlatScreenMonitor", 0.0f, 1f, 0.0f, "\u00A7a")
                registerSixNode(id, 1, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Industrial Data Logger")
                val desc = ElectricalDataLoggerDescriptor(name, false,
                    "IndustrialPanel", 0.25f, 0.5f, 1f, "\u00A7f")
                registerSixNode(id, 2, desc)
            }
        }

        private fun registerElectricalRelay(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Low Current Relay")
                val desc = ElectricRelayDescriptor(name, EAU.obj.getObj("RelaySmall"), EAU.smallInsulationLowCurrentRender)
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Medium Current Relay")
                val desc = ElectricRelayDescriptor(name, EAU.obj.getObj("RelayBig"), EAU.smallInsulationMediumCurrentRender)
                registerSixNode(id, 1, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "High Current Relay")
                val desc = ElectricRelayDescriptor(name, EAU.obj.getObj("relay800"), EAU.smallInsulationHighCurrentRender)
                registerSixNode(id, 2, desc)
            }
        }

        private fun registerElectricalGateSource(id: Int) {
            var name: String
            val signalsourcepot = ElectricalGateSourceRenderObj(EAU.obj.getObj("signalsourcepot"))
            val ledswitch = ElectricalGateSourceRenderObj(EAU.obj.getObj("ledswitch"))
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Signal Trimmer")
                val desc = ElectricalGateSourceDescriptor(name, signalsourcepot, false,
                    "trimmer")
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Signal Switch")
                val desc = ElectricalGateSourceDescriptor(name, ledswitch, true,
                    if (EAU.noSymbols) "signalswitch" else "switch")
                registerSixNode(id, 1, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Signal Switch with LED")
                val desc = ElectricalSwitchDescriptor(name, EAU.smallInsulationLowCurrentRender,
                    EAU.obj.getObj("ledswitch"), VoltageTier.TTL.voltage, 0.5, 0.02,
                    VoltageTier.TTL.voltage * 1.5, 0.5 * 1.2,
                    EAU.cableThermalLoadInitializer.copy(), true)
                registerSixNode(id, 2, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Signal Button")
                val desc = ElectricalGateSourceDescriptor(name, ledswitch, true, "button")
                desc.setWithAutoReset()
                registerSixNode(id, 3, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Wireless Button")
                val desc = WirelessSignalSourceDescriptor(name, ledswitch, EAU.wirelessTxRange, true)
                registerSixNode(id, 4, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Wireless Switch")
                val desc = WirelessSignalSourceDescriptor(name, ledswitch, EAU.wirelessTxRange, false)
                registerSixNode(id, 5, desc)
            }
        }

        private fun registerPassiveComponent(id: Int) {
            var name: String
            var function: IFunction
            val baseFunction = FunctionTableYProtect(doubleArrayOf(0.0, 0.01, 0.03, 0.1, 0.2, 0.4, 0.8, 1.2), 1.0,
                0.0, 5.0)
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "10A Diode")
                function = FunctionTableYProtect(doubleArrayOf(0.0, 0.1, 0.3,
                    1.0, 2.0, 4.0, 8.0, 12.0), 1.0, 0.0, 100.0)
                val desc = DiodeDescriptor(
                    name,
                    function,
                    10.0,
                    1.0, 10.0,
                    EAU.sixNodeThermalLoadInitializer.copy(),
                    EAU.smallInsulationLowCurrentCopperCable,
                    EAU.obj.getObj("PowerElectricPrimitives"))
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "25A Diode")
                function = FunctionTableYProtect(doubleArrayOf(0.0, 0.25,
                    0.75, 2.5, 5.0, 10.0, 20.0, 30.0), 1.0, 0.0, 100.0)
                val desc = DiodeDescriptor(
                    name,
                    function,
                    25.0,
                    1.0, 25.0,
                    EAU.sixNodeThermalLoadInitializer.copy(),
                    EAU.smallInsulationLowCurrentCopperCable,
                    EAU.obj.getObj("PowerElectricPrimitives"))
                registerSixNode(id, 1, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Signal Diode")
                function = baseFunction.duplicate(1.0, 0.1)
                val desc = DiodeDescriptor(name,
                    function, 0.1,
                    1.0, 0.1,
                    EAU.sixNodeThermalLoadInitializer.copy(), EAU.smallInsulationLowCurrentCopperCable,
                    EAU.obj.getObj("PowerElectricPrimitives"))
                registerSixNode(id, 2, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Power Capacitor")
                val desc = PowerCapacitorSixDescriptor(
                    name, EAU.obj.getObj("PowerElectricPrimitives"), SeriesMap.newE6(-1.0), (60 * 2000).toDouble()
                )
                registerSixNode(id, 4, desc)
                EAU.cableTabIcon = desc.newItemStack().item
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Power Inductor")
                val desc = PowerInductorSixDescriptor(
                    name, EAU.obj.getObj("PowerElectricPrimitives"), SeriesMap.newE6(-1.0)
                )
                registerSixNode(id, 5, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Power Resistor")
                val desc = ResistorDescriptor(
                    name, EAU.obj.getObj("PowerElectricPrimitives"), SeriesMap.newE12(-2.0), 0.0, false
                )
                registerSixNode(id, 6, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Rheostat")
                val desc = ResistorDescriptor(
                    name, EAU.obj.getObj("PowerElectricPrimitives"), SeriesMap.newE12(-2.0), 0.0, true
                )
                registerSixNode(id, 7, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Thermistor")
                val desc = ResistorDescriptor(
                    name, EAU.obj.getObj("PowerElectricPrimitives"), SeriesMap.newE12(-2.0), -0.01, false
                )
                registerSixNode(id, 8, desc)
            }
        }

        private fun registerSwitch(id: Int) {
            var name: String

            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Low Current Switch")
                val desc = ElectricalSwitchDescriptor(name, EAU.smallInsulationLowCurrentRender,
                    EAU.obj.getObj("LowVoltageSwitch"), 240.0, 240.0 * 20, 0.1, 1000.0, 240.0 * 25, EAU.cableThermalLoadInitializer.copy(), false)
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "High Current Switch")
                val desc = ElectricalSwitchDescriptor(name, EAU.smallInsulationHighCurrentRender,
                    EAU.obj.getObj("HighVoltageSwitch"), 13_200.0, 13_200.0 * 10, 0.1, 13_200.0, 10000.0 * 12, EAU.cableThermalLoadInitializer.copy(), false)
                registerSixNode(id, 2, desc)
            }
        }

        private fun registerElectricalManager(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Breaker")
                val desc = ElectricalBreakerDescriptor(name, EAU.obj.getObj("ElectricalBreaker"))
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Energy Meter")
                val desc = EnergyMeterDescriptor(name, EAU.obj.getObj("EnergyMeter"), 8, 0)
                registerSixNode(id, 1, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Advanced Energy Meter")
                val desc = EnergyMeterDescriptor(name, EAU.obj.getObj("AdvancedEnergyMeter"), 7, 8)
                registerSixNode(id, 2, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Fuse Holder")
                val desc = ElectricalFuseHolderDescriptor(name, EAU.obj.getObj("ElectricalFuse"))
                registerSixNode(id, 3, desc)
            }
        }

        private fun registerElectricalSensor(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Probe")
                val desc = ElectricalSensorDescriptor(name, "electricalsensor",
                    false)
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Voltage Probe")
                val desc = ElectricalSensorDescriptor(name, "voltagesensor", true)
                registerSixNode(id, 1, desc)
            }
        }

        private fun registerThermalSensor(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Thermal Probe")
                val desc = ThermalSensorDescriptor(name,
                    EAU.obj.getObj("thermalsensor"), false)
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Temperature Probe")
                val desc = ThermalSensorDescriptor(name,
                    EAU.obj.getObj("temperaturesensor"), true)
                registerSixNode(id, 1, desc)
            }
        }

        private fun registerElectricalVuMeter(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Analog vuMeter")
                val desc = ElectricalVuMeterDescriptor(name, "Vumeter", false)
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "LED vuMeter")
                val desc = ElectricalVuMeterDescriptor(name, "Led", true)
                registerSixNode(id, 1, desc)
            }
        }

        private fun registerElectricalAlarm(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Nuclear Alarm")
                val desc = ElectricalAlarmDescriptor(name,
                    EAU.obj.getObj("alarmmedium"), 7, "eln:alarma", 11.0, 1f)
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Standard Alarm")
                val desc = ElectricalAlarmDescriptor(name,
                    EAU.obj.getObj("alarmmedium"), 7, "eln:smallalarm_critical",
                    1.2, 2f)
                registerSixNode(id, 1, desc)
            }
        }

        private fun registerElectricalEnvironmentalSensor(id: Int) {
            var name: String
            run {
                var desc: ElectricalLightSensorDescriptor
                run {
                    name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Daylight Sensor")
                    desc = ElectricalLightSensorDescriptor(name, EAU.obj.getObj("daylightsensor"), true)
                    registerSixNode(id, 0, desc)
                }
                run {
                    name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Light Sensor")
                    desc = ElectricalLightSensorDescriptor(name, EAU.obj.getObj("lightsensor"), false)
                    registerSixNode(id, 1, desc)
                }
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Weather Sensor")
                val desc = ElectricalWeatherSensorDescriptor(name, EAU.obj.getObj("electricalweathersensor"))
                registerSixNode(id, 2, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Anemometer Sensor")
                val desc = ElectricalWindSensorDescriptor(name, EAU.obj.getObj("Anemometer"), 25.0)
                registerSixNode(id, 3, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Entity Sensor")
                val desc = ElectricalEntitySensorDescriptor(name, EAU.obj.getObj("ProximitySensor"), 10.0)
                registerSixNode(id, 4, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Fire Detector")
                val desc = ElectricalFireDetectorDescriptor(name, EAU.obj.getObj("FireDetector"), 15.0, false)
                registerSixNode(id, 5, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Fire Buzzer")
                val desc = ElectricalFireDetectorDescriptor(name, EAU.obj.getObj("FireDetector"), 15.0, true)
                registerSixNode(id, 6, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Scanner")
                val desc = ScannerDescriptor(name, EAU.obj.getObj("scanner"))
                registerSixNode(id, 7, desc)
            }
        }

        private fun registerElectricalRedstone(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Redstone-to-Voltage Converter")
                val desc = ElectricalRedstoneInputDescriptor(name, EAU.obj.getObj("redtoele"))
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Voltage-to-Redstone Converter")
                val desc = ElectricalRedstoneOutputDescriptor(name,
                    EAU.obj.getObj("eletored"))
                registerSixNode(id, 1, desc)
            }
        }

        private fun registerElectricalGate(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Electrical Timer")
                val desc = ElectricalTimeoutDescriptor(name,
                    EAU.obj.getObj("electricaltimer"))
                desc.setTickSound("eln:timer", 0.01f)
                registerSixNode(id, 0, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Signal Processor")
                val desc = ElectricalMathDescriptor(name,
                    EAU.obj.getObj("PLC"))
                registerSixNode(id, 1, desc)
            }
        }

        private fun registerTreeResinCollector(id: Int) {
            run {
                val name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Tree Resin Collector")
                val desc = TreeResinCollectorDescriptor(name, EAU.obj.getObj("treeresincolector"))
                registerSixNode(id, 0, desc)
            }
        }

        private fun registerSixNodeMisc(id: Int) {
            var name: String
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Modbus RTU")
                val desc = ModbusRtuDescriptor(
                    name,
                    EAU.obj.getObj("RTU")
                )
                if (EAU.modbusEnable) {
                    registerSixNode(id, 0, desc)
                } else {
                    registerHiddenSixNode(id, 0, desc)
                }
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Analog Watch")
                val desc = ElectricalWatchDescriptor(
                    name,
                    EAU.obj.getObj("WallClock"),
                    20000.0 / (3600 * 40)
                )
                registerSixNode(id, 1, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Digital Watch")
                val desc = ElectricalWatchDescriptor(
                    name,
                    EAU.obj.getObj("DigitalWallClock"),
                    20000.0 / (3600 * 15)
                )
                registerSixNode(id, 2, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Digital Display")
                val desc = ElectricalDigitalDisplayDescriptor(
                    name,
                    EAU.obj.getObj("DigitalDisplay")
                )
                registerSixNode(id, 3, desc)
            }
            run {
                name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Tutorial Sign")
                val desc = TutorialSignDescriptor(
                    name, EAU.obj.getObj("TutoPlate"))
                registerSixNode(id, 4, desc)
            }
        }

        private fun registerLogicalGates(id: Int) {
            val model = EAU.obj.getObj("LogicGates")
            registerSixNode(id, 0, LogicGateDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "NOT Chip"), model, "NOT", Not::class.java))
            registerSixNode(id, 1, LogicGateDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "AND Chip"), model, "AND", And::class.java))
            registerSixNode(id, 2, LogicGateDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "NAND Chip"), model, "NAND", Nand::class.java))
            registerSixNode(id, 3, LogicGateDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "OR Chip"), model, "OR", Or::class.java))
            registerSixNode(id, 4, LogicGateDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "NOR Chip"), model, "NOR", Nor::class.java))
            registerSixNode(id, 5, LogicGateDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "XOR Chip"), model, "XOR", Xor::class.java))
            registerSixNode(id, 6, LogicGateDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "XNOR Chip"), model, "XNOR", XNor::class.java))
            registerSixNode(id, 7, PalDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "PAL Chip"), model))
            registerSixNode(id, 8, LogicGateDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Schmitt Trigger Chip"), model, "SCHMITT",
                SchmittTrigger::class.java))
            registerSixNode(id, 9, LogicGateDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "D Flip Flop Chip"), model, "DFF", DFlipFlop::class.java))
            registerSixNode(id, 10, LogicGateDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Oscillator Chip"), model, "OSC", Oscillator::class.java))
            registerSixNode(id, 11, LogicGateDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "JK Flip Flop Chip"), model, "JKFF", JKFlipFlop::class.java))
        }

        private fun registerAnalogChips(id: Int) {
            val model = EAU.obj.getObj("AnalogChips")
            registerSixNode(id, 0, AnalogChipDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "OpAmp"), model, "OP", OpAmp::class.java))
            registerSixNode(id, 1, AnalogChipDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "PID Regulator"), model, "PID",
                PIDRegulator::class.java, PIDRegulatorElement::class.java, PIDRegulatorRender::class.java))
            registerSixNode(id, 2, AnalogChipDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Voltage controlled sawtooth oscillator"), model, "VCO-SAW",
                VoltageControlledSawtoothOscillator::class.java))
            registerSixNode(id, 3, AnalogChipDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Voltage controlled sine oscillator"), model, "VCO-SIN",
                VoltageControlledSineOscillator::class.java))
            registerSixNode(id, 4, AnalogChipDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Amplifier"), model, "AMP",
                Amplifier::class.java, AmplifierElement::class.java, AmplifierRender::class.java))
            registerSixNode(id, 5, AnalogChipDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Voltage controlled amplifier"), model, "VCA",
                VoltageControlledAmplifier::class.java))
            registerSixNode(id, 6, AnalogChipDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Configurable summing unit"), model, "SUM",
                SummingUnit::class.java, SummingUnitElement::class.java, SummingUnitRender::class.java))
            registerSixNode(id, 7, AnalogChipDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Sample and hold"), model, "SAH",
                SampleAndHold::class.java))
            registerSixNode(id, 8, AnalogChipDescriptor(org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Lowpass filter"), model, "LPF",
                Filter::class.java, FilterElement::class.java, FilterRender::class.java))
        }

        private fun registerDevStuff(id: Int) {
            run {
                val name = org.ja13.eau.i18n.I18N.TR_NAME(org.ja13.eau.i18n.I18N.Type.NONE, "Portable NaN")
                EAU.stdPortableNaN = org.ja13.eau.cable.CableRenderDescriptor("eau", "sprites/nan.png", 3.95f, 0.95f)
                EAU.portableNaNDescriptor = PortableNaNDescriptor(name, EAU.stdPortableNaN)
                registerSixNode(id, 0, EAU.portableNaNDescriptor)
            }
        }
    }
}
