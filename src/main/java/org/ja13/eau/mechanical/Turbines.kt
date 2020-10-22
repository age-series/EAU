package org.ja13.eau.mechanical

import org.ja13.eau.EAU
import org.ja13.eau.fluid.FuelRegistry
import org.ja13.eau.fluid.PreciseElementFluidHandler
import org.ja13.eau.misc.*
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.published
import org.ja13.eau.node.transparent.EntityMetaTag
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeEntity
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.nbt.NbtElectricalGateInput
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import java.io.DataInputStream
import java.io.DataOutputStream

abstract class TurbineDescriptor(baseName: String, obj: org.ja13.eau.misc.Obj3D) :
    SimpleShaftDescriptor(baseName, TurbineElement::class, TurbineRender::class, org.ja13.eau.node.transparent.EntityMetaTag.Fluid) {
    // Overall time for steam input changes to take effect, in seconds.
    abstract val inertia: Double
    // Optimal fluid consumed per second, mB.
    // Computed to equal a single 36LP Railcraft boiler, or half of a 36HP.
    abstract val fluidConsumption: Float
    // How we describe the fluid in the tooltip.
    abstract val fluidDescription: String
    // The fluids actually accepted.
    abstract val fluidTypes: Array<String>
    // Width of the efficiency curve.
    // <1 means "Can't be started without power".
    abstract val efficiencyCurve: Float
    // If efficiency is below this fraction, do nothing.
    open val efficiencyCutoff = 0f
    val optimalRads = absoluteMaximumShaftSpeed * 0.2f
    // Power stats
    val power: List<Double> by lazy {
        fluidTypes.map { FuelRegistry.heatEnergyPerMilliBucket(it) * fluidConsumption }
    }
    val maxFluidPower: Double by lazy {
        power.max() ?: 0.0
    }
    val minFluidPower: Double by lazy {
        power.min() ?: 0.0
    }

    override val obj = obj
    override val static = arrayOf(
        obj.getPart("Cowl"),
        obj.getPart("Stand")
    )
    override val rotating = arrayOf(
        obj.getPart("Shaft"),
        obj.getPart("Fan")
    )

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        list.add("Converts ${fluidDescription} into mechanical energy.")
        list.add("Nominal usage ->")
        list.add("  ${fluidDescription.capitalize()} input: ${fluidConsumption} mB/s")
        if (power.isEmpty()) {
            list.add("  No valid fluids for this turbine!")
        } else if (power.size == 1) {
            list.add(Utils.plotPower(power[0], "Nominal Power:"))
        } else {
            list.add("  Power out: ${Utils.plotPower(minFluidPower)}- ${Utils.plotPower(maxFluidPower)}")
        }
        list.add(Utils.plotRads(optimalRads, "Optimal Speed:"))
        list.add(Utils.plotRads(absoluteMaximumShaftSpeed, "Absolute Maximum Speed:"))
    }
}

class SteamTurbineDescriptor(baseName: String, obj: org.ja13.eau.misc.Obj3D) :
    TurbineDescriptor(baseName, obj) {
    // Steam turbines are for baseload.
    override val inertia = 20.0
    // Computed to equal a single 36LP Railcraft boiler, or half of a 36HP.
    override val fluidConsumption = 7200f
    // Computed to equal what you'd get from Railcraft steam engines, plus a small
    // bonus because you're using Electrical Age you crazy person you.
    // This pretty much fills up a VHV line. The generator drag gives us a bit of leeway.
    override val fluidDescription = "steam"
    override val fluidTypes = FuelRegistry.steamList
    // Steam turbines can, just barely, be started without power.
    override val efficiencyCurve = 1.1f
    override val sound = "eln:steam_turbine"
}

class GasTurbineDescriptor(basename: String, obj: org.ja13.eau.misc.Obj3D) :
    TurbineDescriptor(basename, obj) {
    // The main benefit of gas turbines.
    override val inertia = 5.0
    // Provides about 8kW of power, given gasoline.
    // Less dense fuels will be proportionally less effective.
    override val fluidConsumption = 4f
    override val fluidDescription = "gasoline"
    // It runs on puns.
    override val fluidTypes = FuelRegistry.gasolineList + FuelRegistry.gasList
    // Gas turbines are finicky about turbine speed.
    override val efficiencyCurve = 0.5f
    // And need to be spun up before working.
    override val efficiencyCutoff = 0.5f
    override val sound = "eln:gas_turbine"
}

