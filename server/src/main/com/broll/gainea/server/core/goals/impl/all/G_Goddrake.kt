package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.objects.monster.GodDragon
import com.broll.gainea.server.core.player.isNeutral


class G_Goddrake : Goal(GoalDifficulty.EASY, "Töte den Götterdrachen im Kampf") {

    override fun battleResult(result: BattleResult) {
        listOf(result.attackers, result.defenders).flatten()
                .filter { it.source is GodDragon && it.owner.isNeutral() }
                .forEach {
                    if (result.getKillingPlayers(it).contains(player)) {
                        success()
                    }
                }
    }

    override fun check() {}
    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setPrepareStrategy {
            val dragonLocation = game.objects.filterIsInstance<GodDragon>().map { it.location }.firstOrNull()
            if (dragonLocation == null) {
                strategy.updateTargets(HashSet())
            } else {
                strategy.updateTargets(setOf(dragonLocation))
                strategy.setRequiredUnits(5)
            }
        }
    }
}
