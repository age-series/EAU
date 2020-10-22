package org.ja13.eau.misc

open class DescriptorBase(descriptorKey: String) {

    @JvmField
    var descriptorKey = descriptorKey

    init {
        DescriptorManager.put(descriptorKey, this)
    }
}
