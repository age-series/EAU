package org.ja13.eau.sixnode

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.client.IItemRenderer
import org.ja13.eau.generic.GenericItemUsingDamageDescriptor
import org.ja13.eau.item.ElectricalFuseDescriptor
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.misc.VoltageTierHelpers
import org.lwjgl.opengl.GL11
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

class ElectricalFuseHolderDescriptor(name: String, obj: org.ja13.eau.misc.Obj3D) :
    org.ja13.eau.node.six.SixNodeDescriptor(name, ElectricalFuseHolderElement::class.java, ElectricalFuseHolderRender::class.java) {
    private val case = obj.getPart("Case")
    private val fuse = obj.getPart("Fuse")
    private val fuseType = obj.getPart("FuseType")
    private val fuseOk = obj.getPart("FuseOk")

    init {
        voltageTier = VoltageTier.NEUTRAL
    }

    override fun handleRenderType(item: ItemStack?, type: IItemRenderer.ItemRenderType?) = true

    override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType?, item: ItemStack?,
                                       helper: IItemRenderer.ItemRendererHelper?) =
        type != IItemRenderer.ItemRenderType.INVENTORY

    override fun shouldUseRenderHelperEln(type: IItemRenderer.ItemRenderType?, item: ItemStack?,
                                          helper: IItemRenderer.ItemRendererHelper?) =
        type != IItemRenderer.ItemRenderType.INVENTORY

    override fun renderItem(type: IItemRenderer.ItemRenderType?, item: ItemStack?, vararg data: Any?) {
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            super.renderItem(type, item, *data)
        } else {
            draw(null)
        }
    }

    fun draw(installedFuse: ElectricalFuseDescriptor?) {
        case?.draw()
        if (installedFuse != null) {
            VoltageTierHelpers.setGLColor(VoltageTier.NEUTRAL)
            fuseType?.draw()
            GL11.glColor3f(1f, 1f, 1f)
            if (installedFuse.cableDescriptor != null) {
                fuseOk?.draw()
            }
            fuse?.draw()
        }
    }

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)
        org.ja13.eau.i18n.I18N.tr("Protects electrical components.\nFuse melts if current exceeds the\nfuse limit").split("\n").forEach { list.add(it) }
    }

    override fun getFrontFromPlace(side: Direction, player: EntityPlayer) =
        super.getFrontFromPlace(side, player).inverse()
}

