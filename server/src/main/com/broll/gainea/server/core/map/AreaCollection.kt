package com.broll.gainea.server.core.map

java.util.ArrayList
abstract class AreaCollection {
    var expansion: Expansion? = null
        private set
    @JvmField
    var areas: List<Area?> = ArrayList()
    @JvmField
    var ships: MutableList<Ship?> = ArrayList()
    @JvmField
    var name: String? = null
    fun init(container: Expansion?) {
        expansion = container
    }

    fun getArea(id: AreaID): Area? {
        return areas.stream().filter { it: Area? -> it.getId() === id }.findFirst().orElse(null)
    }
}
