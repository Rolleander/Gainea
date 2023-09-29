package com.broll.gainea.server.core.map.impl

import com.broll.gainea.server.core.map.AreaID
import com.broll.gainea.server.core.map.ContinentID
import com.broll.gainea.server.core.map.ExpansionFactory
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.IslandID

class MountainsMap : ExpansionFactory(ExpansionType.MOUNTAINS, "expansion_3.png") {
    enum class Continents : ContinentID
    enum class Islands : IslandID
    enum class Areas : AreaID

    init {
        baseCoordinates.set(-1.05f, 1.08f)
    }

    override fun init() {}
    override fun connectWithExpansion(expansion: ExpansionFactory) {}
}
