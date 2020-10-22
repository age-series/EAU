package org.ja13.eau

open class CommonProxy {
    // Client stuff
    open fun registerRenderers() {
        // Nothing here as the server doesn't render graphics!
    }

    companion object {
        const val CABLE_PNG = "/mods/eln/sprites/CABLE.PNG"
        const val CABLENODE_PNG = "/mods/eln/sprites/CABLENODE.PNG"
        const val THERMALCABLE_PNG = "/mods/eln/sprites/TEX_THERMALCABLEBASE.PNG"
    }
}
