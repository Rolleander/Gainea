package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.monster.MonsterActivity
import com.broll.gainea.server.core.objects.monster.MonsterBehavior
import com.broll.gainea.server.core.utils.LocationUtils

class C_PlaceDragon : Card(32, "Drachenhort", "Platziert einen wilden Feuerdrachen auf einen beliebigen unbesetzten Berg") {
    init {
        drawChance = 0.7f
    }

    override val isPlayable: Boolean
        get() = locations.isNotEmpty()
    private val locations: List<Location>
        get() = game.map.allAreas.filter { it.type == AreaType.MOUNTAIN && LocationUtils.emptyOrWildMonster(it) }

    override fun play() {
        val monster = Monster()
        monster.name = "Feuerdrache"
        monster.icon = 119
        monster.setPower(4)
        monster.setHealth(5)
        monster.setActivity(MonsterActivity.RARELY)
        monster.setBehavior(MonsterBehavior.AGGRESSIVE)
        placeUnitHandler.placeUnit(owner, monster, locations, "Platziert den Feuerdrachen")
    }
}
