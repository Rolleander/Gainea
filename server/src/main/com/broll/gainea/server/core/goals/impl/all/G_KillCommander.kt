package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.isCommander

class G_KillCommander :
    Goal(GoalDifficulty.EASY, "Besiege den Feldherr eines anderen Spielers im Kampf") {
    override fun unitKilled(unit: Unit, throughBattle: BattleResult?) {
        if ((throughBattle != null) && throughBattle.getOpposingUnits(player)
                .any { it.isCommander() && it.dead }
        ) {
            success()
        }
    }

    override fun check() {}
    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            val locations = game.allPlayers.filter { it !== player }.flatMap { it.units }
                .filter { it.isCommander() }.map { it.location }.toSet()
            strategy.updateTargets(locations)
            strategy.setRequiredUnits(5)
        }
    }
}
