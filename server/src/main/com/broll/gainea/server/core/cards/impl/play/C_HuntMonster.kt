package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.MOVEMENT
import com.broll.gainea.server.core.utils.getMonsters
import com.broll.gainea.server.core.utils.getUnits
import com.broll.gainea.server.core.utils.getWildMonsterLocations

class C_HuntMonster : Card(
    37,
    MOVEMENT,
    "Drachenjagd",
    "Wählt eine Truppe und greift damit ein beliebiges Monster auf der gleichen Karte an"
) {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val from = selectHandler.selectLocation(
            "Wählt eure Angriffstruppe",
            owner.controlledLocations.toList()
        )
        val attackers = owner.getUnits(from)
        val attackLocations = game.getWildMonsterLocations()
            .filter { it.container.expansion === from.container.expansion }
        if (attackLocations.isNotEmpty()) {
            val target = selectHandler.selectLocation("Wählt den Angriffsort aus", attackLocations)
            game.battleHandler.startBattle(attackers, target.getMonsters())
        }
    }
}
