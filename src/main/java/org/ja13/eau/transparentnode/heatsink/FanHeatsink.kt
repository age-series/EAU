package org.ja13.eau.transparentnode.heatsink

import org.ja13.eau.EAU
import org.ja13.eau.i18n.I18N
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.RcInterpolator
import org.ja13.eau.misc.Utils.plotAmpere
import org.ja13.eau.misc.Utils.plotCelsius
import org.ja13.eau.misc.Utils.plotPower
import org.ja13.eau.misc.Utils.plotValue
import org.ja13.eau.misc.Utils.plotVolt
import org.ja13.eau.misc.VoltageTierHelpers
import org.ja13.eau.node.NodeBase
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeElement
import org.ja13.eau.node.transparent.TransparentNodeElementRender
import org.ja13.eau.node.transparent.TransparentNodeEntity
import org.ja13.eau.sim.ElectricalLoad
import org.ja13.eau.sim.IProcess
import org.ja13.eau.sim.ThermalLoad
import org.ja13.eau.sim.mna.component.Resistor
import org.ja13.eau.sim.nbt.NbtElectricalLoad
import org.ja13.eau.sim.nbt.NbtThermalLoad
import org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog
import org.ja13.eau.sim.process.destruct.VoltageStateWatchDog
import org.ja13.eau.sim.process.destruct.WorldExplosion
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.client.IItemRenderer.ItemRenderType
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*

class FanHeatsinkDescriptor(name: String, obj3D: org.ja13.eau.misc.Obj3D, val voltage: Double, val nominalP: Double = 250.0, var activeNominalP: Double = -1.0): org.ja13.eau.node.transparent.TransparentNodeDescriptor(name, FanHeatsinkElement::class.java, FanHeatsinkRender::class.java) {
    val main = obj3D.getPart("main")
    val fan = obj3D.getPart("rot")
    val maxTemp = 800.0
    val thermalC: Double
    val thermalRp: Double
    val thermalRs: Double

    init {
        voltageTier = VoltageTierHelpers.fromVoltage(voltage)
        if (activeNominalP < 0.0) {
            activeNominalP = if (voltage < 50) {
                500.0
            } else {
                2_000.0
            }
        }
        thermalC = (nominalP + activeNominalP) * (1.0/3.0)
        thermalRp = 30.0 / nominalP
        thermalRs = 1.0 / (nominalP + activeNominalP)
    }

    /*
    nonminalP = 200, nominalT = 30, nominalTao = 10, nominalConnectionDrop = 1, nominalElectricalCooling = 800
    thermalC = (nominalP + nominalElectricalCoolingPower) * nominalTao / nominalT;
    thermalRp = nominalT / nominalP;
    thermalRs = nominalConnectionDrop / (nominalP + nominalElectricalCoolingPower);
     */

    fun applyTo(load: org.ja13.eau.sim.ThermalLoad) {
        load.set(thermalRs, thermalRp, thermalC)
    }

    fun draw(alpha: Float) {
        main.draw()
        fan.draw(alpha, 0f, 1f, 0f)
    }

    override fun handleRenderType(item: ItemStack?, type: ItemRenderType?): Boolean {
        return true
    }

    override fun shouldUseRenderHelper(type: ItemRenderType, item: ItemStack?,
                                       helper: ItemRendererHelper?): Boolean {
        return type != ItemRenderType.INVENTORY
    }

    override fun renderItem(type: ItemRenderType, item: ItemStack?, vararg data: Any?) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, *data)
        } else {
            draw(0f)
        }
    }

    override fun addInfo(itemStack: ItemStack, entityPlayer: EntityPlayer, list: MutableList<String>) {
        super.addInfo(itemStack, entityPlayer, list)
        list.add(org.ja13.eau.i18n.I18N.tr("Used to cool down turbines."))
        list.add(org.ja13.eau.i18n.I18N.tr("Max. temperature: %1$°C", plotValue(maxTemp)))
        list.add(org.ja13.eau.i18n.I18N.tr("Nominal usage:"))
        list.add("  " + org.ja13.eau.i18n.I18N.tr("Cooling power: %1\$W", plotValue(nominalP)))
        list.add("  " + org.ja13.eau.i18n.I18N.tr("Fan voltage: %1\$V", plotValue(voltage)))
        list.add("  " + org.ja13.eau.i18n.I18N.tr("Fan power consumption: %1\$W", plotValue(400.0)))
        list.add("  " + org.ja13.eau.i18n.I18N.tr("Fan cooling power: %1\$W", plotValue(activeNominalP)))
    }
}

