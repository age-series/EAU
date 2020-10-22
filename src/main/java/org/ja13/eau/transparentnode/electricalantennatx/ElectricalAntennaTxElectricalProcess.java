package org.ja13.eau.transparentnode.electricalantennatx;

import org.ja13.eau.sim.IProcess;
import org.ja13.eau.transparentnode.electricalantennarx.ElectricalAntennaRxElement;
import org.ja13.eau.sim.IProcess;

public class ElectricalAntennaTxElectricalProcess implements IProcess {

    ElectricalAntennaTxElement element;

    public ElectricalAntennaTxElectricalProcess(ElectricalAntennaTxElement element) {
        this.element = element;
    }

    @Override
    public void process(double time) {
        ElectricalAntennaRxElement rx = element.getRxElement();

        if (rx == null) {
            element.signalOutProcess.setOutputNormalized(1.0);
        } else {
            double powerOut = Math.max(0, element.powerResistor.getP() * element.powerEfficency - 2);
            rx.setPowerOut(powerOut);
            element.signalOutProcess.setU(rx.getSignal());
        }
        element.calculatePowerInRp();
    }
}
