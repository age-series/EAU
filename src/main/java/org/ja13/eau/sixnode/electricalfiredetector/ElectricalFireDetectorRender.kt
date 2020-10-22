package org.ja13.eau.sixnode.electricalfiredetector

import org.ja13.eau.EAU
import org.ja13.eau.misc.Coordonate
import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LRDU
import org.ja13.eau.node.six.SixNodeDescriptor
import org.ja13.eau.node.six.SixNodeElementInventory
import org.ja13.eau.node.six.SixNodeElementRender
import org.ja13.eau.node.six.SixNodeEntity
import org.ja13.eau.sound.LoopedSound
import net.minecraft.client.audio.ISound
import net.minecraft.entity.player.EntityPlayer
import java.io.DataInputStream
import java.io.IOException

class ElectricalFireDetectorRender(tileEntity: org.ja13.eau.node.six.SixNodeEntity, side: Direction, descriptor: org.ja13.eau.node.six.SixNodeDescriptor)
    : org.ja13.eau.node.six.SixNodeElementRender(tileEntity, side, descriptor) {
    val descriptor = descriptor as org.ja13.eau.sixnode.electricalfiredetector.ElectricalFireDetectorDescriptor

    var powered = false
    var firePresent = false
    var ledOn = false

    val inventory: org.ja13.eau.node.six.SixNodeElementInventory?

    init {
        if (this.descriptor.batteryPowered) {
            inventory = org.ja13.eau.node.six.SixNodeElementInventory(1, 64, this)
            addLoopedSound(object : LoopedSound("eln:FireAlarm",
                Coordonate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tileEntity.worldObj),
                ISound.AttenuationType.LINEAR) {
                override fun getVolume() = if (firePresent) 1f else 0f
            })
        } else {
            inventory = null
        }
    }

    override fun draw() {
        super.draw()

        if (!descriptor.batteryPowered) {
            drawSignalPin(front.right(), descriptor.pinDistance)
        }

        descriptor.draw(ledOn)
    }

    override fun publishUnserialize(stream: DataInputStream) {
        super.publishUnserialize(stream)
        try {
            powered = stream.readBoolean()
            firePresent = stream.readBoolean()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    var time = 0f
    override fun refresh(deltaT: Float) {
        super.refresh(deltaT)
        time += deltaT

        if (powered) {
            if (firePresent) {
                ledOn = firePresent
            } else {
                ledOn = (time * 5).toInt() % 25 == 0
            }
        } else {
            ledOn = false
        }
    }

    override fun getCableRender(lrdu: LRDU) = org.ja13.eau.EAU.smallInsulationLowCurrentRender

    override fun newGuiDraw(side: Direction, player: EntityPlayer) = if (inventory != null)
        ElectricalFireDetectorGui(player, inventory, this) else null
}
