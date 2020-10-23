package org.ja13.eau;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.client.ClientKeyHandler;
import org.ja13.eau.client.SoundLoader;
import org.ja13.eau.crafting.CraftingRegistry;
import org.ja13.eau.generic.GenericCreativeTab;
import org.ja13.eau.generic.GenericItemUsingDamageDescriptor;
import org.ja13.eau.generic.GenericItemUsingDamageDescriptorWithComment;
import org.ja13.eau.generic.SharedItem;
import org.ja13.eau.ghost.GhostBlock;
import org.ja13.eau.ghost.GhostManager;
import org.ja13.eau.ghost.GhostManagerNbt;
import org.ja13.eau.item.CopperCableDescriptor;
import org.ja13.eau.item.GraphiteDescriptor;
import org.ja13.eau.item.MiningPipeDescriptor;
import org.ja13.eau.item.TreeResin;
import org.ja13.eau.item.electricalinterface.ItemEnergyInventoryProcess;
import org.ja13.eau.item.electricalitem.OreScannerTasks;
import org.ja13.eau.item.electricalitem.PortableOreScannerItem.RenderStorage.OreScannerConfigElement;
import org.ja13.eau.misc.FunctionTable;
import org.ja13.eau.misc.IConfigSharing;
import org.ja13.eau.misc.LiveDataManager;
import org.ja13.eau.misc.Obj3DFolder;
import org.ja13.eau.misc.RecipesList;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.Version;
import org.ja13.eau.misc.WindProcess;
import org.ja13.eau.node.NodeBlockEntity;
import org.ja13.eau.node.NodeManager;
import org.ja13.eau.node.NodeManagerNbt;
import org.ja13.eau.node.NodeServer;
import org.ja13.eau.node.six.SixNode;
import org.ja13.eau.node.six.SixNodeBlock;
import org.ja13.eau.node.six.SixNodeCacheStd;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.node.six.SixNodeItem;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeBlock;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import org.ja13.eau.node.transparent.TransparentNodeEntityWithFluid;
import org.ja13.eau.node.transparent.TransparentNodeItem;
import org.ja13.eau.ore.OreBlock;
import org.ja13.eau.ore.OreItem;
import org.ja13.eau.packets.GhostNodeWailaRequestPacket;
import org.ja13.eau.packets.GhostNodeWailaRequestPacketHandler;
import org.ja13.eau.packets.GhostNodeWailaResponsePacket;
import org.ja13.eau.packets.GhostNodeWailaResponsePacketHandler;
import org.ja13.eau.packets.SixNodeWailaRequestPacket;
import org.ja13.eau.packets.SixNodeWailaRequestPacketHandler;
import org.ja13.eau.packets.SixNodeWailaResponsePacket;
import org.ja13.eau.packets.SixNodeWailaResponsePacketHandler;
import org.ja13.eau.packets.TransparentNodeRequestPacket;
import org.ja13.eau.packets.TransparentNodeRequestPacketHandler;
import org.ja13.eau.packets.TransparentNodeResponsePacket;
import org.ja13.eau.packets.TransparentNodeResponsePacketHandler;
import org.ja13.eau.registry.BlockRegistry;
import org.ja13.eau.registry.ItemRegistry;
import org.ja13.eau.registry.SixNodeRegistry;
import org.ja13.eau.registry.TransparentNodeRegistry;
import org.ja13.eau.server.ConsoleListener;
import org.ja13.eau.server.DelayedBlockRemove;
import org.ja13.eau.server.DelayedTaskManager;
import org.ja13.eau.server.OreRegenerate;
import org.ja13.eau.server.PlayerManager;
import org.ja13.eau.server.SaveConfig;
import org.ja13.eau.server.ServerEventListener;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.Simulator;
import org.ja13.eau.sim.ThermalLoadInitializer;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.simplenode.computerprobe.ComputerProbeBlock;
import org.ja13.eau.simplenode.energyconverter.EnergyConverterElnToOtherBlock;
import org.ja13.eau.sixnode.PortableNaNDescriptor;
import org.ja13.eau.sixnode.electricaldatalogger.DataLogsPrintDescriptor;
import org.ja13.eau.sixnode.electriccable.ElectricCableDescriptor;
import org.ja13.eau.sixnode.lampsocket.LightBlock;
import org.ja13.eau.sixnode.lampsocket.LightBlockEntity;
import org.ja13.eau.sixnode.lampsupply.LampSupplyElement;
import org.ja13.eau.sixnode.modbusrtu.ModbusTcpServer;
import org.ja13.eau.sixnode.tutorialsign.TutorialSignElement;
import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalSpot;
import org.ja13.eau.sixnode.wirelesssignal.tx.WirelessSignalTxElement;
import org.ja13.eau.transparentnode.computercraftio.PeripheralHandler;
import org.ja13.eau.transparentnode.electricalfurnace.ElectricalFurnaceDescriptor;
import org.ja13.eau.transparentnode.teleporter.TeleporterElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static org.ja13.eau.i18n.I18N.TR;
import static org.ja13.eau.i18n.I18N.tr;
import static org.ja13.eau.sim.mna.misc.MnaConst.cableResistance;

