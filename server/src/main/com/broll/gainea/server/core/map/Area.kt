package com.broll.gainea.server.core.map

java.util.HashSet
class Area(val id: AreaID,    val type: AreaType,
           val name: String) : Location() {
    override val connectedLocations: MutableSet<Location> = HashSet()


    fun addAdjacentLocation(location: Location?) {
        connectedLocations.add(location)
        if (location is Area) {
            location.connectedLocations.add(this)
        }
    }

    override fun equals(o: Any?): Boolean {
        return if (o is Area) {
            o.number == number
        } else false
    }

    override fun toString(): String {
        return name!!
    }
}
