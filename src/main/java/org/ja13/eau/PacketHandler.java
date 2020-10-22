package org.ja13.eau;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.channel.ChannelHandler.Sharable;
import org.ja13.eau.client.ClientKeyHandler;
import org.ja13.eau.client.ClientProxy;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.IConfigSharing;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.node.INodeEntity;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.NodeManager;
import org.ja13.eau.server.PlayerManager;
import org.ja13.eau.sound.SoundClient;
import org.ja13.eau.sound.SoundCommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;

import java.io.*;

@Sharable
public class PacketHandler {

    public PacketHandler() {
        EAU.eventChannel.register(this);
    }


    @SubscribeEvent
    public void onServerPacket(ServerCustomPacketEvent event) {
        FMLProxyPacket packet = event.packet;
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet.payload().array()));
        NetworkManager manager = event.manager;
        EntityPlayer player = ((NetHandlerPlayServer) event.handler).playerEntity; // EntityPlayerMP

        packetRx(stream, manager, player);
    }


    public void packetRx(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        try {
            switch (stream.readByte()) {
                case EAU.packetPlayerKey:
                    packetPlayerKey(stream, manager, player);
                    break;
                case EAU.packetNodeSingleSerialized:
                    packetNodeSingleSerialized(stream, manager, player);
                    break;
                case EAU.packetPublishForNode:
                    packetForNode(stream, manager, player);
                    break;
                case EAU.packetForClientNode:
                    packetForClientNode(stream, manager, player);
                    break;
                case EAU.packetOpenLocalGui:
                    packetOpenLocalGui(stream, manager, player);
                    break;
                case EAU.packetPlaySound:
                    packetPlaySound(stream, manager, player);
                    break;
                case EAU.packetDestroyUuid:
                    packetDestroyUuid(stream, manager, player);
                    break;
                case EAU.packetClientToServerConnection:
                    packetNewClient(manager, player);
                    break;
                case EAU.packetServerToClientInfo:
                    packetServerInfo(stream, manager, player);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void packetNewClient(NetworkManager manager, EntityPlayer player) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream(64);
        DataOutputStream stream = new DataOutputStream(bos);

        try {
            stream.writeByte(EAU.packetServerToClientInfo);
            for (IConfigSharing c : EAU.configShared) {
                c.serializeConfig(stream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Utils.sendPacketToClient(bos, (EntityPlayerMP) player);
    }

    private void packetServerInfo(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        for (IConfigSharing c : EAU.configShared) {
            try {
                c.deserialize(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void packetDestroyUuid(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        try {
            ClientProxy.uuidManager.kill(stream.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void packetPlaySound(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        try {
            if (stream.readByte() != player.dimension)
                return;
            SoundClient.play(SoundCommand.fromStream(stream, player.worldObj));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void packetOpenLocalGui(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        EntityPlayer clientPlayer = player;
        try {
            clientPlayer.openGui(EAU.instance, stream.readInt(),
                clientPlayer.worldObj, stream.readInt(), stream.readInt(),
                stream.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void packetForNode(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        try {
            Coordonate coordonate = new Coordonate(stream.readInt(),
                stream.readInt(), stream.readInt(), stream.readByte());

            NodeBase node = NodeManager.instance.getNodeFromCoordonate(coordonate);
            if (node != null && node.getNodeUuid().equals(stream.readUTF())) {
                node.networkUnserialize(stream, (EntityPlayerMP) player);
            } else {
                Utils.println("packetForNode node found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void packetForClientNode(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        EntityPlayer clientPlayer = player;
        int x, y, z, dimention;
        try {

            x = stream.readInt();
            y = stream.readInt();
            z = stream.readInt();
            dimention = stream.readByte();


            if (clientPlayer.dimension == dimention) {
                TileEntity entity = clientPlayer.worldObj.getTileEntity(x, y, z);
                if (entity != null && entity instanceof INodeEntity) {
                    INodeEntity node = (INodeEntity) entity;
                    if (node.getNodeUuid().equals(stream.readUTF())) {
                        node.serverPacketUnserialize(stream);
                        if (0 != stream.available()) {
                            Utils.println("0 != stream.available()");
                        }
                    } else {
                        Utils.println("Wrong node UUID warning");
                        int dataSkipLength = stream.readByte();
                        for (int idx = 0; idx < dataSkipLength; idx++) {
                            stream.readByte();
                        }
                    }
                }
            } else
                Utils.println("No node found for " + x + " " + y + " " + z);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void packetNodeSingleSerialized(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        try {
            EntityPlayer clientPlayer = player;
            int x, y, z, dimention;
            x = stream.readInt();
            y = stream.readInt();
            z = stream.readInt();
            dimention = stream.readByte();

            if (clientPlayer.dimension == dimention) {
                TileEntity entity = clientPlayer.worldObj.getTileEntity(x, y, z);
                if (entity != null && entity instanceof INodeEntity) {
                    INodeEntity node = (INodeEntity) entity;
                    if (node.getNodeUuid().equals(stream.readUTF())) {
                        node.serverPublishUnserialize(stream);
                        if (0 != stream.available()) {
                            Utils.println("0 != stream.available()");

                        }
                    } else {
                        Utils.println("Wrong node UUID warning");
                        int dataSkipLength = stream.readByte();
                        for (int idx = 0; idx < dataSkipLength; idx++) {
                            stream.readByte();
                        }
                    }
                } else
                    Utils.println("No node found for " + x + " " + y + " " + z);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void packetPlayerKey(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        byte id;
        try {
            id = stream.readByte();
            boolean state = stream.readBoolean();

            if (id == ClientKeyHandler.wrenchId) {
                PlayerManager.PlayerMetadata metadata = EAU.playerManager.get(playerMP);
                metadata.setInteractEnable(state);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
