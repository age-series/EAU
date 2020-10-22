package org.ja13.eau.transparentnode

import net.minecraft.client.audio.ISound
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.client.IItemRenderer
import net.minecraftforge.fluids.FluidContainerRegistry
import net.minecraftforge.fluids.FluidRegistry
import org.ja13.eau.EAU
import org.ja13.eau.fluid.FuelRegistry
import org.ja13.eau.i18n.I18N.tr
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.LRDUMask
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.SlewLimiter
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.UtilsClient
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.misc.preserveMatrix
import org.ja13.eau.node.published
import org.ja13.eau.sound.LoopedSound
import org.lwjgl.opengl.GL11
import java.io.DataInputStream
import java.io.DataOutputStream

class FuelGeneratorDescriptor(name: String, internal val obj: Obj3D?, internal val nominalVoltage: Double,
                              internal val nominalPower: Double, internal val maxVoltage: Double,
                              tankCapacityInSecondsAtNominalPower: Double)
    : org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, FuelGeneratorElement::class.java, FuelGeneratorRender::class.java) {
    companion object {
        internal fun EfficiencyFactorVsLoadFactor(loadFactor: Double) = when (Utils.limit(loadFactor, 0.0, 1.5)) {
            in 0.0..0.1 -> 1.375
            in 0.1..0.2 -> 1.125
            in 0.2..0.3 -> 1.050
            in 0.3..0.4 -> 1.025
            in 0.4..0.5 -> 1.010
            in 0.5..1.1 -> 1.000
            in 1.1..1.2 -> 1.050
            in 1.2..1.3 -> 1.100
            in 1.3..1.4 -> 1.150
            in 1.4..1.5 -> 1.5
            else -> 1.5
        }

        val TankCapacityInBuckets = 2
        val GeneratorBailOutVoltageRatio = 0.5
        val MinimalLoadFractionOfNominalPower = 0.1
        val VoltageStabilizationGracePeriod = 1.0
    }

    internal val tankEnergyCapacity = tankCapacityInSecondsAtNominalPower * nominalPower

    internal val main = obj?.getPart("main")
    internal val switch = obj?.getPart("switch")

    internal val fuels = FuelRegistry.gasolineList

    init {
        voltageTier = VoltageTier.NEUTRAL
    }

    fun draw(on: Boolean = false) {
        main?.draw()
        if (on) {
            UtilsClient.drawLight(switch)
        } else {
            switch?.draw()
        }
    }

    override fun handleRenderType(item: ItemStack, type: IItemRenderer.ItemRenderType) = true

    override fun shouldUseRenderHelper(
        type: IItemRenderer.ItemRenderType, item: ItemStack,
        helper: IItemRenderer.ItemRendererHelper) = type != IItemRenderer.ItemRenderType.INVENTORY

    override fun renderItem(type: IItemRenderer.ItemRenderType, item: ItemStack, vararg data: Any) = when (type) {
        IItemRenderer.ItemRenderType.INVENTORY -> super.renderItem(type, item, *data)
        else -> {
            objItemScale(obj)
            preserveMatrix {
                Direction.ZP.glRotateXnRef()
                GL11.glTranslatef(0f, -1f, 0f)
                GL11.glScalef(0.6f, 0.6f, 0.6f)
                draw()
            }
        }
    }

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)

        list.add(tr("Produces electricity using gasoline."))
        list.add("  " + tr("Nominal voltage: %1$ V", Utils.plotValue(nominalVoltage)))
        list.add("  " + tr("Nominal power: %1$ W", Utils.plotValue(nominalPower)))
    }
}

