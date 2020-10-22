package org.ja13.eau.gridnode.transformer

import org.ja13.eau.gridnode.GridRender
import org.ja13.eau.misc.SlewLimiter
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeEntity
import org.ja13.eau.sound.LoopedSound
import net.minecraft.client.audio.ISound
import java.io.DataInputStream

class GridTransformerRender(entity: org.ja13.eau.node.transparent.TransparentNodeEntity, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : GridRender(entity, descriptor) {
    val desc = descriptor as GridTransformerDescriptor
    private var load = SlewLimiter(0.5)

    init {
        addLoopedSound(object : LoopedSound("eln:Transformer", coordonate(), ISound.AttenuationType.LINEAR) {
            override fun getVolume() = Math.max(0.0, (load.position - desc.minimalLoadToHum) / (1 - desc.minimalLoadToHum)).toFloat()
        })
    }

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        load.target = stream.readDouble()
    }

    override fun refresh(deltaT: Double) {
        super.refresh(deltaT)
        load.step(deltaT)
    }
}