class FanHeatsinkElement(node: org.ja13.eau.node.transparent.TransparentNode, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElement(node, descriptor) {
    val descriptor = descriptor as FanHeatsinkDescriptor

    val thermalLoad = org.ja13.eau.sim.nbt.NbtThermalLoad("thermalLoad")
    val positiveLoad = org.ja13.eau.sim.nbt.NbtElectricalLoad("positiveLoad")
    val powerResistor = org.ja13.eau.sim.mna.component.Resistor(positiveLoad, null)
    val heatWatchdog = org.ja13.eau.sim.process.destruct.ThermalLoadWatchDog()
    val voltageWatchdog = org.ja13.eau.sim.process.destruct.VoltageStateWatchDog()
    val slowProcess = FanHeatsinkSlowProcess(this)

    init {
        thermalLoadList.add(thermalLoad)
        electricalLoadList.add(positiveLoad)
        electricalComponentList.add(powerResistor)
        heatWatchdog.set(thermalLoad)
            .setTMax(this.descriptor.maxTemp)
            .set(org.ja13.eau.sim.process.destruct.WorldExplosion(this).machineExplosion())
        voltageWatchdog.set(positiveLoad)
            .setUNominal(this.descriptor.voltage)
            .set(org.ja13.eau.sim.process.destruct.WorldExplosion(this).machineExplosion())
        slowProcessList.add(heatWatchdog)
        slowProcessList.add(voltageWatchdog)
        slowProcessList.add(slowProcess)
    }

    override fun getElectricalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ElectricalLoad? {
        return if (side === front || side === front.inverse) positiveLoad else null
    }

    override fun getThermalLoad(side: Direction, lrdu: LRDU): org.ja13.eau.sim.ThermalLoad? {
        if (side === Direction.YN || side === Direction.YP || lrdu !== LRDU.Down) return null
        return if (side === front || side === front.inverse) null else thermalLoad
    }

    override fun getConnectionMask(side: Direction, lrdu: LRDU): Int {
        if (side === Direction.YN || side === Direction.YP || lrdu !== LRDU.Down) return 0
        return if (side === front || side === front.inverse) org.ja13.eau.node.NodeBase.MASK_ELECTRIC else org.ja13.eau.node.NodeBase.MASK_THERMAL
    }

    override fun multiMeterString(side: Direction?): String? {
        return plotVolt(positiveLoad.u) + " " + plotAmpere(positiveLoad.current)
    }

    override fun thermoMeterString(side: Direction?): String? {
        return plotCelsius(thermalLoad.Tc) + " " + plotPower(thermalLoad.power)
    }

    override fun initialize() {
        descriptor.applyTo(thermalLoad)
        positiveLoad.rs = org.ja13.eau.EAU.getSmallRs()
        val power = if (descriptor.voltage > 50) 480.0 else 48.0
        powerResistor.r = descriptor.voltage * descriptor.voltage / power
        connect()
    }

    override fun onBlockActivated(entityPlayer: EntityPlayer?, side: Direction?, vx: Float, vy: Float, vz: Float): Boolean {
        return false
    }


    override fun networkSerialize(stream: DataOutputStream) {
        super.networkSerialize(stream)
        if (descriptor.voltage > 50) {
            stream.writeDouble(powerResistor.p / 480.0)
        } else {
            stream.writeDouble(powerResistor.p / 96.0) // 48, but we want 1/2 the speed of rotation.
        }

    }

    override fun getWaila(): Map<String, String>? {
        val info: MutableMap<String, String> = HashMap()
        info[org.ja13.eau.i18n.I18N.tr("Temperature")] = plotCelsius(thermalLoad.Tc)
        if (org.ja13.eau.EAU.wailaEasyMode) {
            info[org.ja13.eau.i18n.I18N.tr("Thermal power")] = plotPower(thermalLoad.power)
        }
        return info
    }
}

class FanHeatsinkSlowProcess(val element: FanHeatsinkElement): org.ja13.eau.sim.IProcess {
    override fun process(time: Double) {
        val poweredFactor = element.powerResistor.p / if (element.descriptor.voltage > 50) 480.0 else 48.0
        val thermalRp = 1.0 / ( 1.0 / element.descriptor.nominalP + (poweredFactor / (30.0 / element.descriptor.activeNominalP)))
        element.thermalLoad.setRp(thermalRp)
    }
}

class FanHeatsinkRender(entity: org.ja13.eau.node.transparent.TransparentNodeEntity, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor): org.ja13.eau.node.transparent.TransparentNodeElementRender(entity, descriptor) {
    val descriptor = descriptor as FanHeatsinkDescriptor
    var rc = RcInterpolator(2.0)

    override fun draw() {
        front.glRotateXnRef()
        descriptor.draw(alpha.toFloat())
    }

    override fun refresh(deltaT: Double) {
        rc.target = powerFactor
        rc.step(deltaT)
        alpha += rc.get() * 360f * deltaT * 2.0 // speed things up a bit
        while (alpha > 360f) alpha -= 360f
    }

    var alpha = 0.0
    var powerFactor = 0.0

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        powerFactor = stream.readDouble()
    }
}
