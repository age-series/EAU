package org.ja13.eau.misc

class PhysicalInterpolator(preTao: Double, accPerSPerError: Double, slowPerS: Double, rebond: Double) {
    var target = 0.0
    var factorSpeed = 0.0
    var factorPos = 0.0
    var factorFiltered = 0.0
    var accPerSPerError: Double
    var slowPerS: Double
    var ff: Double
    var rebond: Double
    var maxSpeed = 1000.0
    fun step(deltaT: Double) {
        factorFiltered += (target - factorFiltered) * ff * deltaT
        val error = factorFiltered - factorPos
        factorSpeed *= 1 - slowPerS * deltaT
        factorSpeed += error * accPerSPerError * deltaT
        if (factorSpeed > maxSpeed) factorSpeed = maxSpeed
        if (factorSpeed < -maxSpeed) factorSpeed = -maxSpeed
        factorPos += factorSpeed * deltaT
        if (factorPos > 1.0) {
            factorFiltered = target
            factorPos = 1.0
            factorSpeed = -factorSpeed * rebond
        }
        if (factorPos < 0.0) {
            factorFiltered = target
            factorPos = 0.0
            factorSpeed = -factorSpeed * rebond
        }
    }

    /*public void stepGraphic()
    {
        step(FrameTime.get());
    }*/
    fun get(): Double {
        return factorPos
    }

    fun setPos(value: Double) {
        factorPos = value
        factorFiltered = value
        target = value
    }

    init {
        ff = 1 / preTao
        this.accPerSPerError = accPerSPerError
        this.slowPerS = slowPerS
        this.rebond = rebond
    }
}
