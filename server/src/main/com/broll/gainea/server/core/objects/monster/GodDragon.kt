package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.player.Player
import java.util.function.Consumer

class GodDragon : Monster() {
    init {
        icon = 58
        name = "Götterdrache"
        setStats(8, 8)
        setBehavior(MonsterBehavior.AGGRESSIVE)
        setActivity(MonsterActivity.ALWAYS)
        setMotion(MonsterMotion.AIRBORNE)
    }

    override fun onDeath(throughBattle: BattleResult?) {
        if (throughBattle != null) {
            throughBattle.getKillingPlayers(this)!!.forEach(Consumer { p: Player? -> p.getGoalHandler().addPoints(1) })
        }
    }
}
