package org.ja13.eau.misc

open class FunctionTable(var point: DoubleArray, var xMax: Double) : IFunction {
    var xMaxInv: Double = 1.0 / xMax
    override fun getValue(cx: Double): Double {
        var x = cx
        x *= xMaxInv
        if (x < 0f) return point[0] + (point[1] - point[0]) * (point.size - 1) * x
        if (x >= 1.0f) return point[point.size - 1] + (point[point.size - 1] - point[point.size - 2]) * (point.size - 1) * (x - 1.0)
        x *= point.size - 1.toDouble()
        val idx = x.toInt()
        x -= idx.toDouble()
        return point[idx + 1] * x + point[idx] * (1.0f - x)
    }

    open fun duplicate(xFactor: Double, yFactor: Double): FunctionTable? {
        val pointCpy = DoubleArray(point.size)
        for (idx in point.indices) {
            pointCpy[idx] = point[idx] * yFactor
        }
        return FunctionTable(pointCpy, xMax * xFactor)
    }
}
