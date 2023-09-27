package com.broll.gainea.server.core.map.impl

import com.broll.gainea.server.core.map.AreaIDimport

com.broll.gainea.server.core.map.ContinentIDimport com.broll.gainea.server.core.map.ExpansionFactoryimport com.broll.gainea.server.core.map.ExpansionTypeimport com.broll.gainea.server.core.map.IslandID
class MountainsMap : ExpansionFactory(ExpansionType.MOUNTAINS) {
    enum class Continents : ContinentID
    enum class Islands : IslandID
    enum class Areas : AreaID

    init {
        setBaseCoordinates(-1.05f, 1.08f)
    }

    override val texture: String
        get() = "expansion_3.png"

    override fun init() {}
    override fun connectWithExpansion(expansion: ExpansionFactory?) {}
}
