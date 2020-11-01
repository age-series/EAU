package org.ja13.eau.misc

import cpw.mods.fml.common.FMLCommonHandler
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.Vec3
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager
import org.lwjgl.util.vector.Vector3f
import javax.vecmath.Vector3d

class Coordonate : INBTTReady {
    @JvmField
    var x = 0
    @JvmField
    var y = 0
    @JvmField
    var z = 0
    @JvmField
    var dimention = 0

    constructor() {
        x = 0
        y = 0
        z = 0
        dimention = 0
    }

    constructor(coord: Coordonate) {
        x = coord.x
        y = coord.y
        z = coord.z
        dimention = coord.dimention
    }

    constructor(nbt: NBTTagCompound, str: String) {
        readFromNBT(nbt, str)
    }

    override fun hashCode(): Int {
        return (x + y) * 0x10101010 + z
    }

    fun worldDimension(): Int {
        return dimention
    }

    private var w: World? = null
    fun world(): World {
        return if (w == null) {
            FMLCommonHandler.instance().minecraftServerInstance.worldServerForDimension(worldDimension())
        } else w!!
    }

    constructor(entity: org.ja13.eau.node.NodeBlockEntity) {
        x = entity.xCoord
        y = entity.yCoord
        z = entity.zCoord
        dimention = entity.worldObj.provider.dimensionId
    }

    constructor(x: Int, y: Int, z: Int, dimention: Int) {
        this.x = x
        this.y = y
        this.z = z
        this.dimention = dimention
    }

    constructor(x: Int, y: Int, z: Int, world: World) {
        this.x = x
        this.y = y
        this.z = z
        dimention = world.provider.dimensionId
        if (world.isRemote) w = world
    }

    constructor(entity: TileEntity) {
        x = entity.xCoord
        y = entity.yCoord
        z = entity.zCoord
        dimention = entity.worldObj.provider.dimensionId
        if (entity.worldObj.isRemote) w = entity.worldObj
    }

