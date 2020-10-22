package org.ja13.eau.sixnode

import org.ja13.eau.EAU
import org.ja13.eau.cable.CableRenderDescriptor
import org.ja13.eau.i18n.I18N.tr
import org.ja13.eau.misc.*
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.six.*
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.nbt.NbtElectricalGateOutput
import org.ja13.eau.sim.nbt.NbtElectricalGateOutputProcess
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.IFluidHandler
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * A comparator-alike. It doesn't "compare" anything, though.
 */
class ScannerDescriptor(name: String, obj: org.ja13.eau.misc.Obj3D) : org.ja13.eau.node.six.SixNodeDescriptor(name, ScannerElement::class.java, ScannerRender::class.java) {

    val main = obj.getPart("main")!!
    val leds = arrayOf("LED_0", "LED_1").map { obj.getPart(it) }.requireNoNulls()

    init {
        voltageTier = VoltageTier.TTL
    }

    fun draw(mode: ScanMode) {
        main.draw()
        leds[mode.value.toInt()].draw()
    }

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)
        list.add(tr("Scans blocks to produce signals."))
        list.add(tr("- For tanks, outputs fill percentage."))
        // This string sucks. I can't use the normal Java method to fix this problem. TODO: fix this so that it is readable on windowed games.
        list.add(tr("- For inventories, outputs either total fill or fraction of slots with any items."))
        list.add(tr("Right-click to change mode."))
        list.add(tr("Otherwise behaves as a vanilla comparator."))
    }
}

enum class ScanMode(val value: Byte) {
    SIMPLE(0), SLOTS(1);

    companion object {
        private val map = ScanMode.values().associateBy(ScanMode::value)
        fun fromByte(type: Byte) = map[type]
    }
}

class ScannerElement(sixNode: org.ja13.eau.node.six.SixNode, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor) : org.ja13.eau.node.six.SixNodeElement(sixNode, side, descriptor) {
    val output = org.ja13.eau.sim.nbt.NbtElectricalGateOutput("signal")
    val outputProcess = org.ja13.eau.sim.nbt.NbtElectricalGateOutputProcess("signalP", output)

    var mode = ScanMode.SIMPLE

    val updater = org.ja13.eau.sim.IProcess {
        val appliedLRDU = side.applyLRDU(front)
        val scannedCoord = Coordonate(coordonate).apply {
            move(appliedLRDU)
        }
        val targetSide: ForgeDirection = appliedLRDU.inverse.toForge()
        val te = scannedCoord.tileEntity
        // TODO: Throttling.
        var out: Double? = null
        if (te != null) {
            out = scanTileEntity(te, targetSide)
        }
        if (out == null) {
            out = scanBlock(scannedCoord, targetSide)
        }
        outputProcess.outputNormalized = out
    }

    init {
        electricalLoadList.add(output)
        electricalComponentList.add(outputProcess)
        slowProcessList.add(updater)
    }

    private fun scanBlock(scannedCoord: Coordonate, targetSide: ForgeDirection): Double {
        val block = scannedCoord.block
        if (block.hasComparatorInputOverride()) {
            return block.getComparatorInputOverride(scannedCoord.world(), scannedCoord.x, scannedCoord.y, scannedCoord.z, targetSide.ordinal) / 15.0
        } else if (block.isOpaqueCube) {
            return 1.0
        } else if (block.isAir(scannedCoord.world(), scannedCoord.x, scannedCoord.y, scannedCoord.z)) {
            return 0.0
        } else {
            return 1.0/3.0
        }
    }

    private fun scanTileEntity(te: TileEntity, targetSide: ForgeDirection): Double? {
        if (te is IFluidHandler) {
            val info = te.getTankInfo(targetSide)
            return info.sumByDouble {
                (it.fluid?.amount ?: 0).toDouble() / it.capacity
            } / info.size
        } else if (te is ISidedInventory) {
            var sum = 0
            var limit = 0
            val slots = te.getAccessibleSlotsFromSide(targetSide.ordinal)
            when (mode) {
                ScanMode.SIMPLE -> slots.forEach {
                        sum += te.getStackInSlot(it)?.stackSize ?: 0
                        limit += te.inventoryStackLimit
                    }

                ScanMode.SLOTS -> slots.forEach {
                    sum += if ((te.getStackInSlot(it)?.stackSize ?: 0) > 0) 1 else 0
                    limit += 1
                }
            }
            return sum.toDouble() / limit
        } else if (te is IInventory) {
            val sum = when (mode) {
                ScanMode.SIMPLE -> (0..te.sizeInventory - 1).sumBy {
                    te.getStackInSlot(it)?.stackSize ?: 0
                }.toDouble()

                ScanMode.SLOTS -> (0..te.sizeInventory - 1).count {
                    (te.getStackInSlot(it)?.stackSize ?: 0) > 0
                }.toDouble() * te.inventoryStackLimit
            }
            return sum / te.inventoryStackLimit / te.sizeInventory
        } else {
            return null
        }
    }

    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float, vz: Float): Boolean {
        if (onBlockActivatedRotate(entityPlayer)) return true
        if (entityPlayer.isHoldingMeter()) return false
        mode = when (mode) {
            ScanMode.SIMPLE -> ScanMode.SLOTS
            ScanMode.SLOTS -> ScanMode.SIMPLE
        }
        needPublish()
        return true
    }

    override fun getElectricalLoad(lrdu: LRDU, mask: Int): org.ja13.eau.sim.ElectricalLoad? = output
    override fun getThermalLoad(lrdu: LRDU, mask: Int): org.ja13.eau.sim.ThermalLoad? = null

    override fun getConnectionMask(lrdu: LRDU) = when (lrdu) {
        front.inverse() -> org.ja13.eau.node.NodeBase.MASK_ELECTRIC
        else -> 0
    }

    override fun multiMeterString(): String {
        return "Mode: ${tr(mode.name.toLowerCase().capitalize())}, Value: ${Utils.plotPercent(outputProcess.outputNormalized)}"
    }

    override fun thermoMeterString() = ""

    override fun initialize() {
    }

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        stream.writeByte(mode.value.toInt())
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        super.writeToNBT(nbt)
        nbt.setByte("mode", mode.value)
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        mode = ScanMode.fromByte(nbt.getByte("mode"))!!
    }
}


class ScannerRender(entity: org.ja13.eau.node.six.SixNodeEntity, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor) : org.ja13.eau.node.six.SixNodeElementRender(entity, side, descriptor) {
    val desc = descriptor as ScannerDescriptor
    var mode = ScanMode.SIMPLE

    override fun draw() {
        super.draw()
        front.glRotateOnX()
        desc.draw(mode)
    }

    override fun publishUnserialize(stream: DataInputStream) {
        super.publishUnserialize(stream)
        mode = ScanMode.fromByte(stream.readByte())!!
    }

    override fun getCableRender(lrdu: LRDU?): org.ja13.eau.cable.CableRenderDescriptor? = org.ja13.eau.EAU.smallInsulationLowCurrentRender
}
