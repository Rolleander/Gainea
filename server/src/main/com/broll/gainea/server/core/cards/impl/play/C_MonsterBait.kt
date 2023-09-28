package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.monster.MonsterBehavior
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.UnitControl

class C_MonsterBait : Card(69, "Köderstein", "Wählt ein neutrales Gebiet, alle benachbarten Monster bewegen sich dorthin und werden sesshaft.") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val areas = game.map.allAreas.filter { LocationUtils.emptyOrWildMonster(it) }
        val location = selectHandler.selectLocation(owner, "Wähle einen Zielort", areas)
        location.connectedLocations.flatMap { LocationUtils.getMonsters(it) }
                .forEach { monster ->
                    monster.setBehavior(MonsterBehavior.RESIDENT)
                    UnitControl.move(game, monster, location)
                }
    }
}
