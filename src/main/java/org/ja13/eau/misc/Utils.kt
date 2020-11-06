package org.ja13.eau.misc

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.relauncher.Side
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Blocks
import net.minecraft.inventory.IInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.ShapedRecipes
import net.minecraft.item.crafting.ShapelessRecipes
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.network.play.server.S3FPacketCustomPayload
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.ChatComponentText
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.ShapedOreRecipe
import net.minecraftforge.oredict.ShapelessOreRecipe
import org.apache.logging.log4j.LogManager
import org.ja13.eau.EAU
import org.ja13.eau.generic.GenericItemBlockUsingDamage
import org.ja13.eau.misc.Obj3D.Obj3DPart
import org.lwjgl.opengl.GL11
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

object Utils {
    val d = arrayOfNulls<Any>(5)
    const val minecraftDay = 60 * 24.toDouble()
    val random = Random()
    const val burnTimeToEnergyFactor = 1.0
    const val voltageMageFactor = 0.1

    @JvmStatic
    val logger = LogManager.getLogger(EAU.MODID)!!

    @JvmStatic
    var uuid = 1
        get() {
            if (field < 1) field = 1
            return field++
        }
        private set

    @JvmStatic
    fun rand(min: Double, max: Double): Double {
        return random.nextDouble() * (max - min) + min
    }

    @JvmStatic
    fun println(str: Any?) {
        if (EAU.debugEnabled)
            logger.info(str)
    }

    @JvmStatic
    fun isTheClass(o: Any, c: Class<*>): Boolean {
        if (o.javaClass == c) return true
        var classIterator: Class<*>? = o.javaClass.superclass
        while (classIterator != null) {
            if (classIterator == c) {
                return true
            }
            classIterator = classIterator.superclass
        }
        return false
    }

    @JvmStatic
    fun entityLivingViewDirection(entityLiving: EntityLivingBase): Direction {
        if (entityLiving.rotationPitch > 45) return Direction.YN
        if (entityLiving.rotationPitch < -45) return Direction.YP
        val dirx = MathHelper.floor_double((entityLiving.rotationYaw * 4.0f / 360.0f).toDouble() + 0.5) and 3
        if (dirx == 3) return Direction.XP
        if (dirx == 0) return Direction.ZP
        return if (dirx == 1) Direction.XN else Direction.ZN
    }

    @JvmStatic
    fun entityLivingHorizontalViewDirection(entityLiving: EntityLivingBase): Direction {
        val dirx = MathHelper.floor_double((entityLiving.rotationYaw * 4.0f / 360.0f).toDouble() + 0.5) and 3
        if (dirx == 3) return Direction.XP
        if (dirx == 0) return Direction.ZP
        return if (dirx == 1) Direction.XN else Direction.ZN
    }

    @JvmStatic
    fun getItemEnergy(par0ItemStack: ItemStack?): Double {
        return burnTimeToEnergyFactor * 80000.0 / 1600 * TileEntityFurnace.getItemBurnTime(par0ItemStack)
    }

    @JvmStatic
    val coalEnergyReference: Double
        get() = burnTimeToEnergyFactor * 80000.0

    @JvmStatic
    fun plotValue(value: Double): String {
        val valueAbs = Math.abs(value)
        return when {
            valueAbs < 0.0001 ->  "0"
            valueAbs < 0.000999 -> String.format("%1.2fµ", value * 10000)
            valueAbs < 0.00999 -> String.format("%1.2fm", value * 1000)
            valueAbs < 0.0999 -> String.format("%2.1fm", value * 1000)
            valueAbs < 0.999 -> String.format("%3.0fm", value * 1000)
            valueAbs < 9.99 -> String.format("%1.2f", value)
            valueAbs < 99.9 -> String.format("%2.1f", value)
            valueAbs < 999 -> String.format("%3.0f", value)
            valueAbs < 9999 -> String.format("%1.2fk", value / 1000.0)
            valueAbs < 99999 -> String.format("%2.1fk", value / 1000.0)
            else -> String.format("%3.0fk", value / 1000.0)
        }
    }

    @JvmStatic
    fun plotValue(value: Double, unit: String, header: String = ""): String {
        if (header.isNotEmpty()) return "$header ${plotValue(value)}$unit"
        return "${plotValue(value)}$unit"
    }

    @JvmStatic
    fun plotVolt(value: Double, header: String = ""): String {
        return plotValue(value, "V", header)
    }

    @JvmStatic
    fun plotAmpere(value: Double, header: String = ""): String {
        return plotValue(value, "A", header)
    }

    @JvmStatic
    fun plotCelsius(value: Double, header: String = ""): String {
        val localValue = value + org.ja13.eau.sim.PhysicalConstant.Tref - org.ja13.eau.sim.PhysicalConstant.TCelsius
        return plotValue(localValue,"°C", header)
    }

    @JvmStatic
    fun plotPercent(value: Double, header: String = ""): String {
        val plotString = if (value >= 1.0) String.format("%3.0f", value * 100.0) else String.format("%3.1f", value * 100.0)
        if (header.isNotEmpty()) return "$header $plotString%"
        return "$plotString%"
    }

