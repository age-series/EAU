package org.ja13.eau.node

import net.minecraft.inventory.IInventory

interface INodeElement {
    fun needPublish()
    fun reconnect()
    fun inventoryChange(inventory: IInventory?)
}
