package com.broll.gainea.server.core.cards.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.cards.RandomEvent
import com.broll.gainea.server.core.cards.impl.play.C_Treasury
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_King : RandomEvent() {
    override fun run(game: Game) {
        game.freeArea { area ->
            val king = object : Soldier(game.neutralPlayer) {
                override fun battleResult(result: BattleResult) {
                    result.getKillingPlayers(this).forEach {
                        it.cardHandler.receiveCard(C_Treasury())
                    }
                }
            }
            king.description = "Bezwinger erhält Reichtum-Karte"
            king.setStats(1, 1)
            king.icon = 25
            king.name = "Händlerfürst"
            game.spawn(king, area)
            for (i in 1..2) {
                val soldier = Soldier(game.neutralPlayer)
                soldier.icon = 33
                soldier.name = "Gardist"
                soldier.setStats(2, 2)
                game.spawn(soldier, area)
            }
        }
    }
}

