package org.ja13.eau.sound;

public interface IPlayer {

    void play(SoundCommand cmd);

    void stop(int uuid);
}
