package com.broll.gainea.server.core.map

abstract class AreaCollection(val name: String) {
    lateinit var expansion: Expansion
        private set

    val areas = mutableListOf<Area>()

    val ships = mutableListOf<Ship>()

     
    fun init(container: Expansion) {
        expansion = container
    }

    fun getArea(id: AreaID) =
            areas.firstOrNull { it.id == id }

}
