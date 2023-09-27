package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.battle.BattleResultimport

com.broll.gainea.server.core.cards.DirectlyPlayedCardimport com.broll.gainea.server.core.objects.Soldierimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.LocationUtilsimport java.util.function.Consumer
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

    private inner class Challenger : Soldier(null) {
        init {
            name = "Der Fremde"
            icon = 126
            setStats(7, 7)
        }

        override fun onDeath(throughBattle: BattleResult?) {
            if (throughBattle != null) {
                throughBattle.getKillingPlayers(this)!!.forEach(Consumer { p: Player? -> p.getGoalHandler().addPoints(1) })
            }
        }
    }
}
