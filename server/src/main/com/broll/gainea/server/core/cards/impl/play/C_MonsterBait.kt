package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.DISRUPTION
import com.broll.gainea.server.core.objects.monster.MonsterBehavior
import com.broll.gainea.server.core.utils.UnitControl.move
import com.broll.gainea.server.core.utils.emptyOrWildMonster
import com.broll.gainea.server.core.utils.getMonsters

class C_MonsterBait : Card(
    69,
    DISRUPTION,
    "Köderstein",
    "Wählt ein neutrales Gebiet, alle benachbarten Monster bewegen sich dorthin und werden sesshaft."
) {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val areas = game.map.allAreas.filter { it.emptyOrWildMonster() }
        val location = selectHandler.selectLocation(owner, "Wähle einen Zielort", areas)
        location.connectedLocations.flatMap { it.getMonsters() }
            .forEach { monster ->
                monster.behavior = MonsterBehavior.RESIDENT
                game.move(monster, location)
            }
    }
}
