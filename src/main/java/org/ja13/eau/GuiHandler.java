package org.ja13.eau;

import cpw.mods.fml.common.network.IGuiHandler;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.node.INodeEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GuiHandler implements IGuiHandler {

    INodeEntity getNodeEntity(World world, int x, int y, int z) {
        TileEntity e = world.getTileEntity(x, y, z);
        if (e == null || false == e instanceof INodeEntity) return null;
        return (INodeEntity) e;
    }

    // returns an instance of the Container you made earlier
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world,
                                      int x, int y, int z) {
        INodeEntity nodeEntity = getNodeEntity(world, x, y, z);
        if (nodeEntity == null) return null;
        Direction side = Direction.fromInt(id - nodeBaseOpen);
        Object container = nodeEntity.newContainer(side, player);
        if (container == null) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream stream = new DataOutputStream(bos);

                stream.writeByte(EAU.packetOpenLocalGui);
                stream.writeInt(id);
                stream.writeInt(x);
                stream.writeInt(y);
                stream.writeInt(z);
                Utils.sendPacketToClient(bos, (EntityPlayerMP) player);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return container;

    }

    public static final int genericOpen = 5977;
    public static final int nodeBaseOpen = 6935;

    // returns an instance of the Gui you made earlier
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world,
                                      int x, int y, int z) {
        if (id == genericOpen) {
            return UtilsClient.guiLastOpen;
        }

        if (id >= nodeBaseOpen && id <= nodeBaseOpen + 5) {
            INodeEntity nodeEntity = getNodeEntity(world, x, y, z);
            if (nodeEntity == null) return null;
            Direction side = Direction.fromInt(id - nodeBaseOpen);
            return nodeEntity.newGuiDraw(side, player);
        }

        return null;
    }
}
