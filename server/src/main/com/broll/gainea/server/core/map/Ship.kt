package com.broll.gainea.server.core.map

class Ship : Location() {
    @JvmField
    var from: Location? = null
    @JvmField
    var to: Location? = null
    fun passable(from: Location?): Boolean {
        return if (this.from === from) {
            true
        } else false
    }

    override fun toString(): String {
        return "Schiff"
    }

    override val connectedLocations: MutableSet<Location?>
        get() {
            val set: MutableSet<Location?> = HashSet(2)
            set.add(from)
            set.add(to)
            return set
        }

    override fun equals(o: Any?): Boolean {
        return if (o is Ship) {
            o.number == number
        } else false
    }
}
