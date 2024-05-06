package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.monster.Monster

class G_KillStrongMonster2 : Goal(
    GoalDifficulty.EASY,
    "Besiege ein Monster mit $KILLS oder mehr Kills."
) {
    override fun battleResult(result: BattleResult) {
        if (result.getOpposingUnits(player).any { isTarget(it.source) && it.dead }) {
            success()
        }
    }

    private fun isTarget(unit: MapObject) = unit is Monster && unit.kills >= KILLS

    override fun check() {}
    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            val locations = game.objects.filter { isTarget(it) }.map { it.location }.toSet()
            strategy.updateTargets(locations)
            strategy.setRequiredUnits(3)
        }
    }

    companion object {
        private const val KILLS = 4
    }
}