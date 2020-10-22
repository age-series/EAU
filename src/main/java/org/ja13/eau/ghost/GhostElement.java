package org.ja13.eau.ghost;

import org.ja13.eau.EAU;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.INBTTReady;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.ja13.eau.EAU;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.misc.INBTTReady;

public class GhostElement implements INBTTReady {

    Coordonate elementCoordonate;
    Coordonate observatorCoordonate;
    int UUID;

    public Coordonate getObservatorCoordonate() {
        return observatorCoordonate;
    }

    public GhostElement() {
    }

    public GhostElement(Coordonate elementCoordonate, Coordonate observatorCoordonate, int UUID) {
        this.elementCoordonate = elementCoordonate;
        this.observatorCoordonate = observatorCoordonate;
        this.UUID = UUID;
    }

    public int getUUID() {
        return UUID;
    }

    public void breakBlock() {
        EAU.ghostManager.removeGhost(elementCoordonate);
        GhostObserver observer = EAU.ghostManager.getObserver(observatorCoordonate);
        if (observer != null) observer.ghostDestroyed(UUID);
    }

    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        GhostObserver observer = EAU.ghostManager.getObserver(observatorCoordonate);
        if (observer != null) return observer.ghostBlockActivated(UUID, entityPlayer, side, vx, vy, vz);
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        elementCoordonate = new Coordonate(nbt, str + "elemCoord");
        observatorCoordonate = new Coordonate(nbt, str + "obserCoord");
        UUID = nbt.getInteger(str + "UUID");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {

        elementCoordonate.writeToNBT(nbt, str + "elemCoord");
        observatorCoordonate.writeToNBT(nbt, str + "obserCoord");
        nbt.setInteger(str + "UUID", UUID);
    }
}
