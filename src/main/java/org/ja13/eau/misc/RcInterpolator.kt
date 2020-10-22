package org.ja13.eau.misc

import net.minecraft.nbt.NBTTagCompound

class RcInterpolator(preTao: Double) : INBTTReady {
    var ff: Double = 1 / preTao
    var target: Double
    var factorFiltered: Double
    fun step(deltaT: Double) {
        factorFiltered += (target - factorFiltered) * ff * deltaT
    }

    fun get(): Double {
        return factorFiltered
    }

    fun setValue(value: Double) {
        factorFiltered = value
    }

    fun setValueFromTarget() {
        factorFiltered = target
    }

    override fun readFromNBT(nbt: NBTTagCompound, str: String) {
        target = nbt.getDouble(str + "factor")
        factorFiltered = nbt.getDouble(str + "factorFiltered")
    }

    override fun writeToNBT(nbt: NBTTagCompound, str: String) {
        nbt.setDouble(str + "factor", target)
        nbt.setDouble(str + "factorFiltered", factorFiltered)
    }

    init {
        factorFiltered = 0.0
        target = 0.0
    }
}
