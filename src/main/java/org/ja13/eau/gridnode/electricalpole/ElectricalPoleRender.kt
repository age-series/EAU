package org.ja13.eau.gridnode.electricalpole

import org.ja13.eau.EAU
import org.ja13.eau.cable.CableRenderType
import org.ja13.eau.gridnode.GridRender
import org.ja13.eau.misc.LRDUMask
import org.ja13.eau.misc.SlewLimiter
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.node.transparent.TransparentNodeEntity
import org.ja13.eau.sound.LoopedSound
import net.minecraft.client.audio.ISound

import java.io.DataInputStream
import java.io.IOException

class ElectricalPoleRender(entity: org.ja13.eau.node.transparent.TransparentNodeEntity, descriptor: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : GridRender(entity, descriptor) {

    internal var cableRenderType: org.ja13.eau.cable.CableRenderType? = null
    internal var eConn = LRDUMask()

    private val descriptor: ElectricalPoleDescriptor
    private val load = SlewLimiter(0.5)

    init {
        this.descriptor = descriptor as ElectricalPoleDescriptor

        if (this.descriptor.includeTransformer) {
            addLoopedSound(object : LoopedSound("eln:Transformer", coordonate(), ISound.AttenuationType.LINEAR) {
                override fun getVolume(): Float {
                    if (load.position > this@ElectricalPoleRender.descriptor.minimalLoadToHum)
                        return (0.05 * (load.position - this@ElectricalPoleRender.descriptor.minimalLoadToHum) / (1 - this@ElectricalPoleRender.descriptor.minimalLoadToHum)).toFloat()
                    else
                        return 0f
                }
            })
        }
    }

    override fun draw() {
        super.draw()
        cableRenderType = drawCable(front.down(), org.ja13.eau.EAU.mediumInsulationMediumCurrentRender, eConn, cableRenderType)
    }

    override fun networkUnserialize(stream: DataInputStream) {
        super.networkUnserialize(stream)
        eConn.deserialize(stream)
        cableRenderType = null
        try {
            load.target = stream.readDouble()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun refresh(deltaT: Double) {
        super.refresh(deltaT)
        load.step(deltaT)
    }
}
