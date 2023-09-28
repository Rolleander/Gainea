package com.broll.gainea.server.core.map

abstract class AreaCollection {
    lateinit var expansion: Expansion
        private set

    val areas = mutableListOf<Area>()

    val ships = mutableListOf<Ship>()

    var name: String? = null
    fun init(container: Expansion) {
        expansion = container
    }

    fun getArea(id: AreaID) =
            areas.firstOrNull { it.id == id }

}
