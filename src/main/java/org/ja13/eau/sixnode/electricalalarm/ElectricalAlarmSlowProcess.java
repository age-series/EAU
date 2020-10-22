package org.ja13.eau.sixnode.electricalalarm;

import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sound.SoundCommand;
import org.ja13.eau.misc.Coordonate;
import org.ja13.eau.misc.Utils;
import org.ja13.eau.misc.VoltageTier;
import org.ja13.eau.sim.IProcess;
import org.ja13.eau.sound.SoundCommand;

public class ElectricalAlarmSlowProcess implements IProcess {

    ElectricalAlarmElement element;

    double timeCounter = 0, soundTimeTimeout = Math.random() * 2;
    static final double refreshPeriode = 0.25;
    int soundUuid = Utils.getUuid();
    boolean oldWarm = false;
    boolean oldMute = true;

    public ElectricalAlarmSlowProcess(ElectricalAlarmElement element) {
        this.element = element;
    }

    @Override
    public void process(double time) {
        timeCounter += time;
        if (timeCounter > refreshPeriode) {
            timeCounter -= refreshPeriode;

            boolean warm = element.inputGate.getU() > VoltageTier.TTL.getVoltage() / 2;
            element.setWarm(warm);
            if (warm & !element.mute) {
                if (soundTimeTimeout == 0) {
                    float speed = 1f;
                    Coordonate coord = element.sixNode.coordonate;
                    element.play(new SoundCommand(element.descriptor.soundName).mulVolume(1F, 1.0F).longRange().addUuid(soundUuid));
                    soundTimeTimeout = element.descriptor.soundTime;
                }
            }
            if ((oldWarm && !warm) || (!oldMute && element.mute)) {
                stopSound();
            }

            oldWarm = warm;
            oldMute = element.mute;
        }
        soundTimeTimeout -= time;
        if (soundTimeTimeout < 0) soundTimeTimeout = 0;
    }

    void stopSound() {
        element.stop(soundUuid);
        soundTimeTimeout = 0;
    }
}
