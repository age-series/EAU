package org.ja13.eau.ghost;

import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;
import net.minecraft.entity.player.EntityPlayer;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;

public interface GhostObserver {

    Coordonate getGhostObserverCoordonate();

    void ghostDestroyed(int UUID);

    boolean ghostBlockActivated(int UUID, EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz);
}
