package com.broll.gainea.server.init

import com.broll.gainea.server.core.map.ExpansionType

enum class ExpansionSetting(private override val name: String, val maps: Array<ExpansionType>) {
    BASIC_GAME("Gainea", arrayOf(ExpansionType.GAINEA)),
    BOG("Eisländer", arrayOf(ExpansionType.ICELANDS)),
    ICE("Sumpf", arrayOf(ExpansionType.BOGLANDS)),
    PLUS_ICELANDS("Gainea & Eisländer", arrayOf(ExpansionType.GAINEA, ExpansionType.ICELANDS)),
    PLUS_ICELANDS_AND_BOG("Gainea & Eisländer & Sumpf", arrayOf(ExpansionType.GAINEA, ExpansionType.ICELANDS, ExpansionType.BOGLANDS)),
    PLUS_BOG("Gainea & Sumpf", arrayOf(ExpansionType.GAINEA, ExpansionType.BOGLANDS)),
    PLUS_ICELANDS_AND_MOUNTAINS("Gainea & Eisländer & Gipfel", arrayOf(ExpansionType.GAINEA, ExpansionType.ICELANDS, ExpansionType.MOUNTAINS)),
    PLUS_BOG_AND_MOUNTAINS("Gainea & Sumpf & Gipfel", arrayOf(ExpansionType.GAINEA, ExpansionType.BOGLANDS, ExpansionType.MOUNTAINS)),
    BOG_AND_ICE("Eisländer & Sumpf", arrayOf(ExpansionType.ICELANDS, ExpansionType.BOGLANDS)),
    FULL("Alle", ExpansionType.entries.toTypedArray());

    fun getName(): String {
        return name + " (" + maps.size + " - " + (maps.size * 2 + 1) + " Spieler)"
    }
}
