package com.broll.gainea.server.core.map

class Ship(coordinates: Coordinates) : Location(coordinates) {

    var from: Location = SpawnLocation
        set(value) {
            connectedLocations.remove(field)
            connectedLocations.add(value)
            field = value
        }
    var to: Location = SpawnLocation
        set(value) {
            connectedLocations.remove(field)
            connectedLocations.add(value)
            field = value
        }

    fun passable(from: Location) = this.from === from


    override fun toString(): String {
        return "Schiff"
    }

    override fun equals(o: Any?): Boolean {
        return if (o is Ship) {
            o.number == number
        } else false
    }
}
