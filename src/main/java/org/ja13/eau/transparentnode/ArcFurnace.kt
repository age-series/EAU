package org.ja13.eau.transparentnode

import org.ja13.eau.generic.GenericItemUsingDamageSlot
import org.ja13.eau.ghost.GhostGroup
import org.ja13.eau.gui.GuiContainerEln
import org.ja13.eau.gui.GuiHelperContainer
import org.ja13.eau.gui.ISlotSkin.SlotSkin
import org.ja13.eau.item.GraphiteDescriptor
import org.ja13.eau.misc.*
import org.ja13.eau.misc.Obj3D.Obj3DPart
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.transparent.*
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.mna.component.Resistor
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog
import org.ja13.eau.sim.process.destruct.WorldExplosion
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer
import org.lwjgl.opengl.GL11

class ArcFurnaceDescriptor(name: String, val obj: org.ja13.eau.misc.Obj3D): org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, ArcFurnaceElement::class.java, ArcFurnaceRender::class.java) {
    private var main: Obj3DPart? = null

    init {
        this.name = name
        main = obj.getPart("ArcFurnace")
        val gg = org.ja13.eau.ghost.GhostGroup()
        gg.addRectangle(0, 2, 0, 4, -1, 1)
        gg.removeElement(0, 0, 0)
        ghostGroup = gg
    }

    fun draw(front: Direction) {
        if (main != null) {
            front.glRotateZnRef()
            //GL11.glRotatef(-90f, 0f, 1f, 0f);
            GL11.glTranslatef(-1.5f, -0.5f, 2.5f)
            GL11.glScalef(0.5f, 0.5f, 0.5f)
            main?.draw()
            //UtilsClient.drawEntityItem(inEntity, -0.35, 0.04, 0.3, 1, 1f)
        }
    }

    override fun shouldUseRenderHelper(type: IItemRenderer.ItemRenderType?, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper?): Boolean {
        return false
    }

    override fun shouldUseRenderHelperEln(type: IItemRenderer.ItemRenderType?, item: ItemStack?, helper: IItemRenderer.ItemRendererHelper?): Boolean {
        return false
    }
}

class ArcFurnaceElement(node: org.ja13.eau.node.transparent.TransparentNode, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElement(node, descriptor) {

    var adesc: ArcFurnaceDescriptor? = null

    init {
        adesc = descriptor as ArcFurnaceDescriptor
    }

    private val inventory = org.ja13.eau.node.transparent.TransparentNodeElementInventory(5, 1, this)
    //private val connectionType: CableRenderType? = null
    //private val eConn = LRDUMask()

    //private val inEntity: EntityItem? = null
    //private val outEntity: EntityItem? = null
    //var powerFactor = 0f
    //var processState = 0f
    //private val processStatePerSecond = 0f

    //var UFactor = 0f

    private val electricalLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("electricalLoad")
    val electricalResistor = org.ja13.eau.sim.mna.component.Resistor(electricalLoad, null)

    private val voltageWatchdog = org.ja13.eau.sim.process.destruct.VoltageStateWatchDog()

    init {
        electricalLoadList.add(electricalLoad)
        electricalComponentList.add(electricalResistor)
        val exp = org.ja13.eau.sim.process.destruct.WorldExplosion(this).machineExplosion()
        slowProcessList.add(voltageWatchdog.set(electricalLoad).setUNominal(480.0).set(exp))
    }

    override fun multiMeterString(side: Direction?): String? {
        return Utils.plotUIP(electricalLoad.u, electricalLoad.current)
    }

    override fun thermoMeterString(side: Direction?): String? {
        return null
    }

    override fun getElectricalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ElectricalLoad? {
        return electricalLoad
    }

    override fun getThermalLoad(side: Direction?, lrdu: LRDU?): org.ja13.eau.sim.ThermalLoad? {
        return null
    }

    override fun onBlockActivated(entityPlayer: EntityPlayer, side: Direction?, vx: Float, vy: Float, vz: Float): Boolean {
        return false
    }

    override fun getConnectionMask(side: Direction?, lrdu: LRDU): Int {
        if (lrdu != LRDU.Down) return 0
        return org.ja13.eau.node.NodeBase.MASK_ELECTRIC
    }

    override fun initialize() {
        connect()
    }

    override fun getInventory(): IInventory? {
        return inventory
    }

    override fun hasGui(): Boolean {
        return true
    }

    override fun newContainer(side: Direction?, player: EntityPlayer?): Container? {
        return ArcFurnaceContainer(node, player, inventory)
    }
}

class ArcFurnaceRender(val tileEntity: org.ja13.eau.node.transparent.TransparentNodeEntity, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElementRender(tileEntity, descriptor) {

    val inventory = org.ja13.eau.node.transparent.TransparentNodeElementInventory(5, 64, this)

    var adesc: ArcFurnaceDescriptor? = null

    init {
        adesc = descriptor as ArcFurnaceDescriptor
    }

    override fun draw() {
        adesc?.draw(front)
    }

    override fun newGuiDraw(side: Direction?, player: EntityPlayer?): GuiScreen? {
        return ArcFurnaceGui(player, inventory, this)
    }
}

class ArcFurnaceContainer(val node: org.ja13.eau.node.NodeBase?, player: EntityPlayer?, inventory: IInventory): org.ja13.eau.misc.BasicContainer(
    player, inventory, arrayOf<Slot>(
        org.ja13.eau.generic.GenericItemUsingDamageSlot(
                inventory, 0, 0, 0, 1,
                org.ja13.eau.item.GraphiteDescriptor::class.java,
                SlotSkin.medium, arrayOf("Graphite Slot")
        ),
        org.ja13.eau.generic.GenericItemUsingDamageSlot(
                inventory, 1, 30, 0, 1,
                org.ja13.eau.item.GraphiteDescriptor::class.java,
                SlotSkin.medium, arrayOf("Graphite Slot")
        ),
        org.ja13.eau.generic.GenericItemUsingDamageSlot(
                inventory, 2, 15, 15, 1,
                org.ja13.eau.item.GraphiteDescriptor::class.java,
                SlotSkin.medium, arrayOf("Graphite Slot")
        ),
        org.ja13.eau.generic.GenericItemUsingDamageSlot(
                inventory, 3, 15, 30, 64,
                org.ja13.eau.item.GraphiteDescriptor::class.java,
                SlotSkin.medium, arrayOf("Input Slot")
        ),
        org.ja13.eau.generic.GenericItemUsingDamageSlot(
                inventory, 4, 15, 45, 64,
                org.ja13.eau.item.GraphiteDescriptor::class.java,
                SlotSkin.medium, arrayOf("Output Slot")
        )
    ))

class ArcFurnaceGui(player: EntityPlayer?, inventory: IInventory, render: ArcFurnaceRender): org.ja13.eau.gui.GuiContainerEln(ArcFurnaceContainer(null, player, inventory)) {
    override fun newHelper(): org.ja13.eau.gui.GuiHelperContainer {
            return org.ja13.eau.gui.GuiHelperContainer(this, 176, 166, 50, 84)
    }
}
