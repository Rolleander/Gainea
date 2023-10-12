package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.player.Player

class GodDragon(owner: Player) : Monster(owner) {
    init {
        icon = 58
        name = "Götterdrache"
        setStats(8, 8)
        behavior = MonsterBehavior.AGGRESSIVE
        activity = MonsterActivity.ALWAYS
        motion = MonsterMotion.AIRBORNE
        description = "Bezwinger erhält einen Siegespunkt"
    }

    override fun onDeath(throughBattle: BattleResult?) {
        throughBattle?.let {
            it.getKillingPlayers(this).forEach { player -> player.goalHandler.addPoints(1) }
        }
    }
}
