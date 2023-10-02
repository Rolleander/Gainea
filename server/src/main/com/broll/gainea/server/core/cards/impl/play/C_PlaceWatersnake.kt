package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.emptyOrWildMonster

class C_PlaceWatersnake : Card(49, "Verseuchte Gewässer", "Platziert eine wilde Seeschlange auf ein beliebiges unbesetztes Meer") {
    init {
        drawChance = 0.8f
    }

    override val isPlayable: Boolean
        get() = locations.isNotEmpty()
    private val locations: List<Location>
        get() = game.map.allAreas.filter { it.type == AreaType.LAKE && it.emptyOrWildMonster() }

    override fun play() {
        val monster = Monster(game.neutralPlayer)
        monster.name = "Seeschlange"
        monster.icon = 120
        monster.setPower(4)
        monster.setHealth(4)
        placeUnitHandler.placeUnit(owner, monster, locations, "Platziert die Seeschlange")
    }
}
