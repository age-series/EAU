package org.ja13.eau.sixnode.wirelesssignal.aggregator;

import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalTx;
import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalTx;

import java.util.Collection;

public class BiggerAggregator implements IWirelessSignalAggregator {

    @Override
    public double aggregate(Collection<IWirelessSignalTx> txs) {
        double bestValue = 0;
        for (IWirelessSignalTx tx : txs) {
            double v = tx.getValue();
            if (v > bestValue) {
                bestValue = v;
            }
        }
        return bestValue;
    }
}
