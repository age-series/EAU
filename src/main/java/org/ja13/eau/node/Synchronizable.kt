package org.ja13.eau.node

class Synchronizable<T>(initialValue: T) {
    var pending = false
    var value: T = initialValue
        get() {
            pending = false
            return field
        }
        set(newValue) {
            pending = newValue != field
            field = newValue
        }
}
