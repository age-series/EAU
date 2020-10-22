package org.ja13.eau.mechanical

import org.ja13.eau.EAU
import org.ja13.eau.fluid.FuelRegistry
import org.ja13.eau.fluid.PreciseElementFluidHandler
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.INBTTReady
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.RcInterpolator
import org.ja13.eau.misc.Utils
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
import org.lwjgl.opengl.GL11
import java.io.DataInputStream
import java.io.DataOutputStream

class RotaryMotorDescriptor(baseName: String, obj: org.ja13.eau.misc.Obj3D) :
    SimpleShaftDescriptor(baseName, RotaryMotorElement::class, RotaryMotorRender::class, org.ja13.eau.node.transparent.EntityMetaTag.Fluid) {
    companion object {
        const val GAS_GUZZLER_CONSTANT = 0.5
    }

    override val sound = "eln:RotaryEngine"
    override val static = arrayOf(
        obj.getPart("Body_Cylinder.001")
    )
    override val rotating = arrayOf(
        obj.getPart("Shaft")
    )
    override fun preDraw() {
        GL11.glTranslated(-0.5, -1.5, 0.5)
    }
    // Overall time for steam input changes to take effect, in seconds.
    val inertia: Double = 3.0
    // Maximum fluid consumed per second, mB.
    val fluidConsumption: Float = 64f
    // How we describe the fluid in the tooltip.
    val fluidDescription: String = "gasoline"
    // The fluids actually accepted.
    val fluidTypes: Array<String> = FuelRegistry.gasolineList + FuelRegistry.gasList
    // Width of the efficiency curve.
    // <1 means "Can't be started without power".
    val efficiencyCurve: Float = 1.5f
    val optimalRads = absoluteMaximumShaftSpeed * 0.8f
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

    @Suppress("CanBePrimaryConstructorProperty") // If you do that, it changes the constructor and BLAMO, Crash!
    override val obj: org.ja13.eau.misc.Obj3D = obj

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        list.add("Converts $fluidDescription into mechanical energy.")
        list.add("Nominal usage ->")
        list.add("  ${fluidDescription.capitalize()} input: $fluidConsumption mB/s")
        if (power.isEmpty()) {
            list.add("  No valid fluids for this turbine!")
        } else if (power.size == 1) {
            list.add(Utils.plotPower(power[0], "Power Output:"))
        } else {
            list.add("  Power out: ${Utils.plotPower(minFluidPower  * GAS_GUZZLER_CONSTANT)}- ${Utils.plotPower(maxFluidPower * GAS_GUZZLER_CONSTANT)}")
        }
        list.add(Utils.plotRads(optimalRads, "Nominal Speed:"))
        list.add(Utils.plotRads(absoluteMaximumShaftSpeed, "Absolute Maximum Speed:"))
    }
}

class RotaryMotorElement(node: org.ja13.eau.node.transparent.TransparentNode, desc_: org.ja13.eau.node.transparent.TransparentNodeDescriptor) :
    SimpleShaftElement(node, desc_) {
    val desc = desc_ as RotaryMotorDescriptor

    val tank = PreciseElementFluidHandler(desc.fluidConsumption.toInt())
    var fluidRate = 0.0
    var efficiency = 0f
    val rotaryMotorSlowProcess = RotaryMotorSlowProcess()

    internal val throttle = org.ja13.eau.sim.nbt.NbtElectricalGateInput("throttle")

    internal var volume: Double by published(0.0)

    inner class RotaryMotorSlowProcess : org.ja13.eau.sim.IProcess, INBTTReady {
        val rc = RcInterpolator(desc.inertia)

        override fun process(time: Double) {
            // Do anything at all?
            val target: Float
            val computedEfficiency = if (shaft.rads > 150 && shaft.rads < 250) {
                 RotaryMotorDescriptor.GAS_GUZZLER_CONSTANT
            } else {
                RotaryMotorDescriptor.GAS_GUZZLER_CONSTANT / 2
            }
            efficiency = computedEfficiency.toFloat()
            val th = if (throttle.connectedComponents.count() > 0) throttle.normalized else 1.0
            target = (desc.fluidConsumption * th).toFloat()

            val drained = tank.drain(target * time).toFloat()

            rc.target = (drained / time)
            rc.step(time)
            fluidRate = rc.get()

            val power = fluidRate * tank.heatEnergyPerMilliBucket * efficiency
            shaft.energy += power * time.toFloat()

            volume = if (fluidRate > 0.25) {
                Math.max(0.75, (power / desc.maxFluidPower))
            } else {
                0.0
            }
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
        slowProcessList.add(rotaryMotorSlowProcess)
        electricalLoadList.add(throttle)
    }

    override fun getFluidHandler() = tank

    override fun getElectricalLoad(side: Direction, lrdu: LRDU) = throttle
    override fun getThermalLoad(side: Direction?, lrdu: LRDU?) = null
    override fun getConnectionMask(side: Direction, lrdu: LRDU): Int {
        if (lrdu == LRDU.Down && (side == front.up() || side == front.down())) return org.ja13.eau.node.NodeBase.maskElectricalGate
        if (lrdu == LRDU.Up && (side == front.up() || side == front.down())) return org.ja13.eau.node.NodeBase.maskElectricalGate
        if (lrdu == LRDU.Left && (side == front || side == front.back())) return org.ja13.eau.node.NodeBase.maskElectricalGate
        if (lrdu == LRDU.Right && (side == front || side == front.back())) return org.ja13.eau.node.NodeBase.maskElectricalGate
        return 0
    }

    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float, vz: Float) = false

    override fun thermoMeterString(side: Direction?) = Utils.plotPercent(efficiency.toDouble(), "Efficiency:") + fluidRate.toString() + "mB/s"

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        tank.writeToNBT(nbt, "tank")
        rotaryMotorSlowProcess.writeToNBT(nbt, "proc")
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        tank.readFromNBT(nbt, "tank")
        rotaryMotorSlowProcess.readFromNBT(nbt, "proc")
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

// TODO: Particles flying out the exhaust pipe
class RotaryMotorRender(entity: org.ja13.eau.node.transparent.TransparentNodeEntity, desc: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : ShaftRender(entity, desc) {
    override val cableRender = org.ja13.eau.EAU.smallInsulationLowCurrentRender

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        volumeSetting.target = stream.readDouble()
    }

    // Prevents it from not rendering when the main block is just out of frame.
    override fun cameraDrawOptimisation() = false
}
