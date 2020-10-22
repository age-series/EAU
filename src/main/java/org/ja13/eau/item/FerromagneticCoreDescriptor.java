package org.ja13.eau.item;

import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.sim.ElectricalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.sim.ElectricalLoad;

import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class FerromagneticCoreDescriptor extends GenericItemUsingDamageDescriptorUpgrade {

    public double cableMultiplicator;
    public Obj3DPart feroPart;
    Obj3D obj;

    public FerromagneticCoreDescriptor(String name, Obj3D obj, double cableMultiplicator) {
        super(name);
        this.obj = obj;
        if (obj != null) {
            feroPart = obj.getPart("fero");
        }
        this.cableMultiplicator = cableMultiplicator;
    }

    public void applyTo(ElectricalLoad load) {
        load.setRs(load.getRs() * cableMultiplicator);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        list.add(I18N.tr("Cable loss factor: %1$", cableMultiplicator));
    }
}
