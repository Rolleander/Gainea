package com.broll.gainea.server.core.map.impl

import com.broll.gainea.server.core.map.ExpansionFactory
import com.broll.gainea.core.map.ExpansionType

class MountainsMap : ExpansionFactory(ExpansionType.MOUNTAINS) {
    enum class Continents
    enum class Islands
    enum class Areas

    init {
        baseCoordinates.set(-1.05f, 1.08f)
    }

    override fun init() {}
    override fun connectWithExpansion(expansion: ExpansionFactory) {}
}