    fun newWithOffset(x: Int, y: Int, z: Int): Coordonate {
        return Coordonate(this.x + x, this.y + y, this.z + z, dimention)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Coordonate) return false
        return other.x == x && other.y == y && other.z == z && other.dimention == dimention
    }

    override fun readFromNBT(nbt: NBTTagCompound, str: String) {
        x = nbt.getInteger(str + "x")
        y = nbt.getInteger(str + "y")
        z = nbt.getInteger(str + "z")
        dimention = nbt.getInteger(str + "d")
    }

    override fun writeToNBT(nbt: NBTTagCompound, str: String) {
        nbt.setInteger(str + "x", x)
        nbt.setInteger(str + "y", y)
        nbt.setInteger(str + "z", z)
        nbt.setInteger(str + "d", dimention)
    }

    override fun toString(): String {
        return "X : $x Y : $y Z : $z D : $dimention"
    }

    fun move(dir: Direction?) {
        when (dir) {
            Direction.XN -> x--
            Direction.XP -> x++
            Direction.YN -> y--
            Direction.YP -> y++
            Direction.ZN -> z--
            Direction.ZP -> z++
            else -> {
            }
        }
    }

    fun moved(direction: Direction?): Coordonate {
        val moved = Coordonate(this)
        moved.move(direction)
        return moved
    }

    var block: Block
        get() = world().getBlock(x, y, z)
        set(b) {
            world().setBlock(x, y, z, b)
        }

    fun getAxisAlignedBB(ray: Int): AxisAlignedBB {
        return AxisAlignedBB.getBoundingBox(
            x - ray.toDouble(), y - ray.toDouble(), z - ray.toDouble(),
            x + ray + 1.toDouble(), y + ray + 1.toDouble(), z + ray + 1.toDouble())
    }

    fun distanceTo(e: Entity): Double {
        //return Math.abs(e.posX - (x + 0.5)) + Math.abs(e.posY - (y + 0.5)) + Math.abs(e.posZ - (z + 0.5))
        return Math.sqrt(Math.pow((e.posX - (x+0.5)),2.0) + Math.pow((e.posY - (y+0.5)),2.0) + Math.pow((e.posZ - (z+0.5)),2.0))
    }

    fun directionOf(e: Entity): Vector3d { //returns a normalized vector (normalized means vector with only direction)

        //val normalized = e.lookVec //help I don't know how to create a vector. This will work for now.
        val normalized = Vector3d(0.0,0.0,0.0)
        normalized.x = (e.posX - (x+0.5))
        normalized.y = (e.posY - (y+0.5))
        normalized.z = (e.posZ - (z+0.5))
        val magnitude = Math.sqrt(Math.pow((normalized.x),2.0) + Math.pow((normalized.y),2.0) + Math.pow((normalized.z),2.0))
        normalized.x = normalized.x/magnitude
        normalized.y = normalized.y/magnitude
        normalized.z = normalized.z/magnitude
        return normalized
    }

    val meta: Int
        get() = world().getBlockMetadata(x, y, z)
    val blockExist: Boolean
        get() {
            val w = DimensionManager.getWorld(dimention) ?: return false
            return w.blockExists(x, y, z)
        }
    val worldExist: Boolean
        get() = DimensionManager.getWorld(dimention) != null

    fun copyTo(v: DoubleArray) {
        v[0] = x + 0.5
        v[1] = y + 0.5
        v[2] = z + 0.5
    }

    fun setPosition(vp: DoubleArray) {
        x = vp[0].toInt()
        y = vp[1].toInt()
        z = vp[2].toInt()
    }

    fun setPosition(vp: Vec3) {
        x = vp.xCoord.toInt()
        y = vp.yCoord.toInt()
        z = vp.zCoord.toInt()
    }

    val tileEntity: TileEntity
        get() = world().getTileEntity(x, y, z)

    fun invalidate() {
        x = -1
        y = -1
        z = -1
        dimention = -5123
    }

    val isValid: Boolean
        get() = dimention != -5123

    fun trueDistanceTo(c: Coordonate): Double {
        val dx = x - c.x.toLong()
        val dy = y - c.y.toLong()
        val dz = z - c.z.toLong()
        return Math.sqrt(dx * dx + dy * dy + (dz * dz).toDouble())
    }

    fun setDimension(dimension: Int) {
        this.dimention = dimension
        w = null
    }

    fun copyFrom(c: Coordonate) {
        x = c.x
        y = c.y
        z = c.z
        dimention = c.dimention
    }

    fun applyTransformation(front: Direction, coordonate: Coordonate) {
        front.rotateFromXN(this)
        x += coordonate.x
        y += coordonate.y
        z += coordonate.z
    }

    fun setWorld(worldObj: World) {
        if (worldObj.isRemote) w = worldObj
        dimention = worldObj.provider.dimensionId
    }

    fun setMetadata(meta: Int) {
        world().setBlockMetadataWithNotify(x, y, z, meta, 0)
    }

    operator fun compareTo(o: Coordonate): Int {
        if (dimention != o.dimention) {
            return dimention - o.dimention
        } else if (x != o.x) {
            return x - o.x
        } else if (y != o.y) {
            return y - o.y
        } else if (z != o.z) {
            return z - o.z
        }
        return 0
    }

    fun subtract(b: Coordonate): Coordonate {
        return newWithOffset(-b.x, -b.y, -b.z)
    }

    fun negate(): Coordonate {
        return Coordonate(-x, -y, -z, dimention)
    }

    companion object {
        @JvmStatic
        fun getAxisAlignedBB(a: Coordonate, b: Coordonate): AxisAlignedBB {
            return AxisAlignedBB.getBoundingBox(
                Math.min(a.x, b.x).toDouble(), Math.min(a.y, b.y).toDouble(), Math.min(a.z, b.z).toDouble(),
                Math.max(a.x, b.x) + 1.0, Math.max(a.y, b.y) + 1.0, Math.max(a.z, b.z) + 1.0)
        }
    }
}
