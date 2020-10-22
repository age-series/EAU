package org.ja13.eau.gridnode.transformer

import org.ja13.eau.gridnode.GridDescriptor
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.sixnode.genericcable.GenericCableDescriptor

class GridTransformerDescriptor(name: String, obj: org.ja13.eau.misc.Obj3D, cableTexture: String, cableDescriptor: org.ja13.eau.sixnode.genericcable.GenericCableDescriptor) : GridDescriptor(name, obj, GridTransformerElement::class.java, GridTransformerRender::class.java, cableTexture, cableDescriptor, 12) {
    val minimalLoadToHum = 0.1f

    override fun rotationIsFixed(): Boolean {
        return true
    }

    override fun hasCustomIcon() = true
}
