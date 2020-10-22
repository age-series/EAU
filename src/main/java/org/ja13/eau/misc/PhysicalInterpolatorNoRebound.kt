package org.ja13.eau.misc

class PhysicalInterpolatorNoRebound(preTao: Double, var accPerSPerError: Double, var slowPerS: Double) {
    var target = 0.0
    var factorSpeed = 0.0
    var factorPos = 0.0
    var factorFiltered = 0.0
    var ff: Double = 1 / preTao
    var maxSpeed = 1000.0

    fun step(deltaT: Double) {
        factorFiltered += (target - factorFiltered) * ff * deltaT
        val error = factorFiltered - factorPos
        factorSpeed *= 1 - slowPerS * deltaT
        factorSpeed += error * accPerSPerError * deltaT
        if (factorSpeed > maxSpeed) factorSpeed = maxSpeed
        if (factorSpeed < -maxSpeed) factorSpeed = -maxSpeed
        factorPos += factorSpeed * deltaT
    }

    fun get(): Double {
        return factorPos
    }

    fun setPos(value: Double) {
        factorPos = value
        factorFiltered = value
        target = value
    }
}
