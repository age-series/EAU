package org.ja13.eau.sixnode.wirelesssignal.aggregator;

import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalTx;
import org.ja13.eau.sixnode.wirelesssignal.IWirelessSignalTx;

import java.util.Collection;

public interface IWirelessSignalAggregator {
    double aggregate(Collection<IWirelessSignalTx> txs);
}
