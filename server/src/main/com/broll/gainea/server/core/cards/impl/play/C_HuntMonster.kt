package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.PlayerUtils

class C_HuntMonster : Card(37, "Drachenjagd", "Wählt eine Truppe und greift damit ein beliebiges Monster auf der gleichen Karte an") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val from = selectHandler.selectLocation("Wählt eure Angriffstruppe", owner.controlledLocations)
        val attackers = PlayerUtils.getUnits(owner, from)
        val attackLocations = LocationUtils.getWildMonsterLocations(game)
                .filter { it.container.expansion === from.container.expansion }
        if (attackLocations.isNotEmpty()) {
            val target = selectHandler.selectLocation("Wählt den Angriffsort aus", attackLocations)
            game.battleHandler.startBattle(attackers, LocationUtils.getMonsters(target))
        }
    }
}
