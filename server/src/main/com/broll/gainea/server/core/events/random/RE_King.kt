package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.cards.impl.play.C_Treasury
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeArea
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_King : RandomEvent() {

    override fun pickSpot() = game.freeArea()
    override fun run() {
        val king = object : Soldier(game.neutralPlayer) {
            override fun onDeath(throughBattle: BattleResult?) {
                throughBattle?.getKillingPlayers(this)?.forEach {
                    it.cardHandler.receiveCard(C_Treasury())
                }
            }
        }
        king.description = "Bezwinger erhält eine Reichtum-Karte"
        king.setStats(1, 2)
        king.icon = 25
        king.name = "Händlerfürst"
        game.spawn(king, location)
        for (i in 1..2) {
            val soldier = Soldier(game.neutralPlayer)
            soldier.icon = 33
            soldier.name = "Gardist"
            soldier.setStats(2, 2)
            game.spawn(soldier, location)
        }
    }
}

