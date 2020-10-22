package org.ja13.eau.sound

import org.ja13.eau.misc.Coordonate
import net.minecraft.client.audio.ISound
import net.minecraft.client.audio.ITickableSound
import net.minecraft.util.ResourceLocation

abstract class LoopedSound(val sample: String, val coord: Coordonate,
                           val attentuationType: ISound.AttenuationType = ISound.AttenuationType.LINEAR) : ITickableSound {
    var active = true

    final override fun getPositionedSoundLocation() = ResourceLocation(sample)
    final override fun getXPosF() = coord.x.toFloat() + 0.5f
    final override fun getYPosF() = coord.y.toFloat() + 0.5f
    final override fun getZPosF() = coord.z.toFloat() + 0.5f
    final override fun canRepeat() = true
    final override fun getAttenuationType() = attentuationType

    override fun getPitch() = 1f
    override fun getVolume() = 1f

    override fun isDonePlaying() = !active

    override fun getRepeatDelay() = 0
    override fun update() {}
}
