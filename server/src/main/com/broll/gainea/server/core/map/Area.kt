package com.broll.gainea.server.core.map

class Area(val id: AreaID, val type: AreaType,
           val name: String, coordinates: Coordinates) : Location(coordinates) {

    fun addAdjacentLocation(location: Location) {
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
        return name
    }
}
