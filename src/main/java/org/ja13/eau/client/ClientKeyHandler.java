package org.ja13.eau.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import org.ja13.eau.EAU;
import org.ja13.eau.misc.UtilsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.StatCollector;
import org.ja13.eau.EAU;
import org.ja13.eau.misc.UtilsClient;
import org.lwjgl.input.Keyboard;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientKeyHandler {

    static public final int openWikiId = 0;
    static public final int wrenchId = 1;
    static final String openWiki = "Open Wiki";
    static final String wrench = "Wrench";
    private static final int[] keyValues = {Keyboard.KEY_X, Keyboard.KEY_C};
    private static final String[] desc = {openWiki, wrench};
    public static final KeyBinding[] keys = new KeyBinding[desc.length];

    boolean[] states = new boolean[desc.length];

    Minecraft mc;

    public ClientKeyHandler() {
        mc = Minecraft.getMinecraft();

        for (int i = 0; i < desc.length; ++i) {
            if (i != 3)
                states[i] = false;
            keys[i] = new KeyBinding(desc[i], keyValues[i], StatCollector.translateToLocal("ElectricalAge"));
            ClientRegistry.registerKeyBinding(keys[i]);
        }
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        for (int i = 0; i < desc.length; ++i) {
            boolean s = keys[i].getIsKeyPressed();
            if (s == false) continue;
            if (states[i])
                setState(i, false);
            setState(i, true);
        }
    }

    @SubscribeEvent
    public void tick(ClientTickEvent event) {
        if (event.phase != Phase.START) return;
        for (int i = 0; i < desc.length; ++i) {
            boolean s = keys[i].getIsKeyPressed();
            if (s == false && states[i] == true) {
                setState(i, false);
            }
        }
    }

    void setState(int id, boolean state) {
        states[id] = state;

        ByteArrayOutputStream bos = new ByteArrayOutputStream(64);
        DataOutputStream stream = new DataOutputStream(bos);

        try {
            stream.writeByte(EAU.packetPlayerKey);
            stream.writeByte(id);
            stream.writeBoolean(state);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UtilsClient.sendPacketToServer(bos);
    }
}
