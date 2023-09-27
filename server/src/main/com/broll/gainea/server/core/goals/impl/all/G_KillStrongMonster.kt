package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import java.util.stream.Collectors

class G_KillStrongMonster : Goal(GoalDifficulty.MEDIUM, "Besiege ein Monster mit 4 oder mehr Sternen mit nur einer Einheit") {
    override fun battleResult(result: BattleResult) {
        if (result.getOpposingUnits(player)!!.stream().anyMatch { unit: Unit -> isTarget(unit) }) {
            success()
        }
    }

    private fun isTarget(unit: Unit): Boolean {
        if (unit is Monster) {
            return unit.stars >= 4 && unit.isDead()
        }
        return false
    }

    override fun check() {}
    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            val locations = game.objects.stream().filter { it: MapObject? -> it is Unit && isTarget(it) }.map { obj: MapObject? -> obj.getLocation() }.collect(Collectors.toSet())
            strategy.updateTargets(locations)
            strategy.setRequiredUnits(1)
        }
    }
}