    @JvmStatic
    fun plotEnergy(value: Double, header: String = ""): String {
        return plotValue(value, "J", header)
    }

    @JvmStatic
    fun plotRads(value: Double, header: String = ""): String {
        return plotValue(value,"rad/s", header)
    }

    @JvmStatic
    fun plotER(E: Double, R: Double): String {
        return "${plotEnergy(E)} ${plotRads(R)}"
    }

    @JvmStatic
    fun plotPower(value: Double, header: String = ""): String {
        return plotValue(value,"W", header)
    }

    @JvmStatic
    fun plotOhm(value: Double, header: String = ""): String {
        return plotValue(value,"Ω", header)
    }

    @JvmStatic
    fun plotUIP(U: Double, I: Double): String {
        return plotVolt(U, "U") + plotAmpere(I, "I") + plotPower(Math.abs(U * I), "P")
    }

    @JvmStatic
    fun plotTime(value: Double, header: String = ""): String {
        var lVal = value
        var str = ""
        val h: Int
        val mn: Int
        val s: Int
        if (lVal <= 0.0) return plotValue(0.0, "s", header)
        h = (lVal / 3600).toInt()
        lVal = lVal.rem(3600)
        mn = (lVal / 60).toInt()
        lVal = lVal.rem(60)
        s = (lVal / 1).toInt()
        if (h != 0) str += h.toString() + "h"
        if (mn != 0) str += "${mn}m"
        if (s != 0) str += "${s}s"

        if (header.isNotEmpty()) return "$header $str"
        return str
    }

    @JvmStatic
    fun plotBuckets(value: Double, header: String = ""): String {
        return plotValue(value, "B", header)
    }

    @JvmStatic
    fun readFromNBT(nbt: NBTTagCompound, str: String?, inventory: IInventory) {
        val var2 = nbt.getTagList(str, 10)
        for (var3 in 0 until var2.tagCount()) {
            val var4 = var2.getCompoundTagAt(var3) as NBTTagCompound
            val var5: Int = org.ja13.eau.misc.KotlinHelper.and(var4.getByte("Slot"), 255)
            if (var5 >= 0 && var5 < inventory.sizeInventory) {
                inventory.setInventorySlotContents(var5, ItemStack.loadItemStackFromNBT(var4))
            }
        }
    }

    @JvmStatic
    fun writeToNBT(nbt: NBTTagCompound, str: String?, inventory: IInventory) {
        val var2 = NBTTagList()
        for (var3 in 0 until inventory.sizeInventory) {
            if (inventory.getStackInSlot(var3) != null) {
                val var4 = NBTTagCompound()
                var4.setByte("Slot", var3.toByte())
                inventory.getStackInSlot(var3).writeToNBT(var4)
                var2.appendTag(var4)
            }
        }
        nbt.setTag(str, var2)
    }

    @JvmStatic
    fun sendPacketToClient(bos: ByteArrayOutputStream, player: EntityPlayerMP) {
        val packet = S3FPacketCustomPayload(org.ja13.eau.EAU.channelName, bos.toByteArray())
        player.playerNetServerHandler.sendPacket(packet)
    }

    @JvmStatic
    fun setGlColorFromDye(damage: Int) {
        setGlColorFromDye(damage, 1.0f)
    }

    @JvmStatic
    fun setGlColorFromDye(damage: Int, gain: Float) {
        setGlColorFromDye(damage, gain, 0f)
    }

    @JvmStatic
    fun setGlColorFromDye(damage: Int, gain: Float, bias: Float) {
        when (damage) {
            0 -> GL11.glColor3f(0.2f * gain + bias, 0.2f * gain + bias, 0.2f * gain + bias)
            1 -> GL11.glColor3f(1.0f * gain + bias, 0.05f * gain + bias, 0.05f * gain + bias)
            2 -> GL11.glColor3f(0.2f * gain + bias, 0.5f * gain + bias, 0.1f * gain + bias)
            3 -> GL11.glColor3f(0.3f * gain + bias, 0.2f * gain + bias, 0.1f * gain + bias)
            4 -> GL11.glColor3f(0.2f * gain + bias, 0.2f * gain + bias, 1.0f * gain + bias)
            5 -> GL11.glColor3f(0.7f * gain + bias, 0.05f * gain + bias, 1.0f * gain + bias)
            6 -> GL11.glColor3f(0.2f * gain + bias, 0.7f * gain + bias, 0.9f * gain + bias)
            7 -> GL11.glColor3f(0.7f * gain + bias, 0.7f * gain + bias, 0.7f * gain + bias)
            8 -> GL11.glColor3f(0.4f * gain + bias, 0.4f * gain + bias, 0.4f * gain + bias)
            9 -> GL11.glColor3f(1.0f * gain + bias, 0.5f * gain + bias, 0.5f * gain + bias)
            10 -> GL11.glColor3f(0.05f * gain + bias, 1.0f * gain + bias, 0.05f * gain + bias)
            11 -> GL11.glColor3f(0.9f * gain + bias, 0.8f * gain + bias, 0.1f * gain + bias)
            12 -> GL11.glColor3f(0.4f * gain + bias, 0.5f * gain + bias, 1.0f * gain + bias)
            13 -> GL11.glColor3f(0.9f * gain + bias, 0.3f * gain + bias, 0.9f * gain + bias)
            14 -> GL11.glColor3f(1.0f * gain + bias, 0.6f * gain + bias, 0.3f * gain + bias)
            15 -> GL11.glColor3f(1.0f * gain + bias, 1.0f * gain + bias, 1.0f * gain + bias)
            else -> GL11.glColor3f(0.05f * gain + bias, 0.05f * gain + bias, 0.05f * gain + bias)
        }
    }

