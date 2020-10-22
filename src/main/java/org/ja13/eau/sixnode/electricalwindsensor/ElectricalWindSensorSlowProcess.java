package org.ja13.eau.sixnode.electricalwindsensor;

import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.sim.IProcess;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.misc.RcInterpolator;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.sim.IProcess;

public class ElectricalWindSensorSlowProcess implements IProcess, INBTTReady {

    ElectricalWindSensorElement element;

    double timeCounter = 0;
    static final double refreshPeriode = 0.2;
    RcInterpolator rc = new RcInterpolator(3f);
    final float premonitionTime = 120;

    public ElectricalWindSensorSlowProcess(ElectricalWindSensorElement element) {
        this.element = element;
    }

    @Override
    public void process(double time) {
        timeCounter += time;
        if (timeCounter > refreshPeriode) {
            timeCounter -= refreshPeriode;
            Coordonate coord = element.sixNode.coordonate;

            element.outputGateProcess.setOutputNormalized(Utils.getWind(coord.dimention, coord.y) / element.descriptor.windMax);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
    }
}
