package org.ja13.eau.transparentnode.dcdc

import org.ja13.eau.EAU
import org.ja13.eau.cable.CableRenderDescriptor
import org.ja13.eau.cable.CableRenderType
import org.ja13.eau.generic.GenericItemBlockUsingDamageDescriptor
import org.ja13.eau.generic.GenericItemUsingDamageDescriptor
import org.ja13.eau.generic.GenericItemUsingDamageSlot
import org.ja13.eau.gui.GuiContainerEln
import org.ja13.eau.gui.GuiHelperContainer
import org.ja13.eau.gui.ISlotSkin.SlotSkin
import org.ja13.eau.i18n.I18N
import org.ja13.eau.item.CaseItemDescriptor
import org.ja13.eau.item.ConfigCopyToolDescriptor
import org.ja13.eau.item.CopperCableDescriptor
import org.ja13.eau.item.FerromagneticCoreDescriptor
import org.ja13.eau.item.IConfigurable
import org.ja13.eau.misc.BasicContainer
import org.ja13.eau.misc.Coordonate
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.LRDUMask
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.PhysicalInterpolator
import org.ja13.eau.misc.SlewLimiter
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.NodePeriodicPublishProcess
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeElement
import org.ja13.eau.node.transparent.TransparentNodeElementInventory
import org.ja13.eau.node.transparent.TransparentNodeElementRender
import org.ja13.eau.node.transparent.TransparentNodeEntity
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.mna.component.VoltageSource
import org.ja13.eau.sim.mna.process.TransformerInterSystemProcess
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog
import org.ja13.eau.sim.process.destruct.WorldExplosion
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor
import org.ja13.eau.sound.LoopedSound
import net.minecraft.client.audio.ISound
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.client.IItemRenderer
import org.lwjgl.opengl.GL11
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*

class DcDcDescriptor(name: String, objM: org.ja13.eau.misc.Obj3D, coreM: org.ja13.eau.misc.Obj3D, casingM: org.ja13.eau.misc.Obj3D, val minimalLoadToHum: Float):
    org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, DcDcElement::class.java, DcDcRender::class.java) {

    companion object {
        const val COIL_SCALE: Float = 4.0f
        const val COIL_SCALE_LIMIT: Int = 16
    }

    var main: org.ja13.eau.misc.Obj3D.Obj3DPart? = null
    var core: org.ja13.eau.misc.Obj3D.Obj3DPart? = null
    var coil: org.ja13.eau.misc.Obj3D.Obj3DPart? = null
    var casing: org.ja13.eau.misc.Obj3D.Obj3DPart? = null
    var casingLeftDoor: org.ja13.eau.misc.Obj3D.Obj3DPart? = null
    var casingRightDoor: org.ja13.eau.misc.Obj3D.Obj3DPart? = null

    init {
        main = objM.getPart("main")
        coil = objM.getPart("sbire")
        core = coreM.getPart("fero")
        casing = casingM.getPart("Case")
        casingLeftDoor = casingM.getPart("DoorL")
        casingRightDoor = casingM.getPart("DoorR")

        voltageTier = VoltageTier.NEUTRAL
    }

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)
        Collections.addAll(list, *org.ja13.eau.i18n.I18N.tr("Transforms an input voltage to\nan output voltage.")!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
    }

    override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType, item: ItemStack, helper: IItemRenderer.ItemRendererHelper): Boolean {
        return type != IItemRenderer.ItemRenderType.INVENTORY
    }

    override fun handleRenderType(item: ItemStack, type: IItemRenderer.ItemRenderType): Boolean {
        return true
    }

    override fun renderItem(type: IItemRenderer.ItemRenderType, item: ItemStack, vararg data: Any) {
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            super.renderItem(type, item, *data)
        } else {
            draw(core, 1, 4, false, 0f)
        }
    }

    internal fun draw(core: org.ja13.eau.misc.Obj3D.Obj3DPart?, priCableNbr: Int, secCableNbr: Int, hasCasing: Boolean, doorOpen: Float) {
        main?.draw()
        core?.draw()
        if (core != null) {
            if (priCableNbr != 0) {
                var scale = COIL_SCALE
                if (priCableNbr < COIL_SCALE_LIMIT) {
                    scale *= priCableNbr.toFloat() / COIL_SCALE_LIMIT
                }
                GL11.glPushMatrix()
                GL11.glScalef(1f, scale * 2f / (priCableNbr + 1), 1f)
                GL11.glTranslatef(0f, -0.125f * (priCableNbr - 1) / COIL_SCALE, 0f)
                for (idx in 0 until priCableNbr) {
                    coil?.draw()
                    GL11.glTranslatef(0f, 0.25f / COIL_SCALE, 0f)
                }
                GL11.glPopMatrix()
            }
            if (secCableNbr != 0) {
                var scale = COIL_SCALE
                if (secCableNbr < COIL_SCALE_LIMIT) {
                    scale *= secCableNbr.toFloat() / COIL_SCALE_LIMIT
                }
                GL11.glPushMatrix()
                GL11.glRotatef(180f, 0f, 1f, 0f)
                GL11.glScalef(1f, scale * 2f / (secCableNbr + 1), 1f)
                GL11.glTranslatef(0f, -0.125f * (secCableNbr - 1) / COIL_SCALE, 0f)
                for (idx in 0 until secCableNbr) {
                    coil?.draw()
                    GL11.glTranslatef(0f, 0.25f / COIL_SCALE, 0f)
                }
                GL11.glPopMatrix()
            }
        }

        if (hasCasing) {
            casing?.draw()
            casingLeftDoor?.draw(-doorOpen * 90, 0f, 1f, 0f)
            casingRightDoor?.draw(doorOpen * 90, 0f, 1f, 0f)
        }
    }
}