    @JvmStatic
    fun setGlColorFromLamp(colorIdx: Int) {
        when (colorIdx) {
            15 -> GL11.glColor3f(1.0f, 1.0f, 1.0f)
            0 -> GL11.glColor3f(0.25f, 0.25f, 0.25f)
            1 -> GL11.glColor3f(1.0f, 0.5f, 0.5f)
            2 -> GL11.glColor3f(0.5f, 1.0f, 0.5f)
            3 -> GL11.glColor3f(0.5647f, 0.36f, 0.36f)
            4 -> GL11.glColor3f(0.5f, 0.5f, 1.0f)
            5 -> GL11.glColor3f(0.78125f, 0.46666f, 1.0f)
            6 -> GL11.glColor3f(0.5f, 1.0f, 1.0f)
            7 -> GL11.glColor3f(0.75f, 0.75f, 0.75f)
            8 -> GL11.glColor3f(0.5f, 0.5f, 0.5f)
            9 -> GL11.glColor3f(1.0f, 0.5f, 0.65882f)
            10 -> GL11.glColor3f(0.75f, 1.0f, 0.5f)
            11 -> GL11.glColor3f(1.0f, 1.0f, 0.5f)
            12 -> GL11.glColor3f(0.5f, 0.75f, 1.0f)
            13 -> GL11.glColor3f(1.0f, 0.5f, 1.0f)
            14 -> GL11.glColor3f(1.0f, 0.80f, 0.5f)
            else -> GL11.glColor3f(1.0f, 1.0f, 1.0f)
        }
    }

    @JvmStatic
    fun getWeatherNoLoad(dim: Int): Double {
        if (!getWorldExist(dim)) return 0.0
        val world = getWorld(dim)
        if (world.isThundering) return 1.0
        return if (world.isRaining) 0.5 else 0.0
    }

    @JvmStatic
    fun getWorld(dim: Int): World {
        return FMLCommonHandler.instance().minecraftServerInstance.worldServerForDimension(dim)
    }

    @JvmStatic
    fun getWorldExist(dim: Int): Boolean {
        return DimensionManager.getWorld(dim) != null
    }

    @JvmStatic
    fun getWind(worldId: Int, y: Int): Double {
        return if (!getWorldExist(worldId)) {
            Math.max(0.0, org.ja13.eau.EAU.wind.getWind(y))
        } else {
            val world = getWorld(worldId)
            val factor = 1f + world.getRainStrength(0f) * 0.2f + world.getWeightedThunderStrength(0f) * 0.2f
            Math.max(0.0, org.ja13.eau.EAU.wind.getWind(y) * factor + world.getRainStrength(0f) * 1f + world.getWeightedThunderStrength(0f) * 2f)
        }
    }

    @JvmStatic
    fun dropItem(itemStack: ItemStack?, x: Int, y: Int, z: Int, world: World) {
        if (itemStack == null) return
        if (world.gameRules.getGameRuleBooleanValue("doTileDrops")) {
            val var6 = 0.7f
            val var7 = (world.rand.nextFloat() * var6).toDouble() + (1.0f - var6).toDouble() * 0.5
            val var9 = (world.rand.nextFloat() * var6).toDouble() + (1.0f - var6).toDouble() * 0.5
            val var11 = (world.rand.nextFloat() * var6).toDouble() + (1.0f - var6).toDouble() * 0.5
            val var13 = EntityItem(world, x.toDouble() + var7, y.toDouble() + var9, z.toDouble() + var11, itemStack)
            var13.delayBeforeCanPickup = 10
            world.spawnEntityInWorld(var13)
        }
    }

    @JvmStatic
    fun dropItem(itemStack: ItemStack?, coordonate: Coordonate) {
        dropItem(itemStack, coordonate.x, coordonate.y, coordonate.z, coordonate.world())
    }