@Mod(modid = EAU.MODID, name = EAU.NAME, version = "@VERSION@")
public class EAU {
    // Mod information (override from 'mcmod.info' file)
    public final static String MODID = "EAU";
    public final static String NAME = "Electrical Age Unleashed";
    public final static String MODDESC = "Electricity in your base!";
    public final static String URL = "https://eau.ja13.org";
    public final static String UPDATE_URL = "https://github.com/jrddunbr/EAU/releases";
    public final static String SRC_URL = "https://github.com/jrddunbr/EAU";
    public final static String[] AUTHORS = {"jrddunbr"};
    public static final String channelName = "EAU";

    // The instance of your mod that Forge uses.
    @Instance("EAU")
    public static EAU instance;

    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide = "org.ja13.eau.client.ClientProxy", serverSide = "org.ja13.eau.CommonProxy")
    public static CommonProxy proxy;

    public static final double solarPanelBasePower = 65.0;
    public static ArrayList<IConfigSharing> configShared = new ArrayList<>();
    public static SimpleNetworkWrapper elnNetwork;
    public static final byte packetPlayerKey = 14;
    public static final byte packetNodeSingleSerialized = 15;
    public static final byte packetPublishForNode = 16;
    public static final byte packetOpenLocalGui = 17;
    public static final byte packetForClientNode = 18;
    public static final byte packetPlaySound = 19;
    public static final byte packetDestroyUuid = 20;
    public static final byte packetClientToServerConnection = 21;
    public static final byte packetServerToClientInfo = 22;
    public static PacketHandler packetHandler;
    public static NodeServer nodeServer;
    public static LiveDataManager clientLiveDataManager;
    public static ClientKeyHandler clientKeyHandler;
    public static SaveConfig saveConfig;
    public static GhostManager ghostManager;
    public static GhostManagerNbt ghostManagerNbt;
    public static NodeManager nodeManager;
    public static PlayerManager playerManager;
    public static ModbusTcpServer modbusServer;
    public static NodeManagerNbt nodeManagerNbt;
    public static Simulator simulator = null;
    public static DelayedTaskManager delayedTask;
    public static ItemEnergyInventoryProcess itemEnergyInventoryProcess;

    public static CreativeTabs cableTab;
    public static CreativeTabs blockTab;
    public static CreativeTabs itemTab;

    public static Item cableTabIcon;
    public static Item blockTabIcon;
    public static Item itemTabIcon;

    public static double fuelGeneratorTankCapacity = 20 * 60;
    public static GenericItemUsingDamageDescriptor multiMeterElement,
        thermometerElement, allMeterElement;
    public static GenericItemUsingDamageDescriptor configCopyToolElement;
    public static TreeResin treeResin;
    public static MiningPipeDescriptor miningPipeDescriptor;
    public static DataLogsPrintDescriptor dataLogsPrintDescriptor;
    public static GenericItemUsingDamageDescriptorWithComment tinIngot, copperIngot,
        silverIngot, plumbIngot, tungstenIngot;
    public static GenericItemUsingDamageDescriptorWithComment dustTin,
        dustCopper, dustSilver;
    public static final HashMap<String, ItemStack> dictionnaryOreFromMod = new HashMap<>();

    public static CableRenderDescriptor uninsulatedLowCurrentRender;
    public static CableRenderDescriptor uninsulatedMediumCurrentRender;
    public static CableRenderDescriptor uninsulatedHighCurrentRender;
    public static CableRenderDescriptor smallInsulationLowCurrentRender;
    public static CableRenderDescriptor smallInsulationMediumCurrentRender;
    public static CableRenderDescriptor smallInsulationHighCurrentRender;
    public static CableRenderDescriptor mediumInsulationLowCurrentRender;
    public static CableRenderDescriptor mediumInsulationMediumCurrentRender;
    public static CableRenderDescriptor bigInsulationLowCurrentRender;

    public static final double gateOutputCurrent = 0.100;
    public static final double cableHeatingTime = 30;
    public static final double cableWarmLimit = 130;
    public static final double cableThermalConductionTao = 0.5;
    public static final ThermalLoadInitializer cableThermalLoadInitializer = new ThermalLoadInitializer(
        cableWarmLimit, -100, cableHeatingTime, cableThermalConductionTao);
    public static final ThermalLoadInitializer sixNodeThermalLoadInitializer = new ThermalLoadInitializer(
        cableWarmLimit, -100, cableHeatingTime, 1000);
    public static int wirelessTxRange = 32;
    public static FunctionTable batteryVoltageFunctionTable;
    public static ArrayList<ItemStack> furnaceList = new ArrayList<>();
    public static RecipesList maceratorRecipes = new RecipesList();
    public static RecipesList compressorRecipes = new RecipesList();
    public static RecipesList plateMachineRecipes = new RecipesList();
    public static RecipesList magnetiserRecipes = new RecipesList();
    public static  double incandescentLampLife;
    public static  double economicLampLife;
    public static  double carbonLampLife;
    public static  double ledLampLife;
    public static boolean ledLampInfiniteLife = false;
    public static Item swordCopper, hoeCopper, shovelCopper, pickaxeCopper, axeCopper;
    public static ItemArmor helmetCopper, plateCopper, legsCopper, bootsCopper;
    public static ItemArmor helmetECoal, plateECoal, legsECoal, bootsECoal;
    public static SharedItem sharedItem;
    public static SharedItem sharedItemStackOne;
    public static ItemStack wrenchItemStack;
    public static SixNodeBlock sixNodeBlock;
    public static TransparentNodeBlock transparentNodeBlock;
    public static OreBlock oreBlock;
    public static GhostBlock ghostBlock;
    public static LightBlock lightBlock;
    public static SixNodeItem sixNodeItem;
    public static TransparentNodeItem transparentNodeItem;
    public static OreItem oreItem;
    public static String analyticsURL = "";
    public static boolean analyticsPlayerUUIDOptIn = false;
    public static WindProcess wind;
    public static ServerEventListener serverEventListener;
    public static EnergyConverterElnToOtherBlock elnToOtherBlockLvu;
    public static EnergyConverterElnToOtherBlock elnToOtherBlockMvu;
    public static EnergyConverterElnToOtherBlock elnToOtherBlockHvu;
    public static double electricalFrequency, thermalFrequency;
    public static int electricalInterSystemOverSampling;
    public static CopperCableDescriptor copperCableDescriptor;
    public static GraphiteDescriptor GraphiteDescriptor;

    public static ElectricCableDescriptor uninsulatedLowCurrentCopperCable;
    public static ElectricCableDescriptor uninsulatedMediumCurrentCopperCable;
    public static ElectricCableDescriptor uninsulatedHighCurrentCopperCable;
    public static ElectricCableDescriptor smallInsulationLowCurrentCopperCable;
    public static ElectricCableDescriptor smallInsulationMediumCurrentCopperCable;
    public static ElectricCableDescriptor smallInsulationHighCurrentCopperCable;
    public static ElectricCableDescriptor mediumInsulationLowCurrentCopperCable;
    public static ElectricCableDescriptor mediumInsulationMediumCurrentCopperCable;
    public static ElectricCableDescriptor bigInsulationLowCurrentCopperCable;
    public static ElectricCableDescriptor uninsulatedLowCurrentAluminumCable;
    public static ElectricCableDescriptor uninsulatedMediumCurrentAluminumCable;
    public static ElectricCableDescriptor uninsulatedHighCurrentAluminumCable;
    public static ElectricCableDescriptor smallInsulationLowCurrentAluminumCable;
    public static ElectricCableDescriptor smallInsulationMediumCurrentAluminumCable;
    public static ElectricCableDescriptor smallInsulationHighCurrentAluminumCable;
    public static ElectricCableDescriptor mediumInsulationLowCurrentAluminumCable;
    public static ElectricCableDescriptor mediumInsulationMediumCurrentAluminumCable;
    public static ElectricCableDescriptor bigInsulationLowCurrentAluminumCable;

    public static PortableNaNDescriptor portableNaNDescriptor = null;
    public static CableRenderDescriptor stdPortableNaN = null;
    public static OreRegenerate oreRegenerate;
    public static final Obj3DFolder obj = new Obj3DFolder();
    public static boolean oredictTungsten, oredictChips;
    public static boolean genCopper, genLead, genTungsten;
    public static String dictTungstenOre, dictTungstenDust, dictTungstenIngot;
    public static String dictCheapChip, dictAdvancedChip;
    public static final ArrayList<OreScannerConfigElement> oreScannerConfig = new ArrayList<>();
    public static boolean modbusEnable = false;
    public static int modbusPort;
    public static double xRayScannerRange;
    public static boolean addOtherModOreToXRay;
    public static boolean xRayScannerCanBeCrafted = true;
    public static boolean forceOreRegen;
    public static boolean explosionEnable;
    public static boolean debugEnabled = false;
    public static boolean versionCheckEnabled = true;
    public static boolean analyticsEnabled = true;
    public static String playerUUID = null;
    public static double heatTurbinePowerFactor = 1;
    public static double solarPanelPowerFactor = 1;
    public static double windTurbinePowerFactor = 1;
    public static double fuelGeneratorPowerFactor = 1;
    public static double fuelHeatFurnacePowerFactor = 1;
    public static int autominerRange = 10;
    public static boolean killMonstersAroundLamps;
    public static int killMonstersAroundLampsRange;
    public static double stdBatteryHalfLife = 2 * Utils.minecraftDay;
    public static double batteryCapacityFactor = 1.;
    public static boolean wailaEasyMode = false;
    public static double shaftEnergyFactor = 0.05;
    public static double fuelHeatValueFactor = 0.0000675;
    public static int plateConversionRatio;
    public static boolean noSymbols = false;
    public static boolean noVoltageBackground = false;
    public static double maxSoundDistance = 16;
    public static double cablePowerFactor;
    public static boolean allowSwingingLamps = true;
    public static boolean enableFestivities = true;
    public static FMLEventChannel eventChannel;
    public static boolean ComputerProbeEnable;
    public static boolean ElnToOtherEnergyConverterEnable;
    public static HashSet<String> oreNames = new HashSet<>();
    public static ElectricalFurnaceDescriptor electricalFurnace;
    public static ComputerProbeBlock computerProbeBlock;
    public static boolean cableConnectionNodes;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Utils.println("EAU Enters PreInit");

        elnNetwork = NetworkRegistry.INSTANCE.newSimpleChannel("eau");
        elnNetwork.registerMessage(TransparentNodeRequestPacketHandler.class, TransparentNodeRequestPacket.class, 1, Side.SERVER);
        elnNetwork.registerMessage(TransparentNodeResponsePacketHandler.class, TransparentNodeResponsePacket.class, 2, Side.CLIENT);
        elnNetwork.registerMessage(GhostNodeWailaRequestPacketHandler.class, GhostNodeWailaRequestPacket.class, 3, Side.SERVER);
        elnNetwork.registerMessage(GhostNodeWailaResponsePacketHandler.class, GhostNodeWailaResponsePacket.class, 4, Side.CLIENT);
        elnNetwork.registerMessage(SixNodeWailaRequestPacketHandler.class, SixNodeWailaRequestPacket.class, 5, Side.SERVER);
        elnNetwork.registerMessage(SixNodeWailaResponsePacketHandler.class, SixNodeWailaResponsePacket.class, 6, Side.CLIENT);

        // Update ModInfo by code
        ModMetadata meta = event.getModMetadata();
        meta.modId = MODID;
        meta.version = Version.getVersionName();
        meta.name = NAME;
        meta.description = tr("mod.meta.desc");
        meta.url = URL;
        meta.updateUrl = UPDATE_URL;
        meta.authorList = Arrays.asList(AUTHORS);
        meta.autogenerated = false; // Force to update from code

        Utils.println(Version.print());

        Side side = FMLCommonHandler.instance().getEffectiveSide();
        if (side == Side.CLIENT)
            //noinspection InstantiationOfUtilityClass
            MinecraftForge.EVENT_BUS.register(new SoundLoader());

        ConfigHelper.Companion.loadConfiguration(event);

        eventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(channelName);

        simulator = new Simulator(0.05, 1 / electricalFrequency, electricalInterSystemOverSampling, 1 / thermalFrequency);
        nodeManager = new NodeManager("caca");
        ghostManager = new GhostManager("caca2");
        delayedTask = new DelayedTaskManager();
        playerManager = new PlayerManager();
        oreRegenerate = new OreRegenerate();
        nodeServer = new NodeServer();
        clientLiveDataManager = new LiveDataManager();
        packetHandler = new PacketHandler();
        instance = this;

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        Item itemCableTab = new Item()
            .setUnlocalizedName("eau:itemcabletab")
            .setTextureName("eau:itemcabletab");
        GameRegistry.registerItem(itemCableTab, "eau.itemCableTab");
        Item itemBlockTab = new Item()
            .setUnlocalizedName("eau:itemblocktab")
            .setTextureName("eau:itemblocktab");
        GameRegistry.registerItem(itemBlockTab, "eau.itemBlockTab");
        Item itemItemTab = new Item()
            .setUnlocalizedName("eau:itemitemtab")
            .setTextureName("eau:itemitemtab");
        GameRegistry.registerItem(itemItemTab, "eau.itemItemTab");

        cableTab = new GenericCreativeTab("EAU_6_Sided_Components", itemCableTab);
        blockTab = new GenericCreativeTab("EAU_Blocks", itemBlockTab);
        itemTab = new GenericCreativeTab("EAU_Items_and_Tools", itemItemTab);

        oreBlock = (OreBlock) new OreBlock().setCreativeTab(blockTab).setBlockName("eau:OreEAU").setBlockTextureName("eau:OreEAU");
        sharedItem = (SharedItem) new SharedItem()
            .setCreativeTab(itemTab).setMaxStackSize(64)
            .setUnlocalizedName("sharedItem");
        sharedItemStackOne = (SharedItem) new SharedItem()
            .setCreativeTab(itemTab).setMaxStackSize(1)
            .setUnlocalizedName("sharedItemStackOne");
        transparentNodeBlock = (TransparentNodeBlock) new TransparentNodeBlock(
            Material.iron,
            TransparentNodeEntity.class)
            .setCreativeTab(blockTab)
            .setBlockTextureName("iron_block");
        sixNodeBlock = (SixNodeBlock) new SixNodeBlock(
            Material.plants, SixNodeEntity.class)
            .setCreativeTab(cableTab)
            .setBlockTextureName("iron_block");
        ghostBlock = (GhostBlock) new GhostBlock().setBlockTextureName("iron_block");
        lightBlock = new LightBlock();
        obj.loadAllElnModels();
        GameRegistry.registerItem(sharedItem, "EAU.sharedItem");
        GameRegistry.registerItem(sharedItemStackOne, "EAU.sharedItemStackOne");
        GameRegistry.registerBlock(ghostBlock, "EAU.ghostBlock");
        GameRegistry.registerBlock(lightBlock, "EAU.lightBlock");
        GameRegistry.registerBlock(sixNodeBlock, SixNodeItem.class, "EAU.SixNode");
        GameRegistry.registerBlock(transparentNodeBlock, TransparentNodeItem.class, "EAU.TransparentNode");
        GameRegistry.registerBlock(oreBlock, OreItem.class, "EAU.Ore");
        TileEntity.addMapping(TransparentNodeEntity.class, "EAUTransparentNodeEntity");
        TileEntity.addMapping(TransparentNodeEntityWithFluid.class, "EAUTransparentNodeEntityWF");
        // TileEntity.addMapping(TransparentNodeEntityWithSiededInv.class, "TransparentNodeEntityWSI");
        TileEntity.addMapping(SixNodeEntity.class, "EAUSixNodeEntity");
        TileEntity.addMapping(LightBlockEntity.class, "EAULightBlockEntity");
        NodeManager.registerUuid(sixNodeBlock.getNodeUuid(), SixNode.class);
        NodeManager.registerUuid(transparentNodeBlock.getNodeUuid(), TransparentNode.class);
        sixNodeItem = (SixNodeItem) Item.getItemFromBlock(sixNodeBlock);
        transparentNodeItem = (TransparentNodeItem) Item.getItemFromBlock(transparentNodeBlock);
        oreItem = (OreItem) Item.getItemFromBlock(oreBlock);
        SixNode.sixNodeCacheList.add(new SixNodeCacheStd());
        BlockRegistry.Companion.registerBlocks();
        SixNodeRegistry.Companion.register();
        TransparentNodeRegistry.Companion.register();
        ItemRegistry.Companion.register();
    }

    @EventHandler
    public void modsLoaded(FMLPostInitializationEvent event) {
        Other.check();
        if (Other.ccLoaded) {
            PeripheralHandler.register();
        }
        //CraftingRegistry.Companion.recipeMaceratorModOres();
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        Collections.addAll(oreNames, OreDictionary.getOreNames());
        CraftingRegistry.Companion.registerCrafting();
        proxy.registerRenderers();
        TR("itemGroup.EAU");
        CraftingRegistry.Companion.checkRecipe();
        FMLInterModComms.sendMessage("Waila", "register", "org.ja13.eau.integration.waila.WailaIntegration.callbackRegister");
        Utils.println("EAU init done");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        serverEventListener = new ServerEventListener();
        //ImageList.INSTANCE.notifyOfUnusedTextures(); // Used to find unused images and stuff.
    }

    @EventHandler
    /* Remember to use the right event! */
    public void onServerStopped(FMLServerStoppedEvent ev) {
        TutorialSignElement.resetBalise();
        if (modbusServer != null) {
            modbusServer.destroy();
            modbusServer = null;
        }
        LightBlockEntity.observers.clear();
        NodeBlockEntity.clientList.clear();
        TeleporterElement.teleporterList.clear();
        IWirelessSignalSpot.spots.clear();
        playerManager.clear();
        clientLiveDataManager.stop();
        nodeManager.clear();
        ghostManager.clear();
        saveConfig = null;
        modbusServer = null;
        oreRegenerate.clear();
        delayedTask.clear();
        DelayedBlockRemove.clear();
        serverEventListener.clear();
        nodeServer.stop();
        simulator.stop();
        //tileEntityDestructor.clear();
        LampSupplyElement.channelMap.clear();
        WirelessSignalTxElement.channelMap.clear();
    }

    @EventHandler
    public void onServerStart(FMLServerAboutToStartEvent ev) {
        modbusServer = new ModbusTcpServer(modbusPort);
        TeleporterElement.teleporterList.clear();
        LightBlockEntity.observers.clear();
        WirelessSignalTxElement.channelMap.clear();
        LampSupplyElement.channelMap.clear();
        playerManager.clear();
        clientLiveDataManager.start();
        simulator.init();
        simulator.addSlowProcess(wind = new WindProcess());
        simulator.addSlowProcess(itemEnergyInventoryProcess = new ItemEnergyInventoryProcess());
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent ev) {
        {
            MinecraftServer server = FMLCommonHandler.instance()
                .getMinecraftServerInstance();
            WorldServer worldServer = server.worldServers[0];
            ghostManagerNbt = (GhostManagerNbt) worldServer.mapStorage.loadData(
                GhostManagerNbt.class, "EAUGhostManager");
            if (ghostManagerNbt == null) {
                ghostManagerNbt = new GhostManagerNbt("EAUGhostManager");
                worldServer.mapStorage.setData("EAUGhostManager", ghostManagerNbt);
            }
            saveConfig = (SaveConfig) worldServer.mapStorage.loadData(
                SaveConfig.class, "EAUSaveConfig");
            if (saveConfig == null) {
                saveConfig = new SaveConfig("EAUSaveConfig");
                worldServer.mapStorage.setData("EAUSaveConfig", saveConfig);
            }
            nodeManagerNbt = (NodeManagerNbt) worldServer.mapStorage.loadData(
                NodeManagerNbt.class, "EAUNodeManager");
            if (nodeManagerNbt == null) {
                nodeManagerNbt = new NodeManagerNbt("EAUNodeManager");
                worldServer.mapStorage.setData("EAUNodeManager", nodeManagerNbt);
            }
            nodeServer.init();
        }

        {
            MinecraftServer s = MinecraftServer.getServer();
            ICommandManager command = s.getCommandManager();
            ServerCommandManager manager = (ServerCommandManager) command;
            manager.registerCommand(new ConsoleListener());
        }
        OreScannerTasks.Companion.regenOreScannerFactors();
    }

    public static double getSmallRs() {
        return cableResistance;
    }
    public static void applySmallRs(ElectricalLoad aLoad) {
        aLoad.setRs(cableResistance);
    }
    public static void applySmallRs(Resistor r) {
        r.setR(cableResistance);
    }
    public boolean isDevelopmentRun() {
        return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }
}