class DcDcElement(transparentNode: org.ja13.eau.node.transparent.TransparentNode, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElement(transparentNode, descriptor), org.ja13.eau.item.IConfigurable {
    val primaryLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("primaryLoad")
    val secondaryLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("secondaryLoad")

    val primaryVoltageSource = org.ja13.eau.sim.mna.component.VoltageSource("primaryVoltageSource")
    val secondaryVoltageSource = org.ja13.eau.sim.mna.component.VoltageSource("secondaryVoltageSource")

    val interSystemProcess = org.ja13.eau.sim.mna.process.TransformerInterSystemProcess(primaryLoad, secondaryLoad, primaryVoltageSource, secondaryVoltageSource)

    val inventory = org.ja13.eau.node.transparent.TransparentNodeElementInventory(4, 64, this)

    var primaryMaxCurrent = 0.0
    var secondaryMaxCurrent = 0.0

    val primaryVoltageWatchdog = org.ja13.eau.sim.process.destruct.VoltageStateWatchDog()
    val secondaryVoltageWatchdog = org.ja13.eau.sim.process.destruct.VoltageStateWatchDog()

    var populated = false

    var ratioControl = 1.0

    init {
        electricalLoadList.add(primaryLoad)
        electricalLoadList.add(secondaryLoad)
        electricalComponentList.add(primaryVoltageSource)
        electricalComponentList.add(secondaryVoltageSource)
        val exp = org.ja13.eau.sim.process.destruct.WorldExplosion(this).machineExplosion()
        slowProcessList.add(primaryVoltageWatchdog.set(primaryLoad).set(exp))
        slowProcessList.add(secondaryVoltageWatchdog.set(secondaryLoad).set(exp))
        slowProcessList.add(org.ja13.eau.node.NodePeriodicPublishProcess(node, 1.0, .5))
        slowProcessList.add(DcDcProcess(this))
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
        if (lrdu != LRDU.Down) return null
        return when (side) {
            front.right() -> primaryLoad
            front.left() -> secondaryLoad
            else -> null
        }
    }

    override fun getThermalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ThermalLoad? {
        return null
    }

    override fun getConnectionMask(side: Direction, lrdu: LRDU): Int {
        if (lrdu != LRDU.Down) return 0
        return when (side) {
            front.left() -> org.ja13.eau.node.NodeBase.MASK_ELECTRIC
            front.right() -> org.ja13.eau.node.NodeBase.MASK_ELECTRIC
            else -> 0
        }
    }

    override fun multiMeterString(side: Direction): String {
        return Utils.plotVolt(primaryLoad.u, "Primary Voltage:") +
            Utils.plotAmpere(primaryVoltageSource.current, "Primary Current:") +
            Utils.plotVolt(secondaryLoad.u, "Secondary Voltage:") +
            Utils.plotAmpere(secondaryVoltageSource.current, "Secondary Current:")
    }

    override fun thermoMeterString(side: Direction): String? {
        return null
    }

    override fun initialize() {
        primaryVoltageSource.connectTo(primaryLoad, null)
        secondaryVoltageSource.connectTo(secondaryLoad, null)
        electricalComponentList.add(primaryVoltageSource)
        electricalComponentList.add(secondaryVoltageSource)
        interSystemProcess.ratio = 1.0
        computeInventory()
        connect()
    }

    private fun computeInventory() {
        val primaryCable = inventory.getStackInSlot(DcDcContainer.primaryCableSlotId)
        val secondaryCable = inventory.getStackInSlot(DcDcContainer.secondaryCableSlotId)
        val core = inventory.getStackInSlot(DcDcContainer.ferromagneticSlotId)

        primaryVoltageWatchdog.setUNominal(3200.0)
        secondaryVoltageWatchdog.setUNominal(3200.0)

        primaryMaxCurrent = 5.0
        secondaryMaxCurrent = 5.0

        var coreFactor = 1.0
        if (core != null) {
            val coreDescriptor = GenericItemUsingDamageDescriptor.getDescriptor(core) as org.ja13.eau.item.FerromagneticCoreDescriptor
            coreFactor = coreDescriptor.cableMultiplicator
        }

        if (primaryCable == null || core == null || primaryCable.stackSize < 1) {
            primaryLoad.highImpedance()
            populated = false
        } else {
            primaryLoad.rs = coreFactor * 0.01
        }

        if (secondaryCable == null || core == null || secondaryCable.stackSize < 1) {
            secondaryLoad.highImpedance()
            populated = false
        } else {
            secondaryLoad.rs = coreFactor * 0.01
        }

        populated = primaryCable != null && secondaryCable != null && primaryCable.stackSize >= 1 && secondaryCable.stackSize >= 1 && core != null

        ratioControl = if (populated) {
            primaryCable.stackSize.toDouble() / secondaryCable.stackSize.toDouble()
        } else {
            1.0
        }
    }

    override fun inventoryChange(inventory: IInventory?) {
        disconnect()
        computeInventory()
        connect()
        needPublish()
    }

    override fun onBlockActivated(entityPlayer: EntityPlayer, side: Direction, vx: Float, vy: Float, vz: Float): Boolean {
        return false
    }

    override fun hasGui(): Boolean {
        return true
    }

    override fun newContainer(side: Direction, player: EntityPlayer): Container? {
        return DcDcContainer(player, inventory)
    }

    override fun getLightOpacity(): Float {
        return 1.0f
    }

    override fun getInventory(): IInventory? {
        return inventory
    }

    override fun onGroundedChangedByClient() {
        super.onGroundedChangedByClient()
        computeInventory()
        reconnect()
    }

    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        try {
            if (inventory.getStackInSlot(0) == null)
                stream.writeByte(0)
            else
                stream.writeByte(inventory.getStackInSlot(0).stackSize)
            if (inventory.getStackInSlot(1) == null)
                stream.writeByte(0)
            else
                stream.writeByte(inventory.getStackInSlot(1).stackSize)
            Utils.serialiseItemStack(stream, inventory.getStackInSlot(DcDcContainer.ferromagneticSlotId))
            Utils.serialiseItemStack(stream, inventory.getStackInSlot(DcDcContainer.primaryCableSlotId))
            Utils.serialiseItemStack(stream, inventory.getStackInSlot(DcDcContainer.secondaryCableSlotId))
            node.lrduCubeMask.getTranslate(front.down()).serialize(stream)
            var load = 0f
            if (primaryMaxCurrent != 0.0 && secondaryMaxCurrent != 0.0) {
                load = Utils.limit(Math.max(primaryLoad.i / primaryMaxCurrent,
                    secondaryLoad.i / secondaryMaxCurrent).toFloat(), 0f, 1f)
            }
            stream.writeFloat(load)
            stream.writeBoolean(inventory.getStackInSlot(3) != null)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun getWaila(): Map<String, String> {
        val info = HashMap<String, String>()
        info[org.ja13.eau.i18n.I18N.tr("Ratio")] = Utils.plotValue(interSystemProcess.ratio)
        if (org.ja13.eau.EAU.wailaEasyMode) {
            info["Voltages"] = "\u00A7a" + Utils.plotVolt(primaryLoad.u) + " " +
                "\u00A7e" + Utils.plotVolt(secondaryLoad.u)
        }
        try {
            val leftSubSystemSize = primaryLoad.subSystem.component.size
            val rightSubSystemSize = secondaryLoad.subSystem.component.size
            val textColorLeft = when {
                leftSubSystemSize <= 8 -> "§a"
                leftSubSystemSize <= 15 -> "§6"
                else -> "§c"
            }
            val textColorRight = when {
                rightSubSystemSize <= 8 -> "§a"
                rightSubSystemSize <= 15 -> "§6"
                else -> "§c"
            }
            info[org.ja13.eau.i18n.I18N.tr("Subsystem Matrix Size: ")] = "$textColorLeft$leftSubSystemSize §r| $textColorRight$rightSubSystemSize"
        } catch (e: Exception) {
            if (populated) {
                info[org.ja13.eau.i18n.I18N.tr("Subsystem Matrix Size: ")] = "§cNot part of a subsystem!?"
            } else {
                info[org.ja13.eau.i18n.I18N.tr("Subsystem Matrix Size: ")] = "Not part of a subsystem"
            }
        }
        return info
    }

    override fun readConfigTool(compound: NBTTagCompound, invoker: EntityPlayer) {
        if (compound.hasKey("isolator")) {
            disconnect()
            reconnect()
            needPublish()
        }
        if (org.ja13.eau.item.ConfigCopyToolDescriptor.readCableType(compound, "primary", inventory, DcDcContainer.primaryCableSlotId, invoker))
            inventoryChange(inventory)
        if (org.ja13.eau.item.ConfigCopyToolDescriptor.readCableType(compound, "secondary", inventory, DcDcContainer.secondaryCableSlotId, invoker))
            inventoryChange(inventory)
        if (org.ja13.eau.item.ConfigCopyToolDescriptor.readGenDescriptor(compound, "core", inventory, DcDcContainer.ferromagneticSlotId, invoker))
            inventoryChange(inventory)
    }

    override fun writeConfigTool(compound: NBTTagCompound, invoker: EntityPlayer) {
        org.ja13.eau.item.ConfigCopyToolDescriptor.writeCableType(compound, "primary", inventory.getStackInSlot(DcDcContainer.primaryCableSlotId))
        org.ja13.eau.item.ConfigCopyToolDescriptor.writeCableType(compound, "secondary", inventory.getStackInSlot(DcDcContainer.secondaryCableSlotId))
        org.ja13.eau.item.ConfigCopyToolDescriptor.writeGenDescriptor(compound, "core", inventory.getStackInSlot(DcDcContainer.ferromagneticSlotId))
    }
}

class DcDcProcess(val element: DcDcElement): org.ja13.eau.sim.IProcess {

    companion object {
        const val MAX_RATIO = 16.0
        const val MIN_RATIO = 1.0 / 16.0
    }

    override fun process(time: Double) {
        val ratio = when {
            element.ratioControl > MAX_RATIO -> MAX_RATIO
            element.ratioControl < MIN_RATIO -> MIN_RATIO
            else -> element.ratioControl
        }
        if (ratio.isFinite()) {
            element.interSystemProcess.ratio = ratio
        } else {
            element.interSystemProcess.ratio = 1.0
        }
    }
}

class DcDcRender(tileEntity: org.ja13.eau.node.transparent.TransparentNodeEntity, val descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElementRender(tileEntity, descriptor) {

    val inventory = org.ja13.eau.node.transparent.TransparentNodeElementInventory(4, 64, this)

    val load = SlewLimiter(0.5)

    var primaryStackSize: Byte = 0
    var secondaryStackSize: Byte = 0
    var priRender: org.ja13.eau.cable.CableRenderDescriptor? = null
    var secRender: org.ja13.eau.cable.CableRenderDescriptor? = null

    private var feroPart: org.ja13.eau.misc.Obj3D.Obj3DPart? = null
    private var hasCasing = false

    private val coordinate: Coordonate
    private val doorOpen: PhysicalInterpolator

    private val priConn = LRDUMask()
    private val secConn = LRDUMask()
    private val controlConn = LRDUMask()
    private val eConn = LRDUMask()
    private var cableRenderType: org.ja13.eau.cable.CableRenderType? = null

    init {
        addLoopedSound(object : LoopedSound("eln:Transformer", coordonate(), ISound.AttenuationType.LINEAR) {
            override fun getVolume(): Float {
                return if (load.position > (descriptor as DcDcDescriptor).minimalLoadToHum)
                    (0.1f * (load.position - descriptor.minimalLoadToHum) / (1 - descriptor.minimalLoadToHum)).toFloat()
                else
                    0f
            }
        })

        coordinate = Coordonate(tileEntity)
        doorOpen = PhysicalInterpolator(0.4, 4.0, 0.9, 0.05)
    }

    override fun draw() {
        GL11.glPushMatrix()
        front.glRotateXnRef()
        (descriptor as DcDcDescriptor).draw(feroPart, primaryStackSize.toInt(), secondaryStackSize.toInt(), hasCasing, doorOpen.get().toFloat())
        GL11.glPopMatrix()
        cableRenderType = drawCable(front.down(), priRender, priConn, cableRenderType)
        cableRenderType = drawCable(front.down(), secRender, secConn, cableRenderType)
    }

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        try {
            primaryStackSize = stream.readByte()
            secondaryStackSize = stream.readByte()
            val feroStack = Utils.unserializeItemStack(stream)
            if (feroStack != null) {
                val feroDesc: GenericItemUsingDamageDescriptor? = GenericItemUsingDamageDescriptor.getDescriptor(feroStack, org.ja13.eau.item.FerromagneticCoreDescriptor::class.java)
                if (feroDesc != null)
                    feroPart = (feroDesc as org.ja13.eau.item.FerromagneticCoreDescriptor).feroPart
            }
            val priStack = Utils.unserializeItemStack(stream)
            if (priStack != null) {
                val priDesc: GenericItemBlockUsingDamageDescriptor? = GenericItemBlockUsingDamageDescriptor.getDescriptor(priStack, org.ja13.eau.sixnode.genericcable.GenericCableDescriptor::class.java)
                if (priDesc != null)
                    priRender = (priDesc as org.ja13.eau.sixnode.genericcable.GenericCableDescriptor).render
            }

            val secStack = Utils.unserializeItemStack(stream)
            if (secStack != null) {
                val secDesc: GenericItemBlockUsingDamageDescriptor? = GenericItemBlockUsingDamageDescriptor.getDescriptor(secStack, org.ja13.eau.sixnode.genericcable.GenericCableDescriptor::class.java)
                if (secDesc != null)
                    secRender = (secDesc as org.ja13.eau.sixnode.genericcable.GenericCableDescriptor).render
            }

            eConn.deserialize(stream)

            priConn.mask = 0
            secConn.mask = 0
            for (lrdu in LRDU.values()) {
                if(!eConn.get(lrdu)) continue
                if(front.down().applyLRDU(lrdu) == front.left()) {
                    priConn.set(lrdu, true)
                    continue
                }
                if(front.down().applyLRDU(lrdu) == front.right()) {
                    secConn.set(lrdu, true)
                    continue
                }
                controlConn.set(lrdu, true)
            }
            cableRenderType = null

            load.target = stream.readDouble()
            hasCasing = stream.readBoolean()

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun getCableRender(side: Direction?, lrdu: LRDU): org.ja13.eau.cable.CableRenderDescriptor? {
        if (lrdu == LRDU.Down) {
            if (side == front.left()) return priRender
            if (side == front.right()) return secRender
            if (side == front && !grounded) return priRender
            if (side == front.back() && !grounded) return secRender
        }
        return null
    }

    override fun notifyNeighborSpawn() {
        super.notifyNeighborSpawn()
        cableRenderType = null
    }

    override fun refresh(deltaT: Double) {
        super.refresh(deltaT)
        load.step(deltaT)

        if (hasCasing) {
            if (!Utils.isPlayerAround(tileEntity.worldObj, coordinate.moved(front).getAxisAlignedBB(0)))
                doorOpen.target = 0.0
            else
                doorOpen.target = 1.0
            doorOpen.step(deltaT)
        }
    }

    override fun newGuiDraw(side: Direction, player: EntityPlayer): GuiScreen? {
        return DcDcGui(player, inventory, this)
    }
}

class DcDcGui(player: EntityPlayer, inventory: IInventory, val render: DcDcRender): org.ja13.eau.gui.GuiContainerEln(DcDcContainer(player, inventory)) {
    override fun newHelper(): org.ja13.eau.gui.GuiHelperContainer {
        return org.ja13.eau.gui.GuiHelperContainer(this, 176, 194 - 33 + 20, 8, 84 + 194 - 166 - 33 + 20, "transformer.png")
    }
}

class DcDcContainer(player: EntityPlayer, inventory: IInventory) : org.ja13.eau.misc.BasicContainer(player, inventory,
    arrayOf(
            org.ja13.eau.generic.GenericItemUsingDamageSlot(inventory, primaryCableSlotId, 58, 30, 16,
                    arrayOf<Class<*>>(org.ja13.eau.item.CopperCableDescriptor::class.java),
                    SlotSkin.medium, arrayOf(org.ja13.eau.i18n.I18N.tr("Copper cable slot"))),
            org.ja13.eau.generic.GenericItemUsingDamageSlot(inventory, secondaryCableSlotId, 100, 30, 16,
                    arrayOf<Class<*>>(org.ja13.eau.item.CopperCableDescriptor::class.java),
                    SlotSkin.medium, arrayOf(org.ja13.eau.i18n.I18N.tr("Copper cable slot"))),
            org.ja13.eau.generic.GenericItemUsingDamageSlot(inventory, ferromagneticSlotId, 58 + (100 - 58) / 2, 30, 1,
                    arrayOf<Class<*>>(org.ja13.eau.item.FerromagneticCoreDescriptor::class.java),
                    SlotSkin.medium, arrayOf(org.ja13.eau.i18n.I18N.tr("Ferromagnetic core slot"))),
            org.ja13.eau.generic.GenericItemUsingDamageSlot(inventory, CasingSlotId, 130, 74, 1,
                    arrayOf<Class<*>>(CaseItemDescriptor::class.java),
                    SlotSkin.medium, arrayOf(org.ja13.eau.i18n.I18N.tr("Casing slot")))))
    {
    companion object {
        const val primaryCableSlotId = 0
        const val secondaryCableSlotId = 1
        const val ferromagneticSlotId = 2
        const val CasingSlotId = 3
    }
}