    @JvmStatic
    fun tryPutStackInInventory(stack: ItemStack, inventory: IInventory?): Boolean {
        if (inventory == null) return false
        val limit = inventory.inventoryStackLimit

        // First, make a list of possible target slots.
        val slots = ArrayList<Int>(4)
        var need = stack.stackSize
        run {
            var i = 0
            while (i < inventory.sizeInventory && need > 0) {
                val slot = inventory.getStackInSlot(i)
                if (slot != null && slot.stackSize < limit && slot.isItemEqual(stack)) {
                    slots.add(i)
                    need -= limit - slot.stackSize
                }
                i++
            }
        }
        var i = 0
        while (i < inventory.sizeInventory && need > 0) {
            if (inventory.getStackInSlot(i) == null) {
                slots.add(i)
                need -= limit
            }
            i++
        }

        // Is there space enough?
        if (need > 0) {
            return false
        }

        // Yes. Proceed.
        var toPut = stack.stackSize
        for (slot in slots) {
            val target = inventory.getStackInSlot(slot)
            if (target == null) {
                val amount = Math.min(toPut, limit)
                inventory.setInventorySlotContents(slot, ItemStack(stack.item, amount, stack.itemDamage))
                toPut -= amount
            } else {
                val space = limit - target.stackSize
                val amount = Math.min(toPut, space)
                target.stackSize += amount
                toPut -= amount
            }
            if (toPut <= 0) break
        }
        return true
    }

    @JvmStatic
    @Deprecated("")
    fun canPutStackInInventory(stackList: Array<ItemStack>, inventory: IInventory, slotsIdList: IntArray): Boolean {
        val limit = inventory.inventoryStackLimit
        val outputStack = arrayOfNulls<ItemStack>(slotsIdList.size)
        val inputStack = arrayOfNulls<ItemStack>(stackList.size)
        for (idx in outputStack.indices) {
            if (inventory.getStackInSlot(slotsIdList[idx]) != null) outputStack[idx] = inventory.getStackInSlot(slotsIdList[idx]).copy()
        }
        for (idx in stackList.indices) {
            inputStack[idx] = stackList[idx].copy()
        }
        var oneStackDone: Boolean
        for (stack in inputStack) {
            // if(stack == null) continue;
            oneStackDone = false
            for (idx in slotsIdList.indices) {
                val targetStack = outputStack[idx]
                if (targetStack == null) {
                    outputStack[idx] = stack
                    oneStackDone = true
                    break
                } else if (targetStack.isItemEqual(stack)) {
                    // inventory.decrStackSize(idx, -stack.stackSize);
                    val transferMax = limit - targetStack.stackSize
                    if (transferMax > 0) {
                        var transfer = stack!!.stackSize
                        if (transfer > transferMax) transfer = transferMax
                        outputStack[idx]!!.stackSize += transfer
                        stack.stackSize -= transfer
                    }
                    if (stack!!.stackSize == 0) {
                        oneStackDone = true
                        break
                    }
                }
            }
            if (!oneStackDone) return false
        }
        return true
    }

    @JvmStatic
    @Deprecated("")
    fun tryPutStackInInventory(stackList: Array<ItemStack>, inventory: IInventory, slotsIdList: IntArray): Boolean {
        val limit = inventory.inventoryStackLimit
        for (stack in stackList) {
            for (idx in slotsIdList.indices) {
                val targetStack = inventory.getStackInSlot(slotsIdList[idx])
                if (targetStack == null) {
                    inventory.setInventorySlotContents(slotsIdList[idx], stack.copy())
                    stack.stackSize = 0
                    break
                } else if (targetStack.isItemEqual(stack)) {
                    // inventory.decrStackSize(idx, -stack.stackSize);
                    val transferMax = limit - targetStack.stackSize
                    if (transferMax > 0) {
                        var transfer = stack.stackSize
                        if (transfer > transferMax) transfer = transferMax
                        inventory.decrStackSize(slotsIdList[idx], -transfer)
                        stack.stackSize -= transfer
                    }
                    if (stack.stackSize == 0) {
                        break
                    }
                }
            }
        }
        return true
    }

    @JvmStatic
    fun voltageMargeFactorSub(value: Double): Double {
        if (value > 1 + voltageMageFactor) {
            return value - voltageMageFactor
        } else if (value > 1) {
            return 1.0
        }
        return value
    }

    /*
	 * public static void bindGuiTexture(String string) { Utils.bindTextureByName("/sprites/gui/" + string); }
	 */
    @JvmStatic
    fun serialiseItemStack(stream: DataOutputStream, stack: ItemStack?) {
        if (stack == null) {
            stream.writeShort(-1)
            stream.writeShort(-1)
        } else {
            stream.writeShort(Item.getIdFromItem(stack.item))
            stream.writeShort(stack.itemDamage)
        }
    }

    @JvmStatic
    fun unserializeItemStack(stream: DataInputStream): ItemStack? {
        val id: Short = stream.readShort()
        val damage: Short = stream.readShort()
        return if (id.toInt() == -1) null else newItemStack(id.toInt(), 1, damage.toInt())
    }

    @JvmStatic
    fun unserializeItemStackToEntityItem(stream: DataInputStream, old: EntityItem?, tileEntity: TileEntity): EntityItem? {
        val itemId: Short = stream.readShort()
        val itemDamage: Short = stream.readShort()
        return if (itemId == (-1).toShort())
            null
        else if (old == null || Item.getIdFromItem(old.entityItem.item).toShort() != itemId || old.entityItem.itemDamage.toShort() != itemDamage)
                EntityItem(tileEntity.worldObj, tileEntity.xCoord + 0.5, tileEntity.yCoord + 0.5, tileEntity.zCoord + 1.2, newItemStack(itemId.toInt(), 1, itemDamage.toInt()))
        else
            old
    }

