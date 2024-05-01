package com.broll.gainea.server.core.objects.impl

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.player.Player

class Challenger(owner: Player) : Soldier(owner) {
    init {
        name = "Der Fremde"
        icon = 126
        setStats(7, 7)
        description = "Bezwinger erhält einen Siegespunkt"
    }

    override fun onDeath(throughBattle: BattleResult?) {
        throughBattle?.getKillingPlayers(this)?.forEach { it.goalHandler.addPoints(1) }
    }
}