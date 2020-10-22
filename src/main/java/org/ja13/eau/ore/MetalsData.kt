package org.ja13.eau.ore

enum class OreInfo(val type: String, val chemistry: List<String>?) {
    Cryolite("Cryolite", listOf("Na3AlF6")),
    Bauxite("Bauxite", listOf()),
    Corundum("Corundum", listOf("Al2O3")),
    Bismuthinite("Bismuthinite", listOf("Bi2S3")),
    Borax("Borax", listOf("Na2B4O7")),
    Kernite("Kernite", listOf("Na2B4O7")),
    Flourite("Flourite", listOf("CaF2")),
    Graphite("Graphite", listOf("C")),
    Anthracite_Coal("Anthracite Coal", listOf("C")),
    Bituminous_Coal("Bituminous Coal", listOf("C")),
    Lignite("Lignite", listOf("C")),
    // Diamond
    Eskolaite("Eskolaite", listOf("Cr2O3")),
    Chromite("Chromite", listOf("FeCr2O4", "MgCr2O4")),
    Spherocobaltite("Spherocobaltite", listOf("CoCO3")),
    Cattierite("Cattierite", listOf("CoS2")),
    Azurite("Azurite", listOf("Cu3(CO3)2(OH)2")),
    Malachite("Malachite", listOf("Cu2CO3(OH)2")),
    Native_Copper("Native Copper", listOf("Cu")),
    Chalcocite("Chalcocite", listOf("Cu2S")),
    Chalcopyrite("Chalcopyrite", listOf("CuFeS2")),
    // Native Gold
    Siderite("Siderite", listOf("FeCO3")),
    Pyrite("Pyrite", listOf("FeS")),
    Hematite("Hematite", listOf("Fe2O3")), // Oredict this with "Iron Ore"?
    Magnetite("Magnetite", listOf("Fe3O4")),
    Limonite("Limonite", listOf("FeO(OH)")),
    Cerussite("Cerussite", listOf("PbCO3")),
    Galena("Galena", listOf("PbS")),
    Spodumene("Spodumene", listOf("LiAl(SiO3)2")),
    Petalite("Petalite", listOf("LiAlSi4O10")),
    Lepidolite("Lepidolite", listOf()),
    Rhodochrosite("Rhodochrosite", listOf("MnCO3")),
    Pyrolusite("Pyrolusite", listOf("MnO2")),
    Psilomelane("Psilomelane", listOf("H2O2Mn5O10", "Ba2Mn5O10")),
    Cinnabar("Cinnabar", listOf("HgS")),
    Native_Sulfur("Native Sulfur", listOf("S8")),
    Pentlandite("Pentlandite", listOf("(Fe9S8", "Ni9S8")),
    Millerite("Millerite", listOf("NiS")),
    Native_Platinum("Native Platinum", listOf("Pt")),
    Sylvite("Sylvite", listOf("KCl")),
    Quartz("Quartz", listOf("SiO2")),
    Native_Silver("Native Silver", listOf("Ag")),
    Acanthite("Acanthite", listOf("Ag2S")),
    Halite("Halite", listOf("NaCl")),
    Tantalite("Tantalite", listOf("FeTa2O6", "MnTa2O6")),
    Stannite("Stannite", listOf("Cu2FeSnS4")),
    Cassiterite("Cassiterite", listOf("SnO2")),
    Rutile("Rutile", listOf("TiO2")),
    Titanite("Titanite", listOf("CaTiSiO5")),
    Ilmenite("Ilmenite", listOf("FeTiO3")),
    Wolframite("Wolframite", listOf("FeWO4", "MnWO4")),
    Scheelite("Scheelite", listOf("CaWO4")),
    Shpalerite("Shpalerite", listOf("ZnS", "FeS")),
    Smithsonite("Smithsonite", listOf("ZnCO3")),
}

enum class Elements(val type: String, val chemistry: String?) {
    LITHIUM("lithium", "Li"),
    BORON("boron", "B"),
    CARBON("carbon", "C"),
    SODIUM("sodium", "Na"),
    ALUMINUM("aluminum", "Al"),
    SILICON("silicon", "Si"),
    SULFUR("sulfur", "S"),
    POTASSIUM("potassium", "K"),
    CALCIUM("calcium", "Ca"),
    CHROMIUM("chromium", "Cr"),
    MANGANESE("manganese", "Mn"),
    IRON("iron", "Fe"),
    COBALT("cobalt", "Co"),
    NICKEL("nickel", "Ni"),
    COPPER("copper", "Cu"),
    ZINC("zinc", "Zn"),
    GALLIUM("gallium", "Ga"),
    GERMANIUM("germanium", "Ge"),
    SILVER("silver", "Ag"),
    CADMIUM("cadmium", "Cd"),
    INDIUM("indium", "In"),
    TIN("tin", "Sn"),
    TANTALUM("tantalum", "Ta"),
    TUNGSTEN("tungsten", "W"),
    PLATINUM("platinum", "Pt"),
    GOLD("gold", "Au"),
    MERCURY("mercury", "Hg"),
    LEAD("lead", "Pb"),
    BISMUTH("bismuth", "Bi"),
}

enum class DictTypes(val type: String) {

    // STANDARD MINECRAFT TYPES
    ORE("ore"),         // Standard ore blocks mined from Minecraft
    DUST("dust"),       // Typically smelted into Ingot
    INGOT("ingot"),     // Standard Ingot
    NUGGET("nugget"),   // 1/9 of Ingot
    GEM("gem"),         // Minecraft Diamonds, etc.
    BLOCK("block"),     // Minecraft Block (9 ingots)
    STONE("stone"),     // Various rock types (eg, Diorite)

    // SPECIAL TYPES :)
    STAMP_SAND("stampSand"),            // Stamped 'ore'
    DIRTY_ORE_GRAVEL("dirtyOreGravel"), // Crushed 'ore'
    DIRTY_ORE_SAND("dirtyOreSand"),     // "Dusted" 'ore'
    DIRTY_ORE_POWDER("dirtyOrePowder"), // "Powdered" 'ore'
    CLEAN_ORE_GRAVEL("cleanOreGravel"), // Free of impurities
    CLEAN_ORE_SAND("cleanOreSand"),     // "Dusted" free of impurities
    CLEAN_ORE_SLURRY("cleanOreSlurry"), // Wet ore slurry, free of impurities
    CLEAN_ORE_POWDER("cleanOrePowder"), // Clean dust of the metal you want, can be smelted to get ingots and nuggets. AKA 'dust'

    ORE_GRAVEL("oreRockGravel"),        // Ore Gravel
    ORE_SAND("oreSand"),                // Ore Sand

    ORE_NUGGETS("oreNuggets"),                  // Picked out some pure "enough" bits from a pile of gravel. Same quantity as a Minecraft nugget. Smelt to make proper Minecraft nugget.
    NATIVE_CHUNKS("nativeChunks"),              // Craft for 4 nuggets
    NATIVE_FLAKES("nativeFlakes"),              // Craft 9 for a nugget

    // These are registered directly since, they do not have ore contents (eg, they are not wetDepletedSandIron)
    //WET_DEPLETED_SAND("wetDepletedSand"),       // Dry it to get some Depleted sand.
    //DEPLETED_SAND("depletedSand"),              // Depleted Sand
    //DEPLETED_GRAVEL("depletedGravel"),          // Depleted Gravel
}

