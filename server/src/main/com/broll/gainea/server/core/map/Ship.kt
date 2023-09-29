package com.broll.gainea.server.core.map

class Ship(coordinates: Coordinates) : Location(coordinates) {

    lateinit var from: Location
    lateinit var to: Location
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