    @JvmStatic
    val isGameInPause: Boolean
        @JvmStatic
        get() = Minecraft.getMinecraft().isGamePaused

    @JvmStatic
    fun getLight(w: World, e: EnumSkyBlock?, x: Int, y: Int, z: Int): Int {
        return w.getSavedLightValue(e, x, y, z)
    }

    /*
	 * int b = w.getSkyBlockTypeBrightness(EnumSkyBlock.Block, x, y, z); int s = w.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, x, y, z) - w.calculateSkylightSubtracted(0f); return Math.max(b, s); }
	 */
    /*
	 * public static void drawHalo(Obj3DPart halo,float r,float g,float b,World w,int x,int y,int z,boolean bilinear) {
	 * 
	 * disableLight(); enableBlend();
	 * 
	 * drawHaloNoLightSetup(halo,r,g,b, w,x,y,z,bilinear); enableLight(); disableBlend(); }
	 */
    /*
	 * public float frameTime() { float time = Minecraft.getMinecraft().entityRenderer.performanceToFps(par0) }
	 */
    @JvmStatic
    fun notifyNeighbor(t: TileEntity) {
        val x = t.xCoord
        val y = t.yCoord
        val z = t.zCoord
        val w = t.worldObj
        var o: TileEntity?
        o = w.getTileEntity(x + 1, y, z)
        if (o != null && o is org.ja13.eau.node.ITileEntitySpawnClient) (o as org.ja13.eau.node.ITileEntitySpawnClient).tileEntityNeighborSpawn()
        o = w.getTileEntity(x - 1, y, z)
        if (o != null && o is org.ja13.eau.node.ITileEntitySpawnClient) (o as org.ja13.eau.node.ITileEntitySpawnClient).tileEntityNeighborSpawn()
        o = w.getTileEntity(x, y + 1, z)
        if (o != null && o is org.ja13.eau.node.ITileEntitySpawnClient) (o as org.ja13.eau.node.ITileEntitySpawnClient).tileEntityNeighborSpawn()
        o = w.getTileEntity(x, y - 1, z)
        if (o != null && o is org.ja13.eau.node.ITileEntitySpawnClient) (o as org.ja13.eau.node.ITileEntitySpawnClient).tileEntityNeighborSpawn()
        o = w.getTileEntity(x, y, z + 1)
        if (o != null && o is org.ja13.eau.node.ITileEntitySpawnClient) (o as org.ja13.eau.node.ITileEntitySpawnClient).tileEntityNeighborSpawn()
        o = w.getTileEntity(x, y, z - 1)
        if (o != null && o is org.ja13.eau.node.ITileEntitySpawnClient) (o as org.ja13.eau.node.ITileEntitySpawnClient).tileEntityNeighborSpawn()
    }

    @JvmStatic
    fun playerHasMeter(entityPlayer: EntityPlayer): Boolean {
        val cur = entityPlayer.currentEquippedItem
        return (org.ja13.eau.EAU.multiMeterElement.checkSameItemStack(cur)
            || org.ja13.eau.EAU.thermometerElement.checkSameItemStack(cur)
            || org.ja13.eau.EAU.allMeterElement.checkSameItemStack(cur)
            || org.ja13.eau.EAU.configCopyToolElement.checkSameItemStack(cur))
    }

