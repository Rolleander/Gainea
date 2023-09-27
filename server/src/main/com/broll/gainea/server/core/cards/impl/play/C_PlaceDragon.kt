package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.objects.monster.MonsterActivityimport com.broll.gainea.server.core.objects.monster.MonsterBehaviorimport com.broll.gainea.server.core.utils.LocationUtilsimport java.util.stream.Collectors
class C_PlaceDragon : Card(32, "Drachenhort", "Platziert einen wilden Feuerdrachen auf einen beliebigen unbesetzten Berg") {
    init {
        drawChance = 0.7f
    }

    override val isPlayable: Boolean
        get() = !locations.isEmpty()
    private val locations: List<Location?>
        private get() = game.map.allAreas.stream().filter { it: Area? -> it.getType() == AreaType.MOUNTAIN && LocationUtils.emptyOrWildMonster(it) }.collect(Collectors.toList())

    override fun play() {
        val monster = Monster()
        monster.name = "Feuerdrache"
        monster.icon = 119
        monster.setPower(4)
        monster.setHealth(5)
        monster.setActivity(MonsterActivity.RARELY)
        monster.setBehavior(MonsterBehavior.AGGRESSIVE)
        placeUnitHandler!!.placeUnit(owner, monster, locations, "Platziert den Feuerdrachen")
    }
}
