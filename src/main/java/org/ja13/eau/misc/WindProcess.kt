package org.ja13.eau.misc

import org.ja13.eau.sim.IProcess
import net.minecraft.nbt.NBTTagCompound

class WindProcess : org.ja13.eau.sim.IProcess, INBTTReady {
    var windHit = 5.0
    var windTarget = 5.0
    var windVariation = 0.0
    var windTargetNoose = 0.0
    var windTargetFiltered = RcInterpolator(60.0)
    override fun process(time: Double) {
        val varF = 0.01
        windHit += windVariation * time
        windVariation += (target - windHit) * varF * time + (Math.random() * 2 - 1) * 0.1 * time
        windVariation *= 1 - 0.01 * time
        if (Math.random() < time / 1200) {
            newWindTarget()
        }
        if (Math.random() < time / 120) {
            windTargetNoose = (Math.random() * 2 - 1) * 1.2
        }
        windTargetFiltered.target = windTarget
        windTargetFiltered.step(time)
    }

    fun newWindTarget() {
        val next = (Math.pow(Math.random(), 3.0) * 20)
        windTarget += (next - windTarget) * 0.7
    }

    val target: Double
        get() = windTargetNoose + windTargetFiltered.get()
    val targetNotFiltered: Double
        get() = windTargetNoose + windTargetFiltered.target

    fun getWind(y: Int): Double {
        return Math.max(0.0, windHit * Math.min(y, 100) / 100)
    }

    override fun readFromNBT(nbt: NBTTagCompound, str: String) {
        windHit = nbt.getDouble(str + "windHit")
        windTarget = nbt.getDouble(str + "windTarget")
        windVariation = nbt.getDouble(str + "windVariation")
        windTargetFiltered.setValue(nbt.getDouble(str + "windTargetFiltered"))
    }

    override fun writeToNBT(nbt: NBTTagCompound, str: String) {
        nbt.setDouble(str + "windHit", windHit)
        nbt.setDouble(str + "windTarget", windTarget)
        nbt.setDouble(str + "windVariation", windVariation)
        nbt.setDouble(str + "windTargetFiltered", windTargetFiltered.get())
    }
}