    @JvmStatic
    fun getRedstoneLevelAround(coord: Coordonate, side: Direction): Int {
        var localSide = side
        var level = coord.world().getStrongestIndirectPower(coord.x, coord.y, coord.z)
        if (level >= 15) return 15
        localSide = localSide.inverse
        when (localSide) {
            Direction.YN, Direction.YP -> {
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x + 1, coord.y, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x - 1, coord.y, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y, coord.z + 1, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y, coord.z - 1, localSide.toSideValue()))
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y + 1, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y - 1, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y, coord.z + 1, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y, coord.z - 1, localSide.toSideValue()))
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x + 1, coord.y, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x - 1, coord.y, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y + 1, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y - 1, coord.z, localSide.toSideValue()))
            }
            Direction.XN, Direction.XP -> {
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y + 1, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y - 1, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y, coord.z + 1, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y, coord.z - 1, localSide.toSideValue()))
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x + 1, coord.y, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x - 1, coord.y, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y + 1, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y - 1, coord.z, localSide.toSideValue()))
            }
            Direction.ZN, Direction.ZP -> {
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x + 1, coord.y, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x - 1, coord.y, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y + 1, coord.z, localSide.toSideValue()))
                if (level >= 15) return 15
                level = Math.max(level, coord.world().getIndirectPowerLevelTo(coord.x, coord.y - 1, coord.z, localSide.toSideValue()))
            }
        }
        return level
    }

    @JvmStatic
    fun isPlayerAround(world: World, axisAlignedBB: AxisAlignedBB?): Boolean {
        return world.getEntitiesWithinAABB(EntityPlayer::class.java, axisAlignedBB).isNotEmpty()
    }

    @JvmStatic
    fun getItemObject(stack: ItemStack?): Any? {
        if (stack == null) return null
        val i = stack.item
        if (i is org.ja13.eau.generic.GenericItemUsingDamage<*>) {
            return i.getDescriptor(stack)
        }
        return if (i is GenericItemBlockUsingDamage<*>) {
            i.getDescriptor(stack)
        } else i
    }

    val side: Side
        get() = FMLCommonHandler.instance().effectiveSide
    val isServer: Boolean
        get() = side == Side.SERVER

    @JvmStatic
    fun printSide(string: String?) {
        println(string)
    }

    @JvmStatic
    fun modbusToShort(outputNormalized: Double, i: Int): Short {
        val bit = java.lang.Float.floatToRawIntBits(outputNormalized.toFloat())
        return if (i == 1) bit.toShort() else (bit ushr 16).toShort()
    }

    @JvmStatic
    fun modbusToFloat(first: Short, second: Short): Float {
        val bit = (first.toInt() and 0xFFFF shl 16) + (second.toInt() and 0xFFFF)
        return java.lang.Float.intBitsToFloat(bit)
    }

    @JvmStatic
    fun areSame(stack: ItemStack, output: ItemStack): Boolean {
        try {
            if (stack.item === output.item && stack.itemDamage == output.itemDamage) return true
            val stackIds = OreDictionary.getOreIDs(stack)
            val outputIds = OreDictionary.getOreIDs(output)
            for (i in outputIds) {
                for (j in stackIds) {
                    if (i == j) return true
                }
            }
        } catch (e: Exception) {
        }
        return false
    }

    @JvmStatic
    fun getVec05(c: Coordonate): Vec3 {
        return Vec3.createVectorHelper(c.x + (if (c.x < 0) -1 else 1) * 0.5, c.y + (if (c.y < 0) -1 else 1) * 0.5, c.z + (if (c.z < 0) -1 else 1) * 0.5)
    }

    @JvmStatic
    fun getHeadPosY(e: Entity): Double {
        return if (e is EntityOtherPlayerMP) e.posY + e.getEyeHeight() else e.posY
    }

    @JvmStatic
    fun isCreative(entityPlayer: EntityPlayerMP): Boolean {
        return entityPlayer.theItemInWorldManager.isCreative
    }

    @JvmStatic
    fun mustDropItem(entityPlayer: EntityPlayerMP?): Boolean {
        return if (entityPlayer == null) true else !isCreative(entityPlayer)
    }

    @JvmStatic
    fun serverTeleport(e: Entity, x: Double, y: Double, z: Double) {
        if (e is EntityPlayerMP) e.setPositionAndUpdate(x, y, z) else e.setPosition(x, y, z)
    }

    @JvmStatic
    fun traceRay(world: World, x: Double, y: Double, z: Double, tx: Double, ty: Double, tz: Double): ArrayList<Block> {
        var lx = x
        var ly = y
        var lz = z
        val blockList = ArrayList<Block>()
        var dx: Double
        var dy: Double
        var dz: Double
        dx = tx - lx
        dy = ty - ly
        dz = tz - lz
        val norm = Math.sqrt(dx * dx + dy * dy + dz * dz)
        val normInv = 1 / (norm + 0.000000001)
        dx *= normInv
        dy *= normInv
        dz *= normInv
        var d = 0.0
        while (d < norm) {
            if (isBlockLoaded(world, lx, ly, lz)) {
                val b = getBlock(world, lx, ly, lz)
                blockList.add(b)
            }
            lx += dx
            ly += dy
            lz += dz
            d += 1.0
        }
        return blockList
    }

    @JvmStatic
    fun traceRay(w: World, posX: Double, posY: Double, posZ: Double, targetX: Double, targetY: Double, targetZ: Double, weight: TraceRayWeight): Double {
        val posXint = Math.round(posX)
        val posYint = Math.round(posY)
        val posZint = Math.round(posZ)
        var x = (posX - posXint)
        var y = (posY - posYint)
        var z = (posZ - posZint)
        var vx = (targetX - posX)
        var vy = (targetY - posY)
        var vz = (targetZ - posZ)
        val rangeMax = Math.sqrt(vx * vx + vy * vy + (vz * vz))
        val normInv = 1f / rangeMax
        vx *= normInv
        vy *= normInv
        vz *= normInv
        if (vx == 0.0) vx += 0.0001
        if (vy == 0.0) vy += 0.0001
        if (vz == 0.0) vz += 0.0001
        val vxInv = 1f / vx
        val vyInv = 1f / vy
        val vzInv = 1f / vz
        var stackRed = 0.0
        var d = 0.0
        while (d < rangeMax) {
            val xFloor = MathHelper.floor_double(x)
            val yFloor = MathHelper.floor_double(y)
            val zFloor = MathHelper.floor_double(z)
            var dx = x - xFloor
            var dy = y - yFloor
            var dz = z - zFloor
            dx = if (vx > 0) (1 - dx) * vxInv else -dx * vxInv
            dy = if (vy > 0) (1 - dy) * vyInv else -dy * vyInv
            dz = if (vz > 0) (1 - dz) * vzInv else -dz * vzInv
            val dBest = Math.min(Math.min(dx, dy), dz) + 0.01f
            val xInt: Int = (xFloor + posXint).toInt()
            val yInt: Int = (yFloor + posYint).toInt()
            val zInt: Int = (zFloor + posZint).toInt()
            var block = Blocks.air
            if (w.blockExists(xInt, yInt, zInt))
                block = w.getBlock(xInt, yInt, zInt)
            var dToStack: Double
            dToStack = if (d + dBest < rangeMax) dBest else {
                rangeMax - d
            }
            stackRed += weight.getWeight(block) * dToStack
            x += vx * dBest
            y += vy * dBest
            z += vz * dBest
            d += dBest
        }
        return stackRed
    }

    @JvmStatic
    fun isBlockLoaded(world: World, x: Double, y: Double, z: Double): Boolean {
        return world.blockExists(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z))
    }

    @JvmStatic
    fun getBlock(world: World, x: Double, y: Double, z: Double): Block {
        return world.getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z))
    }

    @JvmStatic
    fun getLength(x: Double, y: Double, z: Double, tx: Double, ty: Double, tz: Double): Double {
        val dx: Double = tx - x
        val dy: Double = ty - y
        val dz: Double = tz - z
        return Math.sqrt(dx * dx + dy * dy + dz * dz)
    }

    @JvmStatic
    fun readPrivateInt(o: Any, fieldName: String): Int {
        try {
            val f = o.javaClass.getDeclaredField(fieldName)
            f.isAccessible = true
            return f.getInt(o)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return 0
    }

    @JvmStatic
    fun getItemStackGrid(r: IRecipe?): Array<Array<ItemStack?>>? {
        val stacks = Array(3) { arrayOfNulls<ItemStack>(3) }
        try {
            if (r is ShapedRecipes) {
                for (idx2 in 0..2) {
                    for (idx in 0..2) {
                        var rStack: ItemStack? = null
                        if (idx < r.recipeWidth && idx2 < r.recipeHeight) {
                            rStack = r.recipeItems[idx + idx2 * r.recipeWidth]
                        }
                        stacks[idx2][idx] = rStack
                    }
                }
                return stacks
            }
            if (r is ShapedOreRecipe) {
                val width = readPrivateInt(r, "width")
                val height = readPrivateInt(r, "height")
                val inputs = r.input
                for (idx2 in 0 until height) {
                    for (idx in 0 until width) {
                        val o = inputs[idx + idx2 * width]
                        var stack: ItemStack? = null
                        if (o is List<*>) {
                            if (o.isNotEmpty()) stack = o[0] as ItemStack?
                        }
                        if (o is ItemStack) {
                            stack = o
                        }
                        stacks[idx2][idx] = stack
                    }
                }
                return stacks
            }
            if (r is ShapelessRecipes) {
                for ((idx, o) in r.recipeItems.withIndex()) {
                    val stack = o as ItemStack?
                    stacks[idx / 3][idx % 3] = stack
                }
                return stacks
            }
            if (r is ShapelessOreRecipe) {
                for ((idx, o) in r.input.withIndex()) {
                    var stack: ItemStack? = null
                    if (o is List<*> && o.isNotEmpty()) {
                        stack = o[0] as ItemStack?
                    }
                    if (o is ItemStack) {
                        stack = o
                    }
                    stacks[idx / 3][idx % 3] = stack
                }
                return stacks
            }
        } catch (e: Exception) {
            // TODO: handle exception
        }
        return null
    }

    @JvmStatic
    fun getWorldTime(world: World): Double {
        return world.worldTime / 23999.0
    }

    @JvmStatic
    fun isWater(waterCoord: Coordonate): Boolean {
        val block = waterCoord.block
        return block === Blocks.flowing_water || block === Blocks.water
    }

    @JvmStatic
    fun addChatMessage(entityPlayer: EntityPlayer, string: String?) {
        entityPlayer.addChatMessage(ChatComponentText(string))
    }

    @JvmStatic
    fun newItemStack(i: Int, size: Int, damage: Int): ItemStack {
        return ItemStack(Item.getItemById(i), size, damage)
    }

    @JvmStatic
    fun newItemStack(i: Item?, size: Int, damage: Int): ItemStack {
        return ItemStack(i, size, damage)
    }

    @JvmStatic
    fun getTags(nbt: NBTTagCompound): List<NBTTagCompound> {
        val set: Array<Any?> = nbt.func_150296_c().toTypedArray()
        val tags = ArrayList<NBTTagCompound>()
        for (idx in set.indices) {
            tags.add(nbt.getCompoundTag(set[idx] as String))
        }
        return tags
    }

    @JvmStatic
    fun isRemote(world: IBlockAccess): Boolean {
        if (world !is World) {
            fatal()
        }
        return (world as World).isRemote
    }

    @JvmStatic
    fun nullCheck(o: Any?): Boolean {
        return o == null
    }

    @JvmStatic
    fun nullFatal(o: Any?) {
        if (o == null) fatal()
    }

    @JvmStatic
    fun fatal() {
        try {
            throw Exception()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getBlock(blockId: Int): Block {
        return Block.getBlockById(blockId)
    }

    @JvmStatic
    fun updateSkylight(chunk: Chunk) {
        chunk.func_150804_b(false)
    }

    @JvmStatic
    fun updateAllLightTypes(worldObj: World, xCoord: Int, yCoord: Int, zCoord: Int) {
        worldObj.func_147451_t(xCoord, yCoord, zCoord)
        worldObj.markBlocksDirtyVertical(xCoord, zCoord, 0, 255)
    }

    @JvmStatic
    fun getItemId(stack: ItemStack): Int {
        return Item.getIdFromItem(stack.item)
    }

    @JvmStatic
    fun getItemId(block: Block?): Int {
        return Item.getIdFromItem(Item.getItemFromBlock(block))
    }

    @JvmOverloads
    fun addSmelting(fromStack: ItemStack?, toStack: ItemStack?, f: Float = 0.3f) {
        if (fromStack != null && toStack != null)
            FurnaceRecipes.smelting().func_151394_a(newItemStack(fromStack.item, 1, fromStack.itemDamage), toStack, f)
    }

    @JvmOverloads
    fun addSmelting(parentItem: Item?, parentItemDamage: Int, findItemStack: ItemStack?, f: Float = 0.3f) {
        FurnaceRecipes.smelting().func_151394_a(newItemStack(parentItem, 1, parentItemDamage), findItemStack, f)
    }

    @JvmOverloads
    fun addSmelting(parentBlock: Block?, parentItemDamage: Int, findItemStack: ItemStack?, f: Float = 0.3f) {
        FurnaceRecipes.smelting().func_151394_a(newItemStack(Item.getItemFromBlock(parentBlock), 1, parentItemDamage), findItemStack, f)
    }

    @JvmStatic
    fun newNbtTagCompund(nbt: NBTTagCompound, string: String?): NBTTagCompound {
        val cmp = NBTTagCompound()
        nbt.setTag(string, cmp)
        return cmp
    }

    @JvmStatic
    val mapFolder: String
        get() {
            val server = FMLCommonHandler.instance().minecraftServerInstance
            val savesAt = if (!server.isDedicatedServer) "saves/" else ""
            return savesAt + server.folderName + "/"
        }

    @JvmStatic
    fun getMapFile(name: String): File {
        val server = FMLCommonHandler.instance().minecraftServerInstance
        return server.getFile(mapFolder + name)
    }

    @JvmStatic
    fun readMapFile(name: String): String {
        val file = getMapFile(name)
        val fis = FileInputStream(file)
        val data = ByteArray(file.length().toInt())
        fis.read(data)
        fis.close()
        return String(data, Charset.forName("UTF-8"))
    }

    @JvmStatic
    fun getSixNodePinDistance(obj: Obj3DPart): FloatArray {
        return floatArrayOf(Math.abs(obj.zMin * 16), Math.abs(obj.zMax * 16), Math.abs(obj.yMin * 16), Math.abs(obj.yMax * 16))
    }

    private fun isWrench(stack: ItemStack): Boolean {
        return areSame(stack, org.ja13.eau.EAU.wrenchItemStack) || stack.displayName.toLowerCase().contains("wrench")
    }

    @JvmStatic
    fun isPlayerUsingWrench(player: EntityPlayer?): Boolean {
        if (player == null) return false
        if (org.ja13.eau.EAU.playerManager[player].getInteractEnable()) return true
        val stack = player.inventory.getCurrentItem() ?: return false
        return isWrench(stack)
    }

    @JvmStatic
    fun isClassLoaded(name: String?): Boolean {
        return try {
            Class.forName(name)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    @JvmStatic
    fun plotSignal(U: Double, I: Double): String {
        return plotVolt(U, "U") + plotAmpere(I, "I") + plotPercent(U / VoltageTier.TTL.voltage, "Value")
    }

    @JvmStatic
    fun limit(value: Float, min: Float, max: Float): Float {
        return Math.max(Math.min(value, max), min)
    }

    @JvmStatic
    fun limit(value: Double, min: Double, max: Double): Double {
        return Math.max(Math.min(value, max), min)
    }

    @JvmStatic
    fun printFunction(func: FunctionTable, start: Double, end: Double, stepSize: Double) {
        println("********")
        var actualStart = start
        var actualEnd = end
        if (actualStart < actualEnd) {
            val tmp = actualStart
            actualStart = actualEnd
            actualEnd = tmp
        }
        if (stepSize <= 0.0) return
        var x: Double = actualStart
        while (true) {
            if (x >= actualEnd) break
            println(func.getValue(x))
            x += stepSize
        }
        println("********")
    }

    interface TraceRayWeight {
        fun getWeight(block: Block?): Float
    }

    class TraceRayWeightOpaque : TraceRayWeight {
        override fun getWeight(block: Block?): Float {
            if (block == null) return 0f
            return if (block.isOpaqueCube) 1f else 0f
        }
    }
}
