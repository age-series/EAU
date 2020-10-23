package org.ja13.eau.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import org.ja13.eau.CommonProxy;
import org.ja13.eau.EAU;
import org.ja13.eau.node.six.SixNodeEntity;
import org.ja13.eau.node.six.SixNodeRender;
import org.ja13.eau.node.transparent.TransparentNodeEntity;
import org.ja13.eau.node.transparent.TransparentNodeRender;
import org.ja13.eau.sixnode.tutorialsign.TutorialSignOverlay;
import org.ja13.eau.sound.SoundClientEventListener;

public class ClientProxy extends CommonProxy {

    public static UuidManager uuidManager;
    public static SoundClientEventListener soundClientEventListener;

    @Override
    public void registerRenderers() {
        new ClientPacketHandler();
        ClientRegistry.bindTileEntitySpecialRenderer(SixNodeEntity.class, new SixNodeRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TransparentNodeEntity.class, new TransparentNodeRender());

        MinecraftForgeClient.registerItemRenderer(EAU.transparentNodeItem, EAU.transparentNodeItem);
        MinecraftForgeClient.registerItemRenderer(EAU.sixNodeItem, EAU.sixNodeItem);
        MinecraftForgeClient.registerItemRenderer(EAU.sharedItem, EAU.sharedItem);
        MinecraftForgeClient.registerItemRenderer(EAU.sharedItemStackOne, EAU.sharedItemStackOne);

        EAU.clientKeyHandler = new ClientKeyHandler();
        FMLCommonHandler.instance().bus().register(EAU.clientKeyHandler);
        MinecraftForge.EVENT_BUS.register(new org.ja13.eau.mechanical.MotorOverlay());
        MinecraftForge.EVENT_BUS.register(new TutorialSignOverlay());
        uuidManager = new UuidManager();
        soundClientEventListener = new SoundClientEventListener(uuidManager);

        if (EAU.versionCheckEnabled)
            FMLCommonHandler.instance().bus().register(VersionCheckerHandler.getInstance());

        if (EAU.analyticsEnabled)
            FMLCommonHandler.instance().bus().register(AnalyticsHandler.getInstance());

        new FrameTime();
        new ConnectionListener();
    }
}
