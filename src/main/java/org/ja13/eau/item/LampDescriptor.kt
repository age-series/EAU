package org.ja13.eau.item

import org.ja13.eau.EAU
import org.ja13.eau.i18n.I18N
import org.ja13.eau.misc.IConfigSharing
import org.ja13.eau.misc.Utils
import org.ja13.eau.misc.VoltageTierHelpers
import org.ja13.eau.sim.mna.component.Resistor
import org.ja13.eau.sixnode.lampsocket.LampSocketType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

class LampDescriptor(
        name: String, iconName: String,
        type: Type, socket: org.ja13.eau.sixnode.lampsocket.LampSocketType,
        nominalU: Double, nominalP: Double, nominalLight: Double, nominalLife: Double,
        vegetableGrowRate: Double) : GenericItemUsingDamageDescriptorUpgrade(name), IConfigSharing {
    enum class Type {
        INCANDESCENT, ECO, LED
    }

    var nominalP: Double
    var nominalLight: Double
    var nominalLifeHours: Double
    @JvmField
    var type: Type
    @JvmField
    var socket: org.ja13.eau.sixnode.lampsocket.LampSocketType
    var nominalU: Double
    var minimalU = 0.0
    var stableU = 0.0
    var stableUNormalised = 0.0
    var stableTime = 0.0
    var vegetableGrowRate: Double
    var serverNominalLife = 0.0

    val r: Double
        get() = nominalU * nominalU / nominalP

    fun getLifeInTag(stack: ItemStack): Double {
        if (!stack.hasTagCompound()) stack.tagCompound = getDefaultNBT()
        return if (stack.tagCompound.hasKey("life")) stack.tagCompound.getDouble("life") else {
            32.0 * 60.0 * 60.0 * 20.0
        } // 32 hours * 60 * 60 seconds/hour * 20 ticks/second
    }

    fun setLifeInTag(stack: ItemStack, life: Double) {
        if (!stack.hasTagCompound()) stack.tagCompound = getDefaultNBT()
        stack.tagCompound.setDouble("life", life)
    }

    override fun getDefaultNBT(): NBTTagCompound {
        val tag = NBTTagCompound()
        tag.setDouble("life", nominalLifeHours)
        return tag
    }

    fun applyTo(resistor: org.ja13.eau.sim.mna.component.Resistor) {
        resistor.r = r
    }

    override fun addInformation(itemStack: ItemStack?, entityPlayer: EntityPlayer?, list: MutableList<String>, par4: Boolean) {
        super.addInformation(itemStack, entityPlayer, list, par4)
        list.add(org.ja13.eau.i18n.I18N.tr("Technology: %1$", type))
        list.add(org.ja13.eau.i18n.I18N.tr("Range: %1$ blocks", (nominalLight * 15).toInt()))
        list.add(org.ja13.eau.i18n.I18N.tr("Power: %1\$W", Utils.plotValue(nominalP)))
        list.add(org.ja13.eau.i18n.I18N.tr("Resistance: %1$\u2126", Utils.plotValue(r)))
        list.add(org.ja13.eau.i18n.I18N.tr("Nominal lifetime: %1\$h", serverNominalLife))
        if (itemStack != null) {
            if (!itemStack.hasTagCompound() || !itemStack.tagCompound.hasKey("life")) {
                list.add(org.ja13.eau.i18n.I18N.tr("Condition:") + " " + org.ja13.eau.i18n.I18N.tr("New"))
            } else if (getLifeInTag(itemStack) > 0.5) {
                list.add(org.ja13.eau.i18n.I18N.tr("Condition:") + " " + org.ja13.eau.i18n.I18N.tr("Good"))
            } else if (getLifeInTag(itemStack) > 0.2) {
                list.add(org.ja13.eau.i18n.I18N.tr("Condition:") + " " + org.ja13.eau.i18n.I18N.tr("Used"))
            } else if (getLifeInTag(itemStack) > 0.1) {
                list.add(org.ja13.eau.i18n.I18N.tr("Condition:") + " " + org.ja13.eau.i18n.I18N.tr("End of life"))
            } else {
                list.add(org.ja13.eau.i18n.I18N.tr("Condition:") + " " + org.ja13.eau.i18n.I18N.tr("Bad"))
            }
            if (org.ja13.eau.EAU.debugEnabled)
                list.add("Life: ${getLifeInTag(itemStack)}")
        }
    }

    @Throws(IOException::class)
    override fun serializeConfig(stream: DataOutputStream) {
        stream.writeDouble(nominalLifeHours)
    }

    @Throws(IOException::class)
    override fun deserialize(stream: DataInputStream) {
        serverNominalLife = stream.readDouble()
    }

    init {
        setDefaultIcon(iconName)
        this.type = type
        this.socket = socket
        this.nominalU = nominalU
        this.nominalP = nominalP
        this.nominalLight = nominalLight
        this.nominalLifeHours = nominalLife
        this.vegetableGrowRate = vegetableGrowRate
        when (type) {
            Type.INCANDESCENT -> minimalU = nominalU * 0.5
            Type.ECO -> {
                stableUNormalised = 0.75
                minimalU = nominalU * 0.5
                stableU = nominalU * stableUNormalised
                stableTime = 4.0
            }
            Type.LED -> minimalU = nominalU * 0.75
        }
        org.ja13.eau.EAU.configShared.add(this)
        voltageTier = VoltageTierHelpers.fromVoltage(nominalU)
    }
}
