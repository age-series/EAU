package org.ja13.eau.transparentnode.electricalantennarx;

import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTierHelpers;
import org.ja13.eau.node.transparent.TransparentNode.FrontType;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTierHelpers;
import org.ja13.eau.node.transparent.TransparentNode;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static org.ja13.eau.i18n.I18N.tr;

public class ElectricalAntennaRxDescriptor extends TransparentNodeDescriptor {

    Obj3D obj;
    Obj3D.Obj3DPart main;

    double electricalNominalVoltage, electricalNominalPower;
    double electricalMaximalVoltage, electricalMaximalPower;
    double electricalNominalInputR;
    GenericCableDescriptor cable;

    public ElectricalAntennaRxDescriptor(String name, Obj3D obj,
                                         double electricalNominalVoltage, double electricalNominalPower,
                                         double electricalMaximalVoltage, double electricalMaximalPower,
                                         GenericCableDescriptor cable) {
        super(name, ElectricalAntennaRxElement.class, ElectricalAntennaRxRender.class);
        this.electricalNominalVoltage = electricalNominalVoltage;
        this.electricalNominalPower = electricalNominalPower;
        this.electricalMaximalVoltage = electricalMaximalVoltage;
        this.electricalMaximalPower = electricalMaximalPower;
        this.cable = cable;

        this.obj = obj;
        if (obj != null) main = obj.getPart("main");

        setDefaultIcon("electricalantennarx");
        voltageTier = VoltageTierHelpers.Companion.fromVoltage(electricalNominalVoltage);
    }

    @Override
    public TransparentNode.FrontType getFrontType() {
        return TransparentNode.FrontType.BlockSideInv;
    }

    @Override
    public boolean mustHaveWallFrontInverse() {
        return true;
    }

    @Override
    public boolean mustHaveFloor() {
        return false;
    }

    public void draw() {
        GL11.glDisable(GL11.GL_CULL_FACE);
        if (main != null) main.draw();
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY)
            super.renderItem(type, item, data);
        else
            draw();
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        list.add(I18N.tr("Wireless energy receiver."));
        list.add(I18N.tr("Nominal usage:"));
        list.add("  " + I18N.tr("Voltage: %1$V", Utils.plotValue(electricalNominalVoltage)));
        list.add("  " + I18N.tr("Power: %1$W", Utils.plotValue(electricalNominalPower)));
    }
}
