package org.ja13.eau.item

import org.ja13.eau.generic.GenericItemUsingDamageDescriptor
import net.minecraft.item.Item

open class GenericItemUsingDamageDescriptorUpgrade : GenericItemUsingDamageDescriptor {
    constructor(name: String?) : super(name!!)
    constructor(name: String?, iconName: String?) : super(name!!, iconName!!)
}
