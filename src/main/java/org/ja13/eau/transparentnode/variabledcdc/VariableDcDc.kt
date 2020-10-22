package org.ja13.eau.transparentnode.variabledcdc

import org.ja13.eau.EAU
import org.ja13.eau.cable.CableRenderDescriptor
import org.ja13.eau.cable.CableRenderType
import org.ja13.eau.generic.GenericItemBlockUsingDamageDescriptor
import org.ja13.eau.generic.GenericItemUsingDamageDescriptor
import org.ja13.eau.generic.GenericItemUsingDamageSlot
import org.ja13.eau.gui.GuiContainerEln
import org.ja13.eau.gui.GuiHelperContainer
import org.ja13.eau.gui.ISlotSkin
import org.ja13.eau.i18n.I18N.tr
import org.ja13.eau.item.*
import org.ja13.eau.misc.*
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.NodePeriodicPublishProcess
import org.ja13.eau.node.transparent.*
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.mna.component.VoltageSource
import org.ja13.eau.sim.mna.process.TransformerInterSystemProcess
import org.ja13.eau.sim.nbt.NbtElectricalGateInput
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

class VariableDcDcDescriptor(name: String, objM: org.ja13.eau.misc.Obj3D, coreM: org.ja13.eau.misc.Obj3D, casingM: org.ja13.eau.misc.Obj3D): org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, VariableDcDcElement::class.java, VariableDcDcRender::class.java) {
    companion object {
        val MIN_LOAD_HUM = 0.5
        val COIL_SCALE: Float = 4.0f
        val COIL_SCALE_LIMIT: Int = 16
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
        Collections.addAll(list, *tr("Transforms an input voltage to\nan output voltage.")!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        Collections.addAll(list, *tr("The output voltage is controlled\nfrom a signal input")!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
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

class VariableDcDcElement(transparentNode: org.ja13.eau.node.transparent.TransparentNode, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElement(transparentNode, descriptor), org.ja13.eau.item.IConfigurable {
    val primaryLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("primaryLoad")
    val secondaryLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("secondaryLoad")

    val control = org.ja13.eau.sim.nbt.NbtElectricalGateInput("control")

    val primaryVoltageSource = org.ja13.eau.sim.mna.component.VoltageSource("primaryVoltageSource")
    val secondaryVoltageSource = org.ja13.eau.sim.mna.component.VoltageSource("secondaryVoltageSource")

    val interSystemProcess = org.ja13.eau.sim.mna.process.TransformerInterSystemProcess(primaryLoad, secondaryLoad, primaryVoltageSource, secondaryVoltageSource)

    val inventory = org.ja13.eau.node.transparent.TransparentNodeElementInventory(4, 64, this)

    var primaryMaxCurrent = 0.0
    var secondaryMaxCurrent = 0.0

    val primaryVoltageWatchdog = org.ja13.eau.sim.process.destruct.VoltageStateWatchDog()
    val secondaryVoltageWatchdog = org.ja13.eau.sim.process.destruct.VoltageStateWatchDog()

    var populated = false

    init {
        electricalLoadList.add(primaryLoad)
        electricalLoadList.add(secondaryLoad)
        electricalLoadList.add(control)
        electricalComponentList.add(primaryVoltageSource)
        electricalComponentList.add(secondaryVoltageSource)
        val exp = org.ja13.eau.sim.process.destruct.WorldExplosion(this).machineExplosion()
        slowProcessList.add(primaryVoltageWatchdog.set(primaryLoad).set(exp))
        slowProcessList.add(secondaryVoltageWatchdog.set(secondaryLoad).set(exp))
        slowProcessList.add(org.ja13.eau.node.NodePeriodicPublishProcess(node, 1.0, .5))
        slowProcessList.add(VariableDcDcProcess(this))
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
            front.right() -> secondaryLoad
            front.left() -> primaryLoad
            front -> control
            front.back() -> control
            else -> null
        }
    }

    override fun getThermalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ThermalLoad? {
        return null
    }

    override fun getConnectionMask(side: Direction, lrdu: LRDU): Int {
        if (lrdu != LRDU.Down) return 0
        return when (side) {
            front -> org.ja13.eau.node.NodeBase.maskElectricalGate
            front.back() -> org.ja13.eau.node.NodeBase.maskElectricalGate
            else -> org.ja13.eau.node.NodeBase.MASK_ELECTRIC
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
        val primaryCable = inventory.getStackInSlot(VariableDcDcContainer.primaryCableSlotId)
        val secondaryCable = inventory.getStackInSlot(VariableDcDcContainer.secondaryCableSlotId)
        val core = inventory.getStackInSlot(VariableDcDcContainer.ferromagneticSlotId)

        primaryVoltageWatchdog.setUNominal(3200.0)
        secondaryVoltageWatchdog.setUNominal(3200.0)

        primaryMaxCurrent = 5.0
        secondaryMaxCurrent = 5.0

        var coreFactor = 1.0
        if (core != null) {
            val coreDescriptor = GenericItemUsingDamageDescriptor.getDescriptor(core) as org.ja13.eau.item.FerromagneticCoreDescriptor
            coreFactor = coreDescriptor.cableMultiplicator
        }

        if (primaryCable == null || core == null || primaryCable.stackSize < 4) {
            primaryLoad.highImpedance()
            populated = false
        } else {
            primaryLoad.rs = coreFactor * 0.01
        }

        if (secondaryCable == null || core == null || secondaryCable.stackSize < 4) {
            secondaryLoad.highImpedance()
            populated = false
        } else {
            secondaryLoad.rs = coreFactor * 0.01
        }

        populated = primaryCable != null && secondaryCable != null && primaryCable.stackSize >= 4 && secondaryCable.stackSize >= 4 && core != null
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
        return VariableDcDcContainer(player, inventory)
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
            Utils.serialiseItemStack(stream, inventory.getStackInSlot(VariableDcDcContainer.ferromagneticSlotId))
            Utils.serialiseItemStack(stream, inventory.getStackInSlot(VariableDcDcContainer.primaryCableSlotId))
            Utils.serialiseItemStack(stream, inventory.getStackInSlot(VariableDcDcContainer.secondaryCableSlotId))
            node.lrduCubeMask.getTranslate(front.down()).serialize(stream)
            var load = 0.0
            if (primaryMaxCurrent != 0.0 && secondaryMaxCurrent != 0.0) {
                load = Utils.limit(Math.max(primaryLoad.i / primaryMaxCurrent,
                    secondaryLoad.i / secondaryMaxCurrent), 0.0, 1.0)
            }
            stream.writeDouble(load)
            stream.writeBoolean(inventory.getStackInSlot(3) != null)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun getWaila(): Map<String, String> {
        val info = HashMap<String, String>()
        info[tr("Ratio")] = Utils.plotValue(interSystemProcess.ratio)
        // It's just not fair not to show the voltages on the VDC/DC. It's so variable...
        info["Voltages"] = "\u00A7a" + Utils.plotVolt(primaryLoad.u) + " " +
            "\u00A7e" + Utils.plotVolt(secondaryLoad.u)
        info["Control Voltage"] = Utils.plotVolt(control.u)
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
            info[tr("Subsystem Matrix Size: ")] = "$textColorLeft$leftSubSystemSize §r| $textColorRight$rightSubSystemSize"
        } catch (e: Exception) {
            if (populated) {
                info[tr("Subsystem Matrix Size: ")] = "§cNot part of a subsystem!?"
            } else {
                info[tr("Subsystem Matrix Size: ")] = "Not part of a subsystem"
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
        if (org.ja13.eau.item.ConfigCopyToolDescriptor.readCableType(compound, "primary", inventory, VariableDcDcContainer.primaryCableSlotId, invoker))
            inventoryChange(inventory)
        if (org.ja13.eau.item.ConfigCopyToolDescriptor.readCableType(compound, "secondary", inventory, VariableDcDcContainer.secondaryCableSlotId, invoker))
            inventoryChange(inventory)
        if (org.ja13.eau.item.ConfigCopyToolDescriptor.readGenDescriptor(compound, "core", inventory, VariableDcDcContainer.ferromagneticSlotId, invoker))
            inventoryChange(inventory)
    }

    override fun writeConfigTool(compound: NBTTagCompound, invoker: EntityPlayer) {
        org.ja13.eau.item.ConfigCopyToolDescriptor.writeCableType(compound, "primary", inventory.getStackInSlot(VariableDcDcContainer.primaryCableSlotId))
        org.ja13.eau.item.ConfigCopyToolDescriptor.writeCableType(compound, "secondary", inventory.getStackInSlot(VariableDcDcContainer.secondaryCableSlotId))
        org.ja13.eau.item.ConfigCopyToolDescriptor.writeGenDescriptor(compound, "core", inventory.getStackInSlot(VariableDcDcContainer.ferromagneticSlotId))
    }
}

class VariableDcDcProcess(val element: VariableDcDcElement): org.ja13.eau.sim.IProcess {
    override fun process(time: Double) {
        val ratio = when {
            element.control.normalized > 0.9 -> 0.9
            element.control.normalized < 0.0 -> 0.0
            else -> element.control.normalized
        }
        if (ratio.isFinite()) {
            element.interSystemProcess.ratio = 1-ratio
        } else {
            element.interSystemProcess.ratio = 1.0
        }
    }
}

class VariableDcDcRender(tileEntity: org.ja13.eau.node.transparent.TransparentNodeEntity, val descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElementRender(tileEntity, descriptor) {

    val inventory = org.ja13.eau.node.transparent.TransparentNodeElementInventory(4, 64, this)

    val load = SlewLimiter(0.5)

    var primaryStackSize: Byte = 0
    var secondaryStackSize: Byte = 0
    var priRender: org.ja13.eau.cable.CableRenderDescriptor? = null
    var secRender: org.ja13.eau.cable.CableRenderDescriptor? = null
    var controlRender: org.ja13.eau.cable.CableRenderDescriptor? = null

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
                return if (load.position > VariableDcDcDescriptor.MIN_LOAD_HUM)
                    0.1f * (load.position - VariableDcDcDescriptor.MIN_LOAD_HUM).toFloat() / (1 - VariableDcDcDescriptor.MIN_LOAD_HUM).toFloat()
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
        (descriptor as VariableDcDcDescriptor).draw(feroPart, primaryStackSize.toInt(), secondaryStackSize.toInt(), hasCasing, doorOpen.get().toFloat())
        GL11.glPopMatrix()
        cableRenderType = drawCable(front.down(), priRender, priConn, cableRenderType)
        cableRenderType = drawCable(front.down(), secRender, secConn, cableRenderType)
        cableRenderType = drawCable(front.down(), org.ja13.eau.EAU.smallInsulationLowCurrentRender, controlConn, cableRenderType)
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
            controlConn.mask = 0
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
        return VariableDcDcGui(player, inventory, this)
    }
}

class VariableDcDcGui(player: EntityPlayer, inventory: IInventory, val render: VariableDcDcRender): org.ja13.eau.gui.GuiContainerEln(VariableDcDcContainer(player, inventory)) {
    override fun newHelper(): org.ja13.eau.gui.GuiHelperContainer {
        return org.ja13.eau.gui.GuiHelperContainer(this, 176, 194 - 33 + 20, 8, 84 + 194 - 166 - 33 + 20, "vdcdc.png")
    }
}

class VariableDcDcContainer(player: EntityPlayer, inventory: IInventory) : org.ja13.eau.misc.BasicContainer(player, inventory,
    arrayOf(
            org.ja13.eau.generic.GenericItemUsingDamageSlot(inventory, primaryCableSlotId, 58, 30, 4,
                    arrayOf<Class<*>>(org.ja13.eau.item.CopperCableDescriptor::class.java),
                    org.ja13.eau.gui.ISlotSkin.SlotSkin.medium, arrayOf(tr("Copper cable slot"), tr("4 Copper Cables Required"))),
            org.ja13.eau.generic.GenericItemUsingDamageSlot(inventory, secondaryCableSlotId, 100, 30, 4,
                    arrayOf<Class<*>>(org.ja13.eau.item.CopperCableDescriptor::class.java),
                    org.ja13.eau.gui.ISlotSkin.SlotSkin.medium, arrayOf(tr("Copper cable slot"), tr("4 Copper Cables Required"))),
            org.ja13.eau.generic.GenericItemUsingDamageSlot(inventory, ferromagneticSlotId, 58 + (100 - 58) / 2, 30, 1,
                    arrayOf<Class<*>>(org.ja13.eau.item.FerromagneticCoreDescriptor::class.java),
                    org.ja13.eau.gui.ISlotSkin.SlotSkin.medium, arrayOf(tr("Ferromagnetic core slot"))),
            org.ja13.eau.generic.GenericItemUsingDamageSlot(inventory, CasingSlotId, 130, 74, 1,
                    arrayOf<Class<*>>(CaseItemDescriptor::class.java),
                    org.ja13.eau.gui.ISlotSkin.SlotSkin.medium, arrayOf(tr("Casing slot")))))
    {
    companion object {
        const val primaryCableSlotId = 0
        const val secondaryCableSlotId = 1
        const val ferromagneticSlotId = 2
        const val CasingSlotId = 3
    }
}
