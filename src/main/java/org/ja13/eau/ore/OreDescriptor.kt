package org.ja13.eau.ore

import cpw.mods.fml.common.IWorldGenerator
import org.ja13.eau.EAU
import org.ja13.eau.generic.GenericItemBlockUsingDamageDescriptor
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.World
import net.minecraft.world.WorldType
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.feature.WorldGenMinable
import java.util.*

class OreDescriptor(
    name: String,
    var metadata: Int,
    var spawnRate: Int,
    var spawnSizeMin: Int,
    var spawnSizeMax: Int,
    var spawnHeightMin: Int,
    var spawnHeightMax: Int
) : GenericItemBlockUsingDamageDescriptor(name), IWorldGenerator {

    fun getBlockIconId(side: Int, damage: Int): IIcon {
        return icon!!
    }

    fun getBlockDropped(fortune: Int): ArrayList<ItemStack> {
        val list = ArrayList<ItemStack>()
        list.add(ItemStack(org.ja13.eau.EAU.oreItem, 1, metadata))
        return list
    }

    override fun generate(random: Random, chunkX: Int, chunkZ: Int, world: World, chunkGenerator: IChunkProvider, chunkProvider: IChunkProvider) {
        if (world.provider.isSurfaceWorld && world.worldInfo.terrainType != WorldType.FLAT) {
            val x = chunkX * 16
            val z = chunkZ * 16
            for (ii in 0 until spawnRate) { //This makes it gen multiple times in each chunk
                val posX = x + random.nextInt(16) //X coordinate to gen at
                val posY = spawnHeightMin + random.nextInt(spawnHeightMax - spawnHeightMin) //Y coordinate less than 40 to gen at
                val posZ = z + random.nextInt(16) //Z coordinate to gen at
                val size = spawnSizeMin + random.nextInt(spawnSizeMax - spawnSizeMin)
                println("Generating ore $metadata")
                WorldGenMinable(org.ja13.eau.EAU.oreBlock, metadata, size, Blocks.stone).generate(world, random, posX, posY, posZ) //The gen call
            }
        }
    }
}
