package org.ja13.eau.sixnode.diode;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.IFunction;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.sim.ElectricalLoad;
import org.ja13.eau.sim.ThermalLoad;
import org.ja13.eau.sim.ThermalLoadInitializer;
import org.ja13.eau.sim.mna.component.ResistorSwitch;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

public class DiodeDescriptor extends SixNodeDescriptor {

    private final Obj3D.Obj3DPart base;
    private final Obj3D.Obj3DPart diodeCables;
    private final Obj3D.Obj3DPart diodeCore;

    double stdI, stdU;
    GenericCableDescriptor cable;
    String descriptor;
    IFunction IfU;

    ThermalLoadInitializer thermal;

    public DiodeDescriptor(String name,
                           IFunction IfU,
                           double Imax,
                           double stdU, double stdI,
                           ThermalLoadInitializer thermal,
                           GenericCableDescriptor cable, Obj3D obj) {
        super(name, DiodeElement.class, DiodeRender.class);

        this.IfU = IfU;

        //double Umax = 0;
        //while(IfU.getValue(Umax) < Imax) Umax += 0.01;
        //double Pmax = Umax * IfU.getValue(Umax);
        this.cable = cable;
        this.thermal = thermal;
        thermal.setMaximalPower(stdU * stdI * 1.2);
        this.stdI = stdI;
        this.stdU = stdU;

        base = obj.getPart("Base");
        diodeCables = obj.getPart("DiodeCables");
        diodeCore = obj.getPart("DiodeCore");
        voltageTier = VoltageTier.NEUTRAL;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelperEln(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        } else {
            GL11.glTranslatef(0.0f, 0.0f, -0.2f);
            GL11.glScalef(1.25f, 1.25f, 1.25f);
            GL11.glRotatef(-90.f, 0.f, 1.f, 0.f);
            draw();
        }
    }

    /*	public void applyTo(DiodeProcess diode) {
            diode.IfU = IfU;
        }
        */
    public void applyTo(ThermalLoad load) {
        thermal.applyTo(load);
    }

    public void applyTo(ElectricalLoad load) {
        cable.applyTo(load);
    }

    public void applyTo(ResistorSwitch resistorSwitch) {
        resistorSwitch.setR(stdU / stdI);
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        Collections.addAll(list, I18N.tr("Electrical current can only\nflow through the diode\nfrom anode to cathode").split("\n"));
    }

    void draw() {
        if (base != null) base.draw();
        if (diodeCables != null) diodeCables.draw();
        if (diodeCore != null) diodeCore.draw();
    }
}
