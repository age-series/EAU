package org.ja13.eau

import cpw.mods.fml.common.Loader

class Other {
     companion object {
        @JvmField
        var ic2Loaded = false
        @JvmField
        var ocLoaded = false
        @JvmField
        var ccLoaded = false
        @JvmField
        var teLoaded = false
         @JvmStatic
        var elnToIc2ConversionRatio = 0.0
         @JvmStatic
        var elnToOcConversionRatio = 0.0
         @JvmStatic
        var elnToTeConversionRatio = 0.0

        const val modIdIc2 = "IC2"
        const val modIdOc = "OpenComputers"
        const val modIdTe = "Eln"
        const val modIdCc = "ComputerCraft"

        @JvmStatic
        fun check() {
            ic2Loaded = Loader.isModLoaded(modIdIc2)
            ocLoaded = Loader.isModLoaded(modIdOc)
            ccLoaded = Loader.isModLoaded(modIdCc)
            teLoaded = Loader.isModLoaded(modIdTe)
        }
    }
}