class TurbineElement(node: org.ja13.eau.node.transparent.TransparentNode, desc_: org.ja13.eau.node.transparent.TransparentNodeDescriptor) :
    SimpleShaftElement(node, desc_) {
    val desc = desc_ as TurbineDescriptor

    val tank = PreciseElementFluidHandler(desc.fluidConsumption.toInt())
    var fluidRate = 0.0
    var efficiency = 0f
    val turbineSlowProcess = TurbineSlowProcess()

    internal val throttle = org.ja13.eau.sim.nbt.NbtElectricalGateInput("throttle")

    internal var volume: Double by published(0.0)

    inner class TurbineSlowProcess : org.ja13.eau.sim.IProcess, INBTTReady {
        val rc = RcInterpolator(desc.inertia)

        override fun process(time: Double) {
            // Do anything at all?
            val target: Float
            val computedEfficiency = Math.pow(Math.cos((shaft.rads - desc.optimalRads) / (desc.optimalRads * desc.efficiencyCurve) * Math.PI / 2), 3.0)
            if (computedEfficiency >= desc.efficiencyCutoff) {
                efficiency = computedEfficiency.toFloat()
                val th = if (throttle.connectedComponents.count() > 0) throttle.normalized else 1.0
                target = (desc.fluidConsumption * th).toFloat()
            } else {
                efficiency = 0f
                target = 0f
            }

            val drained = tank.drain(target * time).toFloat()

            rc.target = (drained / time)
            rc.step(time)
            fluidRate = rc.get()

            val power = fluidRate * tank.heatEnergyPerMilliBucket * efficiency
            shaft.energy += power * time.toFloat()

            volume = power / desc.maxFluidPower
        }

        override fun readFromNBT(nbt: NBTTagCompound, str: String) {
            rc.readFromNBT(nbt, str)
        }

        override fun writeToNBT(nbt: NBTTagCompound, str: String) {
            rc.writeToNBT(nbt, str)
        }
    }

    init {
        tank.setFilter(FuelRegistry.fluidListToFluids(desc.fluidTypes))
        slowProcessList.add(turbineSlowProcess)
        electricalLoadList.add(throttle)
    }

    override fun getFluidHandler() = tank

    override fun getElectricalLoad(side: Direction, lrdu: LRDU) = throttle
    override fun getThermalLoad(side: Direction?, lrdu: LRDU?) = null
    override fun getConnectionMask(side: Direction, lrdu: LRDU): Int {
        if (lrdu == LRDU.Down && (side == front || side == front.back())) return org.ja13.eau.node.NodeBase.maskElectricalGate
        return 0
    }

    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float, vz: Float) = false

    override fun thermoMeterString(side: Direction?) = Utils.plotPercent(efficiency.toDouble(), "Efficiency:") + fluidRate.toString() + "mB/s"

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        tank.writeToNBT(nbt, "tank")
        turbineSlowProcess.writeToNBT(nbt, "proc")
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        tank.readFromNBT(nbt, "tank")
        turbineSlowProcess.readFromNBT(nbt, "proc")
    }

    override fun getWaila(): Map<String, String> {
        val info = mutableMapOf<String, String>()
        info["Speed"] = Utils.plotRads(shaft.rads)
        info["Energy"] = Utils.plotEnergy(shaft.energy)
        if (org.ja13.eau.EAU.wailaEasyMode) {
            info["Efficiency"] = Utils.plotPercent(efficiency.toDouble())
            info["Fuel usage"] = Utils.plotBuckets(fluidRate / 1000.0) + "/s"
        }
        return info
    }

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        stream.writeDouble(volume)
    }
}

class TurbineRender(entity: org.ja13.eau.node.transparent.TransparentNodeEntity, desc: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : ShaftRender(entity, desc) {
    override val cableRender = org.ja13.eau.EAU.smallInsulationLowCurrentRender

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        volumeSetting.target = stream.readDouble()
    }
}
