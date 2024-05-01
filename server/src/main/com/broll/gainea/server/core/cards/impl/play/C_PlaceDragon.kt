package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.SUMMON
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.monster.MonsterActivity
import com.broll.gainea.server.core.objects.monster.MonsterBehavior
import com.broll.gainea.server.core.utils.emptyOrWildMonster

class C_PlaceDragon : Card(
    32, SUMMON,
    "Drachenhort",
    "Platziert einen wilden Feuerdrachen auf einen beliebigen unbesetzten Berg"
) {
    init {
        drawChance = 0.7f
    }

    override val isPlayable: Boolean
        get() = locations.isNotEmpty()
    private val locations: List<Location>
        get() = game.map.allAreas.filter { it.type == AreaType.MOUNTAIN && it.emptyOrWildMonster() }

    override fun play() {
        val monster = Monster(game.neutralPlayer)
        monster.name = "Feuerdrache"
        monster.icon = 119
        monster.setPower(4)
        monster.setHealth(5)
        monster.activity = MonsterActivity.RARELY
        monster.behavior = MonsterBehavior.AGGRESSIVE
        placeUnitHandler.placeUnit(owner, monster, locations)
    }
}
