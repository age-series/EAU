package org.ja13.eau.transparentnode.electricalmachine;

import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.Obj3D.Obj3DPart;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.misc.RecipesList;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.sim.ThermalLoadInitializer;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import net.minecraft.entity.item.EntityItem;
import org.ja13.eau.cable.CableRenderDescriptor;
import org.ja13.eau.misc.Obj3D;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.misc.RecipesList;
import org.ja13.eau.misc.UtilsClient;
import org.ja13.eau.sim.ThermalLoadInitializer;
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor;
import org.lwjgl.opengl.GL11;

public class MagnetizerDescriptor extends ElectricalMachineDescriptor {
    private Obj3D.Obj3DPart main;
    private Obj3D.Obj3DPart rot;

    public MagnetizerDescriptor(String name, Obj3D obj, double nominalU, double nominalP, double maximalU,
                                ThermalLoadInitializer thermal, GenericCableDescriptor cable, RecipesList recipe) {
        super(name, nominalU, nominalP, maximalU, thermal, cable, recipe);
        if (obj != null) {
            rot = obj.getPart("rot");
            main = obj.getPart("main");
        }
    }

    class MaceratorDescriptorHandle {
        float counter = 0;
        float itemCounter = 0;
        final RcInterpolator interpolator = new RcInterpolator(0.5f);
    }

    @Override
    Object newDrawHandle() {
        return new MaceratorDescriptorHandle();
    }

    @Override
    void draw(ElectricalMachineRender render, Object handleO, EntityItem inEntity, EntityItem outEntity, float powerFactor, float processState) {
        MaceratorDescriptorHandle handle = (MaceratorDescriptorHandle) handleO;

        main.draw();
        rot.draw(handle.counter, 0f, 0f, 1f);

        GL11.glScalef(0.5f, 0.5f, 0.5f);
        UtilsClient.drawEntityItem(inEntity, 0.0, 0.25f, 0f, handle.itemCounter, 1f);
    }

    @Override
    void refresh(float deltaT, ElectricalMachineRender render, Object handleO, EntityItem inEntity, EntityItem outEntity, float powerFactor, float processState) {
        MaceratorDescriptorHandle handle = (MaceratorDescriptorHandle) handleO;
        handle.interpolator.setTarget(powerFactor);
        handle.interpolator.step(deltaT);
        handle.counter += deltaT * handle.interpolator.get() * 360;
        while (handle.counter >= 360f) handle.counter -= 360;

        handle.itemCounter += deltaT * 90;
        while (handle.itemCounter >= 360f) handle.itemCounter -= 360;
    }

    @Override
    public boolean drawCable() {
        return true;
    }

    @Override
    CableRenderDescriptor getPowerCableRender() {
        return cable.render;
    }
}