class ElectricalFuseHolderElement(sixNode: org.ja13.eau.node.six.SixNode, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor) :
    org.ja13.eau.node.six.SixNodeElement(sixNode, side, descriptor) {
    private val aLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("aLoad")
    private val bLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("bLoad")
    private val fuseResistor = org.ja13.eau.sim.mna.component.Resistor(aLoad, bLoad)

    var installedFuse: ElectricalFuseDescriptor? = null
        set(value) {
            if (value == field) return
            field = value
            refreshSwitchResistor()
            needPublish()
        }

    private var T = 0.0

    private val fuseProcess = org.ja13.eau.sim.IProcess { time ->
        val I = aLoad.current
        val cable = installedFuse?.cableDescriptor
        if (cable == null) {
            T = 0.0
        } else {
            val P = I * I * cable.electricalRs * 2.0 - T / cable.thermalRp * 0.9

            T += P / cable.thermalC * time
        }
        if (T > cable?.thermalWarmLimit ?: 0.0 * 0.8) {
            installedFuse = ElectricalFuseDescriptor.BlownFuse
        }
    }

    init {
        electricalLoadList.add(aLoad)
        electricalLoadList.add(bLoad)
        electricalComponentList.add(fuseResistor)
        electricalComponentList.add(org.ja13.eau.sim.mna.component.Resistor(bLoad, null).pullDown())
        electricalComponentList.add(org.ja13.eau.sim.mna.component.Resistor(aLoad, null).pullDown())
        electricalProcessList.add(fuseProcess)
    }

    override fun readFromNBT(nbt: NBTTagCompound?) {
        super.readFromNBT(nbt)
        if (nbt != null) {
            front = LRDU.readFromNBT(nbt, "front")

            val fuseCompound = nbt.getTag("fuse") as? NBTTagCompound
            if (fuseCompound != null) {
                val fuseStack = ItemStack.loadItemStackFromNBT(fuseCompound)
                if (fuseStack != null) {
                    installedFuse = GenericItemUsingDamageDescriptor.getDescriptor(fuseStack) as? ElectricalFuseDescriptor
                }
            }

            T = nbt.getDouble("T")
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound?) {
        super.writeToNBT(nbt)
        if (nbt != null) {
            front.writeToNBT(nbt, "front")

            if (installedFuse != null) {
                val fuseCompaound = NBTTagCompound()
                installedFuse!!.newItemStack().writeToNBT(fuseCompaound)
                nbt.setTag("fuse", fuseCompaound)
            }

            nbt.setDouble("T", T)
        }
    }

    override fun getElectricalLoad(lrdu: LRDU, mask: Int): org.ja13.eau.sim.ElectricalLoad? = when (lrdu) {
        front -> aLoad
        front.inverse() -> bLoad
        else -> null
    }

    override fun getThermalLoad(lrdu: LRDU, mask: Int): org.ja13.eau.sim.ThermalLoad? = null

    override fun getConnectionMask(lrdu: LRDU?): Int = when (lrdu) {
        front -> org.ja13.eau.node.NodeBase.maskElectricalAll
        front.inverse() -> org.ja13.eau.node.NodeBase.maskElectricalAll
        else -> 0
    }

    override fun multiMeterString() = Utils.plotAmpere(Math.abs(aLoad.current))

    override fun getWaila(): MutableMap<String, String> {
        return mutableMapOf(Pair(org.ja13.eau.i18n.I18N.tr("Current"), Utils.plotAmpere(Math.abs(aLoad.current))))
    }

    override fun thermoMeterString(): String? = null

    override fun networkSerialize(stream: DataOutputStream?) {
        super.networkSerialize(stream)
        if (stream != null) {
            try {
                Utils.serialiseItemStack(stream, installedFuse?.newItemStack())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun refreshSwitchResistor() {
        installedFuse?.cableDescriptor?.applyTo(fuseResistor) ?: fuseResistor.ultraImpedance()
    }

    override fun initialize() {
        computeElectricalLoad()
    }

    fun computeElectricalLoad() {
        org.ja13.eau.EAU.uninsulatedHighCurrentCopperCable.applyTo(aLoad)
        org.ja13.eau.EAU.uninsulatedHighCurrentCopperCable.applyTo(bLoad)
        refreshSwitchResistor()
    }

    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float, vz: Float): Boolean {
        if (onBlockActivatedRotate(entityPlayer)) return true

        var takenOutFuse: ElectricalFuseDescriptor? = null
        val itemStack = entityPlayer?.currentEquippedItem
        val fuseDescriptor = itemStack?.let { GenericItemUsingDamageDescriptor.getDescriptor(it) } as? ElectricalFuseDescriptor
        if (itemStack != null) {
            if (fuseDescriptor != null && itemStack.stackSize > 0) {
                // The player puts in a new lead fuse.
                itemStack.stackSize--
                takenOutFuse = installedFuse
                installedFuse = fuseDescriptor
            }
        } else {
            // The player takes out the fuse and does not install a new one at all.
            takenOutFuse = installedFuse
            installedFuse = null
        }

        // What do we do with the fuse just taken out?
        takenOutFuse?.let {
            sixNode.dropItem(it.newItemStack())
        }

        return takenOutFuse != null || fuseDescriptor != null
    }
}

class ElectricalFuseHolderRender(tileEntity: org.ja13.eau.node.six.SixNodeEntity, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor) :
    org.ja13.eau.node.six.SixNodeElementRender(tileEntity, side, descriptor) {
    private val descriptor = descriptor as ElectricalFuseHolderDescriptor
    private var installedFuse: ElectricalFuseDescriptor? = null

    override fun draw() {
        front.right().glRotateOnX()
        descriptor.draw(installedFuse)
    }

    override fun publishUnserialize(stream: DataInputStream?) {
        super.publishUnserialize(stream)
        if (stream != null) {
            installedFuse = GenericItemUsingDamageDescriptor.getDescriptor(Utils.unserializeItemStack(stream)) as? ElectricalFuseDescriptor
        }
    }
}
