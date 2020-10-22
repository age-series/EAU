package org.ja13.eau.sim.process.destruct

import org.ja13.eau.mechanical.ShaftElement

class ShaftSpeedWatchdog(shaftElement: ShaftElement, max: Double) : org.ja13.eau.sim.process.destruct.ValueWatchdog() {
    val shaftElement = shaftElement

    init {
        this.max = max
    }

    override fun getValue(): Double {
        var max = 0.0
        shaftElement.shaftConnectivity.forEach {
            val shaft = shaftElement.getShaft(it)
            if(shaft != null) max = Math.max(max, shaft.rads)
        }
        return max
    }
}
