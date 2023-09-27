package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.utils.LocationUtilsimport java.util.stream.Collectors
class C_PlaceWatersnake : Card(49, "Verseuchte Gewässer", "Platziert eine wilde Seeschlange auf ein beliebiges unbesetztes Meer") {
    init {
        drawChance = 0.8f
    }

    override val isPlayable: Boolean
        get() = !locations.isEmpty()
    private val locations: List<Location?>
        private get() = game.map.allAreas.stream().filter { it: Area? -> it.getType() == AreaType.LAKE && LocationUtils.emptyOrWildMonster(it) }.collect(Collectors.toList())

    override fun play() {
        val monster = Monster()
        monster.name = "Seeschlange"
        monster.icon = 120
        monster.setPower(4)
        monster.setHealth(4)
        placeUnitHandler!!.placeUnit(owner, monster, locations, "Platziert die Seeschlange")
    }
}
