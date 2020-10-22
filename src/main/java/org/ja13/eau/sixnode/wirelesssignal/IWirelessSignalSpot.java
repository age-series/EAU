package org.ja13.eau.sixnode.wirelesssignal;

import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Coordonate;

import java.util.ArrayList;
import java.util.HashMap;

public interface IWirelessSignalSpot {

    ArrayList<IWirelessSignalSpot> spots = new ArrayList<IWirelessSignalSpot>();

    HashMap<String, ArrayList<IWirelessSignalTx>> getTx();

    ArrayList<IWirelessSignalSpot> getSpot();

    Coordonate getCoordonate();

    int getRange();
}
