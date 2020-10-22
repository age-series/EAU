package org.ja13.eau.misc

import com.google.common.base.Objects
import net.minecraft.util.Vec3
import java.lang.Double.NEGATIVE_INFINITY
import java.lang.Double.POSITIVE_INFINITY

class BoundingBox(xMin: Double, xMax: Double, yMin: Double, yMax: Double, zMin: Double, zMax: Double) {
    val min: Vec3 = Vec3.createVectorHelper(xMin, yMin, zMin)
    val max: Vec3 = Vec3.createVectorHelper(xMax, yMax, zMax)

    fun merge(other: BoundingBox): BoundingBox {
        return BoundingBox(
            Math.min(min.xCoord, other.min.xCoord),
            Math.max(max.xCoord, other.max.xCoord),
            Math.min(min.yCoord, other.min.yCoord),
            Math.max(max.yCoord, other.max.yCoord),
            Math.min(min.zCoord, other.min.zCoord),
            Math.max(max.zCoord, other.max.zCoord)
        )
    }

    fun centre(): Vec3 {
        return Vec3.createVectorHelper(
            min.xCoord + (max.xCoord - min.xCoord) / 2,
            min.yCoord + (max.yCoord - min.yCoord) / 2,
            min.zCoord + (max.zCoord - min.zCoord) / 2
        )
    }

    override fun toString(): String {
        return Objects.toStringHelper(this)
            .add("min", min)
            .add("max", max)
            .toString()
    }

    companion object {
        @JvmStatic
        fun mergeIdentity(): BoundingBox {
            return BoundingBox(POSITIVE_INFINITY, NEGATIVE_INFINITY, POSITIVE_INFINITY, NEGATIVE_INFINITY, POSITIVE_INFINITY, NEGATIVE_INFINITY)
        }
    }
}
