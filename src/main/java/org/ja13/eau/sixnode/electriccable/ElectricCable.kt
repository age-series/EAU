package org.ja13.eau.sixnode.electriccable

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import org.ja13.eau.misc.Coordonate
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.UtilsClient
import org.ja13.eau.misc.VoltageTier
import org.ja13.eau.sim.mna.misc.MnaConst
import org.lwjgl.opengl.GL11
import java.util.*

class ElectricCableDescriptor(name: String, render: org.ja13.eau.cable.CableRenderDescriptor, val material: String = "Copper"): org.ja13.eau.sixnode.genericcable.GenericCableDescriptor(name, ElectricCableElement::class.java, ElectricCableRender::class.java) {

    var insulationVoltage = 0.0
        set(x) {
            field = x
            voltageTier = when {
                insulationVoltage <= 0.0 -> {
                    // No insulation means no voltage limits!
                    VoltageTier.NEUTRAL
                }
                insulationVoltage <= 300.0 -> {
                    VoltageTier.INDUSTRIAL
                }
                insulationVoltage <= 1_000.0 -> {
                    VoltageTier.INDUSTRIAL
                }
                else -> {
                    VoltageTier.SUBURBAN_GRID
                }
            }
        }

    init {
        this.render = render
        this.electricalRs = MnaConst.cableResistance
        this.voltageTier = VoltageTier.NEUTRAL
    }

    override fun applyTo(electricalLoad: org.ja13.eau.sim.ElectricalLoad, rsFactor: Double) {
        electricalLoad.rs = electricalRs * rsFactor
    }

    override fun applyTo(electricalLoad: org.ja13.eau.sim.ElectricalLoad) {
        electricalLoad.rs = electricalRs
    }

    override fun applyTo(resistor: org.ja13.eau.sim.mna.component.Resistor) {
        resistor.r = electricalRs
    }

    override fun applyTo(resistor: org.ja13.eau.sim.mna.component.Resistor, factor: Double) {
        resistor.r = electricalRs * factor
    }

    override fun applyTo(thermalLoad: org.ja13.eau.sim.ThermalLoad) {
        thermalLoad.Rs = 1.0
        thermalLoad.C = 1.0
        thermalLoad.Rp = 1.0
    }

    override fun getNodeMask(): Int {
        return org.ja13.eau.node.NodeBase.MASK_ELECTRIC
    }

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)
        list.add(org.ja13.eau.i18n.I18N.tr("Nominal Ratings:"))
        list.add("  " + org.ja13.eau.i18n.I18N.tr("Serial resistance: %1$\u2126", Utils.plotValue(electricalRs * 2)))
    }
}

class ElectricCableElement(sixNode: org.ja13.eau.node.six.SixNode, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor): org.ja13.eau.node.six.SixNodeElement(sixNode, side, descriptor) {

    val descriptor = descriptor as ElectricCableDescriptor

    val electricalLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("electricalLoad")
    val thermalLoad = org.ja13.eau.sim.nbt.NbtThermalLoad("thermalLoad")
    val heater = org.ja13.eau.sim.process.heater.ElectricalLoadHeatThermalLoad(electricalLoad, thermalLoad)
    val thermalWatchdog = org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog()
    val voltageWatchdog = org.ja13.eau.sim.process.destruct.VoltageStateWatchDog()

    init {
        electricalLoad.setCanBeSimplifiedByLine(true)
        electricalLoadList.add(electricalLoad)
        thermalLoad.setAsSlow()
        thermalLoadList.add(thermalLoad)
        thermalSlowProcessList.add(heater)
        thermalWatchdog
            .set(thermalLoad)
            .setLimit(100.0, -100.0)
            .set(org.ja13.eau.sim.process.destruct.WorldExplosion(this).cableExplosion())
        slowProcessList.add(thermalWatchdog)
        slowProcessList.add(PlayerHarmer(electricalLoad, this.descriptor.insulationVoltage, this.coordonate))
    }

    override fun getElectricalLoad(lrdu: LRDU?, mask: Int): org.ja13.eau.sim.ElectricalLoad {
        return electricalLoad
    }

