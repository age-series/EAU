package org.ja13.eau.transparentnode.teleporter;

import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Coordonate;

public interface ITeleporter {
    Coordonate getTeleportCoordonate();

    String getName();

    boolean reservate();

    void reservateRefresh(boolean doorState, float processRatio);
}