class FuelGeneratorElement(transparentNode: org.ja13.eau.node.transparent.TransparentNode, descriptor_: org.ja13.eau.node.transparent.TransparentNodeDescriptor) :
    org.ja13.eau.node.transparent.TransparentNodeElement(transparentNode, descriptor_) {
    internal var positiveLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("positiveLoad")
    internal var powerSource = org.ja13.eau.sim.mna.component.PowerSource("powerSource", positiveLoad)
    internal var slowProcess = FuelGeneratorSlowProcess(this)
    internal var descriptor = descriptor_ as FuelGeneratorDescriptor
    internal val fuels = FuelRegistry.fluidListToFluids(descriptor.fuels).map { it.id }
    internal var tankLevel = 0.0
    internal var tankFluid = FluidRegistry.getFluid("lava").id
    internal var on by published(false)
    internal var voltageGracePeriod = 0.0

    init {
        electricalLoadList.add(positiveLoad)
        electricalComponentList.add(powerSource)
        slowProcessList.add(org.ja13.eau.node.NodePeriodicPublishProcess(transparentNode, 1.0, 0.5))
        slowProcessList.add(slowProcess)
    }

    override fun getElectricalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ElectricalLoad? = when (lrdu) {
        LRDU.Down -> when (side) {
            front, front.inverse -> positiveLoad
            else -> null
        }
        else -> null
    }

    override fun getThermalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ThermalLoad? = null

    override fun getConnectionMask(side: Direction, lrdu: LRDU): Int = when (lrdu) {
        LRDU.Down -> when (side) {
            front, front.inverse -> org.ja13.eau.node.NodeBase.MASK_ELECTRIC
            else -> 0
        }
        else -> 0
    }

    override fun multiMeterString(side: Direction) = Utils.plotVolt(positiveLoad.u) +
        Utils.plotAmpere(positiveLoad.current) +
        Utils.plotPercent(tankLevel,"Fuel level:")


    override fun thermoMeterString(side: Direction): String? = null

    override fun initialize() {
        EAU.applySmallRs(positiveLoad)
        powerSource.setUmax(descriptor.maxVoltage)
        powerSource.setImax(descriptor.nominalPower * 5 / descriptor.maxVoltage)
        connect()
    }

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        node.lrduCubeMask.getTranslate(Direction.YN).serialize(stream)
        stream.writeBoolean(on)
        stream.writeDouble(positiveLoad.u / descriptor.maxVoltage)
    }

    override fun onBlockActivated(player: EntityPlayer?, side: Direction?, vx: Float, vy: Float, vz: Float): Boolean {
        if (player?.worldObj?.isRemote == false) {
            val bucket = player?.currentEquippedItem
            if (FluidContainerRegistry.isBucket(bucket) && FluidContainerRegistry.isFilledContainer(bucket)) {
                val deltaLevel = 1.0 / FuelGeneratorDescriptor.TankCapacityInBuckets
                if (tankLevel <= 1.0 - deltaLevel) {
                    val fluidStack = FluidContainerRegistry.getFluidForFilledItem(bucket)
                    if (fluidStack != null && (fluidStack.fluidID == tankFluid || tankLevel <= 0.0) &&
                        fluidStack.fluidID in fuels) {
                        tankFluid = fluidStack.fluidID
                        tankLevel += deltaLevel
                        if (player != null && !player.capabilities.isCreativeMode) {
                            val emptyBucket = FluidContainerRegistry.drainFluidContainer(bucket)
                            val slot = player.inventory.currentItem
                            player.inventory.setInventorySlotContents(slot, emptyBucket)
                        }

                        return true
                    }
                }
            } else {
                if (org.ja13.eau.EAU.multiMeterElement.checkSameItemStack(player?.currentEquippedItem) ||
                    org.ja13.eau.EAU.thermometerElement.checkSameItemStack(player?.currentEquippedItem) ||
                    org.ja13.eau.EAU.allMeterElement.checkSameItemStack(player?.currentEquippedItem)) {
                    return false
                }

                if (on) {
                    on = false
                } else {
                    if (tankLevel > 0) {
                        on = true
                        voltageGracePeriod = FuelGeneratorDescriptor.VoltageStabilizationGracePeriod
                    }
                }
                return true
            }
        }

        return false
    }

    override fun readFromNBT(nbt: NBTTagCompound?) {
        super.readFromNBT(nbt)
        tankLevel = nbt?.getDouble("tankLevel") ?: 0.0
        on = nbt?.getBoolean("on") ?: false
    }

    override fun writeToNBT(nbt: NBTTagCompound?) {
        super.writeToNBT(nbt)
        nbt?.setDouble("tankLevel", tankLevel)
        nbt?.setBoolean("on", on)
    }

    override fun getWaila(): Map<String, String> = mutableMapOf(
        Pair(tr("State"), if (on) tr("ON") else tr("OFF")),
        Pair(tr("Fuel level"), Utils.plotPercent(tankLevel)),
        Pair(tr("Generated power"), Utils.plotPower(powerSource.effectiveP)),
        Pair(tr("Voltage"), Utils.plotVolt(powerSource.u))
    )
}

class FuelGeneratorRender(tileEntity: org.ja13.eau.node.transparent.TransparentNodeEntity, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor) :
    org.ja13.eau.node.transparent.TransparentNodeElementRender(tileEntity, descriptor) {
    internal var descriptor: FuelGeneratorDescriptor
    private var renderPreProcess: org.ja13.eau.cable.CableRenderType? = null
    private val eConn = LRDUMask()
    private var on = false
    private var voltageRatio = SlewLimiter(1.0)
    private val sound = object : LoopedSound("eln:FuelGenerator", coordonate(), ISound.AttenuationType.LINEAR) {
        override fun getVolume() = if (on) 0.2f else 0f
        override fun getPitch() = 0.75f + 1f * voltageRatio.position.toFloat()
    }

    init {
        this.descriptor = descriptor as FuelGeneratorDescriptor
        addLoopedSound(sound)
    }

    override fun draw() {
        renderPreProcess = drawCable(Direction.YN, EAU.smallInsulationMediumCurrentRender, eConn, renderPreProcess)
        front.glRotateZnRef()
        descriptor.draw(on)
    }

    override fun cameraDrawOptimisation(): Boolean = false

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        eConn.deserialize(stream)
        val on_ = stream.readBoolean()
        voltageRatio.target = stream.readDouble()
        if (on_ != on) {
            voltageRatio.position = voltageRatio.target
        }
        on = on_
        renderPreProcess = null
    }

    override fun refresh(deltaT: Double) {
        super.refresh(deltaT)
        voltageRatio.step(deltaT)
    }
}

class FuelGeneratorSlowProcess(internal val generator: FuelGeneratorElement) : org.ja13.eau.sim.IProcess {
    override fun process(time: Double) {
        if (generator.on) {
            val power = Math.max(generator.powerSource.effectiveP,
                generator.descriptor.nominalPower * FuelGeneratorDescriptor.MinimalLoadFractionOfNominalPower)
            generator.tankLevel = Math.max(0.0, generator.tankLevel - time *
                FuelGeneratorDescriptor.EfficiencyFactorVsLoadFactor(power / generator.descriptor.nominalPower) *
                power / generator.descriptor.tankEnergyCapacity)

            if (generator.tankLevel <= 0) {
                generator.on = false
            }

            if (generator.voltageGracePeriod > 0) {
                generator.voltageGracePeriod -= time
            } else if (generator.positiveLoad.u <
                FuelGeneratorDescriptor.GeneratorBailOutVoltageRatio * generator.descriptor.maxVoltage) {
                generator.on = false
            }
        }

        if (generator.on) {
            generator.powerSource.p = generator.descriptor.nominalPower
        } else {
            generator.powerSource.p = 0.0
        }
    }
}
