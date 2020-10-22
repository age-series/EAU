package org.ja13.eau.sixnode.wirelesssignal;

import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Coordonate;

public interface IWirelessSignalTx {

    Coordonate getCoordonate();

    int getRange();

    String getChannel();

    double getValue();
}
