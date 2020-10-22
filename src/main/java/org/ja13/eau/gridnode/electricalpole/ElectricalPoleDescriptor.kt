package org.ja13.eau.gridnode.electricalpole

import org.ja13.eau.gridnode.GridDescriptor
import org.ja13.eau.misc.Obj3D

/**
 * Created by svein on 07/08/15.
 */
class ElectricalPoleDescriptor(name: String,
                               obj: Obj3D, cableTexture: String,
                               cableDescriptor: org.ja13.eau.sixnode.genericcable.GenericCableDescriptor,
                               val includeTransformer: Boolean,
                               connectRange: Int, val voltageLimit: Double)
    : GridDescriptor(name, obj, ElectricalPoleElement::class.java, ElectricalPoleRender::class.java, cableTexture, cableDescriptor, connectRange) {
    val minimalLoadToHum = 0.2

    init {
        obj.getPart("foot")?.let {
            static_parts.add(it)
        }
        if (includeTransformer) {
            arrayOf("transformer", "cables").forEach {
                obj.getPart(it)?.let { rotating_parts.add(it) }
            }
        }
    }

    override fun hasCustomIcon() = this.name == "Transmission Tower"
}
