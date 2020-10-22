package org.ja13.eau.sim.process.destruct;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.node.six.SixNodeElement;
import org.ja13.eau.node.transparent.TransparentNodeElement;
import org.ja13.eau.simplenode.energyconverter.EnergyConverterElnToOtherNode;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import org.ja13.eau.EAU;

public class WorldExplosion implements IDestructable {

    Object origine;

    Coordonate c;
    float strength;
    String type;

    public WorldExplosion(Coordonate c) {
        this.c = c;
    }

    public WorldExplosion(SixNodeElement e) {
        this.c = e.getCoordonate();
        this.type = e.toString();
        origine = e;
    }

    public WorldExplosion(TransparentNodeElement e) {
        this.c = e.coordonate();
        this.type = e.toString();
        origine = e;
    }

    public WorldExplosion(EnergyConverterElnToOtherNode e) {
        this.c = e.coordonate;
        this.type = e.toString();
        origine = e;
    }

    public WorldExplosion cableExplosion() {
        strength = 1.5f;
        return this;
    }

    public WorldExplosion machineExplosion() {
        strength = 3;
        return this;
    }

    @Override
    public void destructImpl() {
        //NodeManager.instance.removeNode(NodeManager.instance.getNodeFromCoordonate(c));

        if (EAU.explosionEnable)
            c.world().createExplosion(null, c.x, c.y, c.z, strength, true);
        else
            c.world().setBlock(c.x, c.y, c.z, Blocks.air);
    }

    @Override
    public String describe() {
        return String.format("%s (%s)", this.type, this.c.toString());
    }
}
