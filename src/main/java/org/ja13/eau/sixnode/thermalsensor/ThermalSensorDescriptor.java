package org.ja13.eau.sixnode.thermalsensor;

import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class ThermalSensorDescriptor extends SixNodeDescriptor {

    public boolean temperatureOnly;

    Obj3D obj;
    Obj3D.Obj3DPart main;
    Obj3D.Obj3DPart adapter;

    public ThermalSensorDescriptor(String name,
                                   Obj3D obj,
                                   boolean temperatureOnly) {
        super(name, ThermalSensorElement.class, ThermalSensorRender.class);
        this.temperatureOnly = temperatureOnly;
        this.obj = obj;
        if (obj != null) {
            main = obj.getPart("main");
            adapter = obj.getPart("adapter");
        }
        voltageTier = VoltageTier.TTL;
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);

        if (temperatureOnly) {
            list.add(I18N.tr("Measures temperature of cables."));
            list.add(I18N.tr("Has a signal output."));
        } else {
            list.add(I18N.tr("Measures thermal values on cables."));
            list.add(I18N.tr("Can measure:"));
            list.add(I18N.tr("  Temperature/Power conducted"));
            list.add(I18N.tr("Has a signal output."));
        }
    }

    void draw(boolean renderAdapter) {
        if (main != null) main.draw();
        if (renderAdapter && adapter != null) adapter.draw();
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).inverse();
    }
}
