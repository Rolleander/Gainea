package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.UnitControl.spawn

class C_UnknownSoldier : DirectlyPlayedCard(67, "Mytseriöser Herausforderer", "Ein fremder Herausforderer taucht auf! Wer ihn besiegt erhält einen Siegespunkt.") {
    init {
        drawChance = 0.2f
    }

    override fun play() {
        val location = LocationUtils.getRandomFree(game.map.allAreas)
        if (location != null) {
            val soldier = Challenger()
            spawn(game, soldier, location)
        }
    }

    private inner class Challenger : Soldier(game.neutralPlayer) {
        init {
            name = "Der Fremde"
            icon = 126
            setStats(7, 7)
        }

        override fun onDeath(throughBattle: BattleResult?) {
            throughBattle?.getKillingPlayers(this)?.forEach { it.goalHandler.addPoints(1) }
        }
    }
}
