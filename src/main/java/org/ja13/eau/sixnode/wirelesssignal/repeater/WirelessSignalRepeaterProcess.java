package org.ja13.eau.sixnode.wirelesssignal.repeater;

import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalSpot;
import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalTx;
import org.ja13.eau.sixnode.wirelesssignal.WirelessUtils;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalSpot;
import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalTx;
import org.ja13.eau.sixnode.wirelesssignal.WirelessUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class WirelessSignalRepeaterProcess implements IProcess, IWirelessSignalSpot {

    private final WirelessSignalRepeaterElement rx;

    double sleepTimer = 0;
    IWirelessSignalSpot spot;

    boolean boot = true;

    public WirelessSignalRepeaterProcess(WirelessSignalRepeaterElement rx) {
        this.rx = rx;
    }

    @Override
    public void process(double time) {
        sleepTimer -= time;
        if (sleepTimer < 0) {
            sleepTimer += Utils.rand(1.2, 2);

            spot = WirelessUtils.buildSpot(rx.getCoordonate(), null, rx.descriptor.range);

            if (boot) {
                boot = false;
                //IWirelessSignalSpot.spots.add(this);
            }
        }
    }

    @Override
    public HashMap<String, ArrayList<IWirelessSignalTx>> getTx() {
        return spot.getTx();
    }

    @Override
    public ArrayList<IWirelessSignalSpot> getSpot() {
        return spot.getSpot();
    }

    @Override
    public Coordonate getCoordonate() {
        return rx.getCoordonate();
    }

    @Override
    public int getRange() {
        return rx.descriptor.range;
    }
}
