package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.PlayerUtils

class C_BlitzAttack : Card(11, "Blitzkrieg", "Wählt eine Truppe und greift damit ein beliebiges feindliches Land auf der gleichen Karte an") {
    init {
        drawChance = 0.3f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val from = selectHandler.selectLocation("Wählt eure Angriffstruppe", owner.controlledLocations)
        val attackers = PlayerUtils.getUnits(owner, from)
        val attackLocations = PlayerUtils.getHostileLocations(game, owner)
                .filter { it.container.expansion === from.container.expansion }
        if (attackLocations.isNotEmpty()) {
            val target = selectHandler.selectLocation("Wählt den Angriffsort aus", attackLocations)
            game.battleHandler.startBattle(attackers, PlayerUtils.getHostileArmy(owner, target))
        }
    }
}
