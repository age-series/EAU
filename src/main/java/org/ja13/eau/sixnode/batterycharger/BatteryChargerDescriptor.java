package org.ja13.eau.sixnode.batterycharger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.LRDU;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.misc.VoltageTierHelpers;
import org.ja13.eau.node.six.SixNodeDescriptor;
import org.ja13.eau.sim.mna.component.Resistor;
import org.ja13.eau.sim.nbt.NbtElectricalLoad;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

public class BatteryChargerDescriptor extends SixNodeDescriptor {

    private final Obj3D obj;
    Obj3D.Obj3DPart main;

    public double nominalVoltage;
    public double nominalPower;
    public GenericCableDescriptor cable;
    double Rp;
    public float[] pinDistance;

    Obj3D.Obj3DPart[] leds = new Obj3D.Obj3DPart[4];

    public BatteryChargerDescriptor(String name,
                                    Obj3D obj,
                                    GenericCableDescriptor cable,
                                    double nominalVoltage, double nominalPower) {
        super(name, BatteryChargerElement.class, BatteryChargerRender.class);

        this.nominalVoltage = nominalVoltage;
        this.nominalPower = nominalPower;
        this.Rp = nominalVoltage * nominalVoltage / nominalPower;
        this.obj = obj;
        this.cable = cable;

        if (obj != null) {
            main = obj.getPart("main");
            for (int idx = 0; idx < 4; idx++) {
                leds[idx] = obj.getPart("led" + idx);
            }
            pinDistance = Utils.getSixNodePinDistance(main);
        }

        setDefaultIcon("batterycharger");
        voltageTier = VoltageTierHelpers.Companion.fromVoltage(nominalVoltage);
    }

    public void draw(boolean[] presence, boolean[] charged) {
        if (main != null)
            main.draw();

        int idx = 0;
        for (Obj3D.Obj3DPart led : leds) {
            if (presence != null && presence[idx]) {
                UtilsClient.ledOnOffColor(charged[idx]);
                UtilsClient.drawLight(led);
            } else {
                GL11.glColor3f(0.2f, 0.2f, 0.2f);
                led.draw();
            }
            idx++;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    //boolean[] defaultCharged = new boolean[]{true, true, true, true};

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        }
        draw(null, null);
    }

    public void applyTo(NbtElectricalLoad powerLoad) {
        cable.applyTo(powerLoad);
    }

    public void setRp(Resistor powerload, boolean powerOn) {
        if (!powerOn)
            powerload.highImpedance();
        else
            powerload.setR(Rp);
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List list) {
        super.addInfo(itemStack, entityPlayer, list);
        Collections.addAll(list, I18N.tr("Can be used to recharge\nelectrical items like:\nFlash Light, X-Ray scanner\nand Portable Battery ...").split("\n"));
        list.add(I18N.tr("Nominal power: %1$W", Utils.plotValue(nominalPower)));
    }

    @Override
    public LRDU getFrontFromPlace(Direction side, EntityPlayer player) {
        return super.getFrontFromPlace(side, player).inverse();
    }
}
