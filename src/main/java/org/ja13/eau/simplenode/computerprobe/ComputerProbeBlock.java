package org.ja13.eau.simplenode.computerprobe;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.ja13.eau.node.simple.SimpleNode;
import org.ja13.eau.node.simple.SimpleNodeBlock;

public class ComputerProbeBlock extends SimpleNodeBlock {

    private final IIcon[] icon = new IIcon[6];

    public ComputerProbeBlock() {
        super(Material.packedIce);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new ComputerProbeEntity();
    }

    @Override
    protected SimpleNode newNode() {
        return new ComputerProbeNode();
    }

    public IIcon getIcon(int side, int meta) {
        return icon[side];
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icon[4] = register.registerIcon("eau:computerprobe_xn");
        icon[5] = register.registerIcon("eau:computerprobe_xp");
        icon[2] = register.registerIcon("eau:computerprobe_zn");
        icon[3] = register.registerIcon("eau:computerprobe_zp");
        icon[0] = register.registerIcon("eau:computerprobe_yn");
        icon[1] = register.registerIcon("eau:computerprobe_yp");
    }
}
