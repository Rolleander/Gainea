package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.monster.Monster

class G_KillStrongMonster : Goal(
    GoalDifficulty.MEDIUM,
    "Besiege ein Monster mit 4 oder mehr Sternen mit nur einer Einheit"
) {
    override fun battleResult(result: BattleResult) {
        if (result.getUnits(player).size == 1 &&
            result.getOpposingUnits(player).any { isTarget(it.source) && it.dead }
        ) {
            success()
        }
    }

    private fun isTarget(unit: MapObject) = unit is Monster && unit.stars >= 4


    override fun check() {}
    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            val locations = game.objects.filter { isTarget(it) }.map { it.location }.toSet()
            strategy.updateTargets(locations)
            strategy.setRequiredUnits(1)
        }
    }
}
