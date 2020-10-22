package org.ja13.eau.transparentnode.teleporter;

import org.ja13.eau.ghost.GhostGroup;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.*;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.ja13.eau.ghost.GhostGroup;
import org.ja13.eau.i18n.I18N;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.node.transparent.TransparentNodeDescriptor;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeleporterDescriptor extends TransparentNodeDescriptor {

    private final Obj3D obj;
    public Obj3D.Obj3DPart main, ext_control, ext_power;
    public Obj3D.Obj3DPart door_out, door_in, door_in_charge;
    public Obj3D.Obj3DPart indoor_open, indoor_closed;
    public Obj3D.Obj3DPart outlampline0_alpha, outlampline0;
    public Obj3D.Obj3DPart[] leds = new Obj3D.Obj3DPart[10];
    public Obj3D.Obj3DPart scr0_electrictity, scr1_cables, scr2_transporter, scr3_userin, scr5_dooropen, src4_doorclosed;
    public Obj3D.Obj3DPart gyro_alpha, gyro, whiteblur;

    public TeleporterDescriptor(
            String name, Obj3D obj,
            GenericCableDescriptor cable,
            Coordonate areaCoordonate, Coordonate lightCoordonate,
            int areaH,
            Coordonate[] powerCoordonate,
            GhostGroup ghostDoorOpen, GhostGroup ghostDoorClose

    ) {
        super(name, TeleporterElement.class, TeleporterRender.class);
        this.cable = cable;
        this.obj = obj;
        this.powerCoordonate = powerCoordonate;
        if (obj != null) {
            main = obj.getPart("main");
            ext_control = obj.getPart("ext_control");
            ext_power = obj.getPart("ext_power");
            door_out = obj.getPart("door_out");
            door_in_charge = obj.getPart("door_in_charge");
            door_in = obj.getPart("door_in");
            indoor_closed = obj.getPart("indoor_closed");
            indoor_open = obj.getPart("indoor_open");
            outlampline0_alpha = obj.getPart("outlampline0_alpha");
            outlampline0 = obj.getPart("outlampline0");
            scr0_electrictity = obj.getPart("scr0_electrictity");
            scr1_cables = obj.getPart("scr1_cables");
            scr2_transporter = obj.getPart("scr2_transporter");
            scr3_userin = obj.getPart("scr3_userin");
            scr5_dooropen = obj.getPart("scr5_dooropen");
            src4_doorclosed = obj.getPart("src4_doorclosed");
            gyro_alpha = obj.getPart("gyro_alpha");
            gyro = obj.getPart("gyro");
            whiteblur = obj.getPart("whiteblur");

            for (int idx = 0; idx < 10; idx++) {
                leds[idx] = obj.getPart("led" + idx);
            }
        }
        this.areaCoordonate = areaCoordonate;
        this.areaH = areaH;
        this.ghostDoorClose = ghostDoorClose;
        this.ghostDoorOpen = ghostDoorOpen;
        this.lightCoordonate = lightCoordonate;

        voltageTier = VoltageTier.INDUSTRIAL;
    }

    @Override
    public void addInfo(@NotNull ItemStack itemStack, @NotNull EntityPlayer entityPlayer, @NotNull List<String> list) {
        super.addInfo(itemStack, entityPlayer, list);
        list.add(I18N.tr("It's experimental!"));
    }

    public GhostGroup ghostDoorOpen, ghostDoorClose;

    int areaH;
    public Coordonate areaCoordonate, lightCoordonate;

    public AxisAlignedBB getBB(Coordonate c, Direction front) {
        Coordonate temp = new Coordonate(areaCoordonate);
        temp.setDimension(c.dimention);
        temp.applyTransformation(front, c);

        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(temp.x, temp.y, temp.z, temp.x + 1, temp.y + areaH, temp.z + 1);
        return bb;
    }

    public Coordonate getTeleportCoordonate(Direction front, Coordonate c) {
        Coordonate temp = new Coordonate(areaCoordonate);
        temp.setDimension(c.dimention);
        temp.applyTransformation(front, c);

        return temp;
    }

    public GenericCableDescriptor cable;

    public void draw() {
        if (main != null) main.draw();
        if (ext_control != null) ext_control.draw();
        if (ext_power != null) ext_power.draw();
        if (door_out != null) door_out.draw();
    }

    Coordonate[] powerCoordonate;

    public Coordonate[] getPowerCoordonate(World w) {
        Coordonate[] temp = new Coordonate[powerCoordonate.length];
        for (int idx = 0; idx < temp.length; idx++) {
            temp[idx] = new Coordonate(powerCoordonate[idx]);
            temp[idx].setDimension(w.provider.dimensionId);
        }
        return temp;
    }

    @Override
    public int getSpawnDeltaX() {
        return 4;
    }

    String chargeSound = null;
    float chargeVolume = 0;

    public TeleporterDescriptor setChargeSound(String sound, float volume) {
        chargeSound = sound;
        chargeVolume = volume;
        return this;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
                                         ItemRendererHelper helper) {
        return type != ItemRenderType.INVENTORY;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        } else {
            objItemScale(obj);
            main.draw();
            ext_control.draw();
            ext_power.draw();
            UtilsClient.disableCulling();
            door_out.draw();
            UtilsClient.enableCulling();
            indoor_open.draw();
        }
    }
}
