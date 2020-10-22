package org.ja13.eau.misc.series

class SeriesMap(startExp: Double, eValue: DoubleArray) : ISeriesMapping {
    var startExp = 1.0
    var eValue: DoubleArray
    fun getSeries(): Int {
        return eValue.size
    }

    override fun getValue(id: Int): Double {
        var count = id
        val rot = count / getSeries()
        count -= rot * getSeries()
        return Math.pow(10.0, startExp) * Math.pow(10.0, rot.toDouble()) * eValue[count]
    }

    companion object {
        fun newE12(startExp: Double): SeriesMap {
            return SeriesMap(startExp, doubleArrayOf(1.0, 1.2, 1.5, 1.8, 2.2, 2.7, 3.3, 3.9, 4.7, 5.6, 6.8, 8.2))
        }

        fun newE6(startExp: Double): SeriesMap {
            return SeriesMap(startExp, doubleArrayOf(1.0, 1.5, 2.2, 3.3, 4.7, 6.8))
        }
    }

    init {
        this.startExp = startExp
        this.eValue = eValue
    }
}
