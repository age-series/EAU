package org.ja13.eau.misc

import java.io.File

object ImageList {
    val blockTextures = mutableSetOf<String>()
    val itemTextures = mutableSetOf<String>()

    private const val ASSET_LOCATION = "/home/jared/Documents/EAU/src/main/resources/assets/eau"

    fun notifyOfUnusedTextures() {
        val blockDir = File("$ASSET_LOCATION/textures/blocks")
        val itemDir = File("$ASSET_LOCATION/textures/items")

        var blockTexturesList = (blockDir.listFiles()?: arrayOf()).map { it.name }.toMutableSet()
        var itemTexturesList = (itemDir.listFiles()?: arrayOf()).map { it.name }.toMutableSet()

        blockTexturesList.removeAll(blockTextures)
        itemTexturesList.removeAll(itemTextures.map{it.substring(4)})
        blockTexturesList = blockTexturesList.filterNot{it.endsWith(".xcf")}.toMutableSet()
        itemTexturesList = itemTexturesList.filterNot{it.endsWith(".xcf")}.toMutableSet()
        blockTexturesList = blockTexturesList.filterNot{it.contains("computerprobe") || it.contains("elnto") || it.contains("relay") || it.contains("current")}.toMutableSet()

        println("DOOT DOOT")
        println(blockTexturesList.toSortedSet())
        println(itemTexturesList.toSortedSet())
    }
}
