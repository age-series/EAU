package org.ja13.eau.gridnode.electricalpole

import org.ja13.eau.gridnode.GridDescriptor
import org.ja13.eau.gridnode.GridElement
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.VoltageTier
import java.io.DataOutputStream
import java.io.IOException

data class Transformer(
        val secondaryLoad: org.ja13.eau.sim.nbt.NbtElectricalLoad,
        val primaryVoltageSource: org.ja13.eau.sim.mna.component.VoltageSource,
        val secondaryVoltageSource: org.ja13.eau.sim.mna.component.VoltageSource,
        val interSystemProcess: org.ja13.eau.sim.mna.process.TransformerInterSystemProcess,
        val voltageSecondaryWatchdog: org.ja13.eau.sim.process.destruct.VoltageStateWatchDog
)

class ElectricalPoleElement(node: org.ja13.eau.node.transparent.TransparentNode, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor)
    : GridElement(node, descriptor, (descriptor as GridDescriptor).connectRange) {
    private val desc = descriptor as ElectricalPoleDescriptor

    var electricalLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("electricalLoad")
    var thermalLoad = org.ja13.eau.sim.nbt.NbtThermalLoad("thermalLoad")
    internal var heater = org.ja13.eau.sim.process.heater.ElectricalLoadHeatThermalLoad(electricalLoad, thermalLoad)
    internal var thermalWatchdog = org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog()
    internal var voltageWatchdog = org.ja13.eau.sim.process.destruct.VoltageStateWatchDog()
    internal var secondaryMaxCurrent = 0f

    val trafo: Transformer?

    init {
        electricalLoad.setCanBeSimplifiedByLine(true)
        // Most of the resistance is in the cable, which is handled in GridLink.
        // We put some of it here, thereby allowing the thermal watchdog to work.
        desc.cableDescriptor.applyTo(electricalLoad, 0.01)
        desc.cableDescriptor.applyTo(thermalLoad)
        electricalLoadList.add(electricalLoad)

        thermalLoadList.add(thermalLoad)
        slowProcessList.add(heater)
        thermalLoad.setAsSlow()
        slowProcessList.add(thermalWatchdog)
        thermalWatchdog
                .set(thermalLoad)
                .setLimit(desc.cableDescriptor.thermalWarmLimit, desc.cableDescriptor.thermalCoolLimit)
                .set(org.ja13.eau.sim.process.destruct.WorldExplosion(this).cableExplosion())

        slowProcessList.add(voltageWatchdog)
        // Electrical poles can handle higher voltages, due to air insulation.
        // This puts utility poles at 4 * Very High Voltage.
        val exp: org.ja13.eau.sim.process.destruct.WorldExplosion
        if (desc.includeTransformer) {
            exp = org.ja13.eau.sim.process.destruct.WorldExplosion(this).machineExplosion()
        } else {
            exp = org.ja13.eau.sim.process.destruct.WorldExplosion(this).cableExplosion()
        }
        voltageWatchdog
                .set(electricalLoad)
                .setUMaxMin(desc.voltageLimit)
                .set(exp)

        if (desc.includeTransformer) {
            val secondaryLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("secondaryLoad")
            val primaryVoltageSource = org.ja13.eau.sim.mna.component.VoltageSource("primaryVoltageSource", electricalLoad, null)
            val secondaryVoltageSource = org.ja13.eau.sim.mna.component.VoltageSource("secondaryVoltageSource", secondaryLoad, null)
            val interSystemProcess = org.ja13.eau.sim.mna.process.TransformerInterSystemProcess(electricalLoad, secondaryLoad, primaryVoltageSource, secondaryVoltageSource)
            val voltageSecondaryWatchdog = org.ja13.eau.sim.process.destruct.VoltageStateWatchDog()

            trafo = Transformer(
                secondaryLoad,
                primaryVoltageSource,
                secondaryVoltageSource,
                interSystemProcess,
                voltageSecondaryWatchdog
            )

            desc.cableDescriptor.applyTo(secondaryLoad, 0.0001)

            electricalLoadList.add(secondaryLoad)
            electricalComponentList.add(primaryVoltageSource)
            electricalComponentList.add(secondaryVoltageSource)
            slowProcessList.add(voltageSecondaryWatchdog.set(secondaryLoad).set(exp))

            // Publish load from time to time.
            slowProcessList.add(org.ja13.eau.node.NodePeriodicPublishProcess(node, 1.0, 0.5))
        } else {
            trafo = null
        }
    }

    override fun disconnectJob() {
        super.disconnectJob()
        trafo?.apply {
            org.ja13.eau.EAU.simulator.mna.removeProcess(interSystemProcess)
        }
    }

    override fun connectJob() {
        trafo?.apply {
            org.ja13.eau.EAU.simulator.mna.addProcess(interSystemProcess)
        }
        super.connectJob()
    }

    override fun multiMeterString(side: Direction): String {
        if (trafo != null) {
            return (Utils.plotVolt(electricalLoad.u, "GridU:") + Utils.plotAmpere(electricalLoad.current, "GridP:")
                + Utils.plotVolt(trafo.secondaryLoad.u, "GroundU:") + Utils.plotAmpere(trafo.secondaryLoad.current, "GroundP:"))
        } else {
            return super.multiMeterString(side)
        }
    }

    override fun getElectricalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ElectricalLoad? {
        return trafo?.secondaryLoad
    }

    override fun getGridElectricalLoad(side: Direction): org.ja13.eau.sim.ElectricalLoad {
        return electricalLoad
    }

    override fun getThermalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ThermalLoad {
        return thermalLoad
    }

    override fun getConnectionMask(side: Direction, lrdu: LRDU): Int {
        if (desc.includeTransformer) {
            return org.ja13.eau.node.NodeBase.MASK_ELECTRIC
        } else {
            return 0
        }
    }

    override fun initialize() {
        trafo?.apply {
            voltageSecondaryWatchdog.setUNominal(VoltageTier.DISTRIBUTION_GRID.voltage)
            secondaryMaxCurrent = desc.cableDescriptor.electricalMaximalCurrent.toFloat()
            interSystemProcess.ratio = 0.25
        }
        super.initialize()
    }

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        node.lrduCubeMask.getTranslate(front.down()).serialize(stream)
        try {
            if (trafo != null && secondaryMaxCurrent != 0f) {
                stream.writeDouble((trafo.secondaryLoad.i / secondaryMaxCurrent))
            } else {
                stream.writeDouble(0.0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
