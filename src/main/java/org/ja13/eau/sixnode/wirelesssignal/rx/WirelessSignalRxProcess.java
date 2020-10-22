package org.ja13.eau.sixnode.wirelesssignal.rx;

import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalSpot;
import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalTx;
import org.ja13.eau.sixnode.wirelesssignal.WirelessUtils;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.misc.INBTTReady;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalSpot;
import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalTx;
import org.ja13.eau.sixnode.wirelesssignal.WirelessUtils;

import java.util.HashMap;
import java.util.HashSet;

public class WirelessSignalRxProcess implements IProcess, INBTTReady {

    private final WirelessSignalRxElement rx;

    double sleepTimer = 0;

    HashMap<String, HashSet<IWirelessSignalTx>> txSet = new HashMap<String, HashSet<IWirelessSignalTx>>();
    HashMap<IWirelessSignalTx, Double> txStrength = new HashMap<IWirelessSignalTx, Double>();

    public WirelessSignalRxProcess(WirelessSignalRxElement rx) {
        this.rx = rx;
    }

    @Override
    public void process(double time) {
        double output;
        sleepTimer -= time;

        if (sleepTimer < 0) {
            sleepTimer += Utils.rand(1.2, 2);

            IWirelessSignalSpot spot = WirelessUtils.buildSpot(rx.getCoordonate(), rx.channel, 0);
            WirelessUtils.getTx(spot, txSet, txStrength);
        }

        HashSet<IWirelessSignalTx> txs = txSet.get(rx.channel);
        if (txs == null) {
            output = 0;
            rx.setConnection(false);
        } else {
            output = rx.getAggregator().aggregate(txs);
            rx.setConnection(true);
        }

        rx.outputGateProcess.setOutputNormalized(output);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
    }
}
