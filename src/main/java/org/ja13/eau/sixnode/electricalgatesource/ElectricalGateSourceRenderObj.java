package org.ja13.eau.sixnode.electricalgatesource;

import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.sixnode.electricalgatesource.ElectricalGateSourceDescriptor.ObjType;
import net.minecraft.tileentity.TileEntity;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.UtilsClient;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ElectricalGateSourceRenderObj {

    private Obj3D.Obj3DPart rot;
    private Obj3D.Obj3DPart main;
    private final Obj3D obj;
    private Obj3D.Obj3DPart lever;
    private Obj3D.Obj3DPart led;
    private Obj3D.Obj3DPart halo;
    ElectricalGateSourceDescriptor.ObjType objType;
    float leverTx;

    private float rotAlphaOn, rotAlphaOff;
    public float speed;

    public ElectricalGateSourceRenderObj(Obj3D obj) {
        this.obj = obj;
        if (obj != null) {
            main = obj.getPart("main");

            if (obj.getString("type").equals("pot")) {
                objType = ElectricalGateSourceDescriptor.ObjType.Pot;
                rot = obj.getPart("rot");
                if (rot != null) {
                    rotAlphaOff = rot.getFloat("alphaOff");
                    rotAlphaOn = rot.getFloat("alphaOn");
                    speed = rot.getFloat("speed");
                }
            }

            if (obj.getString("type").equals("button")) {
                lever = obj.getPart("button");
                led = obj.getPart("led");
                halo = obj.getPart("halo");

                objType = ElectricalGateSourceDescriptor.ObjType.Button;
                if (lever != null) {
                    speed = lever.getFloat("speed");
                    leverTx = lever.getFloat("tx");
                }
            }
        }
    }

    public void draw(float factor, float distance, TileEntity e) {
        switch (objType) {
            case Button:
                if (main != null) main.draw();

                GL11.glTranslatef(leverTx * factor, 0f, 0f);
                if (lever != null) lever.draw();

                UtilsClient.ledOnOffColor(factor > 0.5f);
                UtilsClient.disableLight();
                if (led != null) led.draw();
                UtilsClient.enableBlend();

                if (halo != null) {
                    if (e == null)
                        UtilsClient.drawLight(halo);
                    else {
                        Color c = UtilsClient.ledOnOffColorC(factor > 0.5f);
                        UtilsClient.drawHaloNoLightSetup(halo, c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, e, false);
                    }
                }

                UtilsClient.disableBlend();
                UtilsClient.enableLight();

                break;
            case Pot:
                if (main != null) main.draw();
                if (rot != null) rot.draw(factor * (rotAlphaOn - rotAlphaOff) + rotAlphaOff, 1f, 0f, 0f);
                break;
        }
    }
}
