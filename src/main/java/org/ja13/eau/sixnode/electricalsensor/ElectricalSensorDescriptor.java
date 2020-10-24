package org.ja13.eau.sixnode.electricalsensor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.EAU;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ElectricalSensorDescriptor extends SixNodeDescriptor {

    boolean voltageOnly;
    Obj3D.Obj3DPart main;

    public ElectricalSensorDescriptor(
        String name, String modelName,
        boolean voltageOnly) {
        super(name, ElectricalSensorElement.class, ElectricalSensorRender.class);
        this.voltageOnly = voltageOnly;
        main = EAU.obj.getPart(modelName, "main");

        voltageTier = VoltageTier.NEUTRAL;
    }

    void draw() {
        if (main != null) main.draw();
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        if (voltageOnly) {
            list.add(I18N.tr("Measures voltage on cables."));
            list.add(I18N.tr("Has a signal output."));
        } else {
            list.add(I18N.tr("Measures electrical values on cables."));
            list.add(I18N.tr("Can measure Voltage/Power/Current"));
            list.add(I18N.tr("Has a signal output."));
        }
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).inverse();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }
}
