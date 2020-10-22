package org.ja13.eau.misc

class SlewLimiter {
    var positiveSlewRate = 0.0
        private set
    var negativeSlewRate = 0.0
        private set
    var target = 0.0
    var position = 0.0

    constructor(slewRate: Double) {
        setSlewRate(slewRate)
    }

    constructor(positive: Double, negative: Double) {
        setSlewRate(positive, negative)
    }

    fun targetReached(): Boolean {
        return position == target
    }

    fun targetReached(tolerance: Double): Boolean {
        return Math.abs(position - target) <= tolerance
    }

    fun setSlewRate(slewRate: Double) {
        positiveSlewRate = Math.abs(slewRate)
        negativeSlewRate = Math.abs(slewRate)
    }

    fun setSlewRate(positive: Double, negative: Double) {
        positiveSlewRate = Math.abs(positive)
        negativeSlewRate = Math.abs(negative)
    }

    fun step(deltaTime: Double) {
        var delta = target - position
        if (delta > 0f) delta = Math.min(delta, positiveSlewRate * deltaTime) else if (delta < 0f) delta = Math.max(delta, -negativeSlewRate * deltaTime)
        position += delta
    }
}