    override fun getThermalLoad(lrdu: LRDU?, mask: Int): org.ja13.eau.sim.ThermalLoad {
        return thermalLoad
    }

    override fun getConnectionMask(lrdu: LRDU?): Int {
        return descriptor.nodeMask
    }

    override fun multiMeterString(): String {
        return Utils.plotUIP(electricalLoad.u, electricalLoad.i)
    }

    override fun thermoMeterString(): String {
        return Utils.plotCelsius(thermalLoad.Tc)
    }

    override fun initialize() {
        descriptor.applyTo(electricalLoad)
        descriptor.applyTo(thermalLoad)
    }

    override fun getWaila(): Map<String, String>? {
        val info: MutableMap<String, String> = HashMap()
        info[org.ja13.eau.i18n.I18N.tr("Current")] = Utils.plotAmpere(electricalLoad.i)
        info[org.ja13.eau.i18n.I18N.tr("Temperature")] = Utils.plotCelsius(thermalLoad.t)
        if (org.ja13.eau.EAU.wailaEasyMode) {
            info[org.ja13.eau.i18n.I18N.tr("Voltage")] = Utils.plotVolt(electricalLoad.u)
        }
        val ss = electricalLoad.subSystem
        if (ss != null) {
            val subSystemSize = electricalLoad.subSystem.component.size
            val textColor = when {
                subSystemSize <= 8 -> "§a"
                subSystemSize <= 15 -> "§6"
                else -> "§c"
            }
            info[org.ja13.eau.i18n.I18N.tr("Subsystem Matrix Size")] = textColor + subSystemSize
        } else {
            info[org.ja13.eau.i18n.I18N.tr("Subsystem Matrix Size")] = "§cnull SubSystem"
        }
        return info
    }
}

class ElectricCableRender(tileEntity: org.ja13.eau.node.six.SixNodeEntity, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor): org.ja13.eau.node.six.SixNodeElementRender(tileEntity, side, descriptor) {

    val descriptor = descriptor as ElectricCableDescriptor

    override fun drawCableAuto() = false

    override fun draw() {
        if (descriptor.insulationVoltage < 0.1) {
            when (descriptor.material.toLowerCase()) {
                "copper" -> {
                    GL11.glColor3f(0.722f, 0.451f, 0.20f)
                }
                "aluminum" -> {
                    GL11.glColor3f(0.815f, 0.835f, 0.858f)
                }
                else -> {
                    // Same as copper
                    GL11.glColor3f(0.722f, 0.451f, 0.20f)
                }
            }
        } else {
            Utils.setGlColorFromDye(0, 1.0f)
        }
        UtilsClient.bindTexture(descriptor.render.cableTexture)
        glListCall()
        GL11.glColor3f(1f, 1f, 1f)
    }

    override fun glListDraw() {
        org.ja13.eau.cable.CableRender.drawCable(descriptor.render, connectedSide, org.ja13.eau.cable.CableRender.connectionType(this, side))
        org.ja13.eau.cable.CableRender.drawNode(descriptor.render, connectedSide, org.ja13.eau.cable.CableRender.connectionType(this, side))
    }

    override fun glListEnable() = true

    override fun getCableRender(lrdu: LRDU?): org.ja13.eau.cable.CableRenderDescriptor? {
        return descriptor.render
    }
}

class PlayerHarmer(val electricalLoad: org.ja13.eau.sim.ElectricalLoad, private val insulationVoltage: Double, val location: Coordonate): org.ja13.eau.sim.IProcess {

    private fun harmFunction(distance: Double) = 1.0 - ( distance / 3.0)

    override fun process(time: Double) {
        val harmLevel = Math.max(0.0, (electricalLoad.u - 50 - insulationVoltage) / 500.0)
        val objects = location.world().getEntitiesWithinAABB(Entity::class.java, location.getAxisAlignedBB(4))
        for(obj in objects) {
            val ent = obj as Entity
            val distance = location.distanceTo(ent)
            val pain = (harmFunction(distance) * harmLevel).toFloat()
            if (distance < 3 && pain > 0.05) {
                ent.attackEntityFrom(DamageSource("Cable"), pain)
            }
        }
    }
}
