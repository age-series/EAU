package org.ja13.eau.gridnode.transformer

import org.ja13.eau.EAU
import org.ja13.eau.gridnode.GridElement
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Utils
import org.ja13.eau.node.NodePeriodicPublishProcess
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.mna.component.VoltageSource
import org.ja13.eau.sim.mna.process.TransformerInterSystemProcess
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import org.ja13.eau.sim.nbt.NbtThermalLoad
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog
import org.ja13.eau.sim.process.destruct.WorldExplosion
import org.ja13.eau.sim.process.heater.ElectricalLoadHeatThermalLoad
import net.minecraft.util.Vec3
import java.io.DataOutputStream

class GridTransformerElement(node: org.ja13.eau.node.transparent.TransparentNode, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : GridElement(node, descriptor, 8) {
    var primaryLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("primaryLoad")
    var secondaryLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("secondaryLoad")
    var primaryVoltageSource = org.ja13.eau.sim.mna.component.VoltageSource("primaryVoltageSource", primaryLoad, null)
    var secondaryVoltageSource = org.ja13.eau.sim.mna.component.VoltageSource("secondaryVoltageSource", secondaryLoad, null)
    var interSystemProcess = org.ja13.eau.sim.mna.process.TransformerInterSystemProcess(primaryLoad, secondaryLoad, primaryVoltageSource, secondaryVoltageSource)
    internal var desc: GridTransformerDescriptor = descriptor as GridTransformerDescriptor
    internal var maxCurrent = desc.cableDescriptor.electricalMaximalCurrent.toFloat()

    // Primary is the T2 coupling, secondary is the lower-voltage T1 coupling.
    internal val secondaryVoltage = desc.cableDescriptor.electricalNominalVoltage * 16
    internal val primaryVoltage = secondaryVoltage * 4

    internal val explosion = org.ja13.eau.sim.process.destruct.WorldExplosion(this).machineExplosion()

    internal var voltagePrimaryWatchdog = org.ja13.eau.sim.process.destruct.VoltageStateWatchDog().apply {
        setUNominal(primaryVoltage)
        set(primaryLoad)
        set(explosion)
        slowProcessList.add(this)
    }
    internal var voltageSecondaryWatchdog = org.ja13.eau.sim.process.destruct.VoltageStateWatchDog().apply {
        setUNominal(secondaryVoltage)
        set(secondaryLoad)
        set(explosion)
        slowProcessList.add(this)
    }

    internal val thermalLoad = org.ja13.eau.sim.nbt.NbtThermalLoad("thermal").apply {
        desc.cableDescriptor.applyTo(this)
        setAsSlow()
        slowProcessList.add(org.ja13.eau.sim.process.heater.ElectricalLoadHeatThermalLoad(secondaryLoad, this))
        thermalLoadList.add(this)
    }
    internal val thermalWatchdog = org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog().apply {
        setLimit(desc.cableDescriptor.thermalWarmLimit, desc.cableDescriptor.thermalCoolLimit)
        set(thermalLoad)
        set(explosion)
        slowProcessList.add(this)
    }

    init {
        electricalLoadList.add(primaryLoad)
        electricalLoadList.add(secondaryLoad)
        electricalComponentList.add(primaryVoltageSource)
        electricalComponentList.add(secondaryVoltageSource)

        desc.cableDescriptor.applyTo(primaryLoad, 0.0001)
        desc.cableDescriptor.applyTo(secondaryLoad, 0.0001)

        interSystemProcess.ratio = 0.25

        // Publish load from time to time.
        slowProcessList.add(org.ja13.eau.node.NodePeriodicPublishProcess(node, 1.0, 0.5))
    }

    override fun getConnectionMask(side: Direction, lrdu: LRDU): Int {
        return 0
    }

    override fun disconnectJob() {
        super.disconnectJob()
        org.ja13.eau.EAU.simulator.mna.removeProcess(interSystemProcess)
    }

    override fun connectJob() {
        org.ja13.eau.EAU.simulator.mna.addProcess(interSystemProcess)
        super.connectJob()
    }

    override fun getElectricalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ElectricalLoad? {
        return when (side) {
            front.left() -> primaryLoad
            front.right() -> secondaryLoad
            else -> null
        }
    }

    // TODO: Factor this against super.
    public override fun getCablePoint(side: Direction, i: Int): Vec3 {
        if (i >= 2) throw AssertionError("Invalid cable point index")
        val idx = when (side) {
            front.left() -> 1
            front.right() -> 0
            else -> throw AssertionError("Invalid connection side")
        }
        val part = (if (i == 0) desc.plus else desc.gnd)[idx]
        return part.boundingBox().centre()
    }

    override fun getGridElectricalLoad(side: Direction): org.ja13.eau.sim.ElectricalLoad? {
        return getElectricalLoad(side, LRDU.Down)
    }

    override fun getThermalLoad(side: Direction, lrdu: LRDU) = thermalLoad

    override fun multiMeterString(side: Direction): String {
        return (Utils.plotVolt(primaryLoad.u, "Primary Voltage:") + Utils.plotAmpere(primaryLoad.current, "Primary Current:")
            + Utils.plotVolt(secondaryLoad.u, "Secondary Voltage:") + Utils.plotAmpere(secondaryLoad.current, "Secondary Current:"))
    }

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        stream.writeDouble(secondaryLoad.current / maxCurrent)
    }

    override fun getLightOpacity(): Float {
        return 1.0f
    }
}


