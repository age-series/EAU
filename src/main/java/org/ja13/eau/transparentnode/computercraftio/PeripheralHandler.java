package org.ja13.eau.transparentnode.computercraftio;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.NodeManager;
import org.ja13.eau.simplenode.computerprobe.ComputerProbeNode;
import net.minecraft.world.World;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.node.NodeBase;
import org.ja13.eau.node.NodeManager;
import org.ja13.eau.simplenode.computerprobe.ComputerProbeNode;

public class PeripheralHandler implements IPeripheralProvider {

    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        NodeBase nb = NodeManager.instance.getNodeFromCoordonate(new Coordonate(x, y, z, world));
    /*	if (nb instanceof TransparentNode) {
			TransparentNode tn = (TransparentNode) nb;
			if (tn.element != null && tn.element instanceof ComputerCraftIoElement) {
				return (IPeripheral) tn.element;
			}
		}*/

        if (nb instanceof ComputerProbeNode) {
            IPeripheral p = (IPeripheral) nb;
            return p;
        }

        return null;
    }

    public static void register() {
        ComputerCraftAPI.registerPeripheralProvider(new PeripheralHandler());
    }
}
