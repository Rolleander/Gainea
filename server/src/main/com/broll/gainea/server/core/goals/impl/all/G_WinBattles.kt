package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty

open class G_WinBattles(difficulty: GoalDifficulty = GoalDifficulty.MEDIUM, private val winTarget: Int = 6) : Goal(difficulty, "Gewinne $winTarget Schlachten gegen andere Spieler") {
    private var wins = 0

    init {
        setProgressionGoal(winTarget)
    }

    override fun battleResult(result: BattleResult) {
        if (result.isWinner(player) && !result.isNeutralLoser) {
            wins++
            check()
        }
    }

    override fun check() {
        updateProgression(wins)
        if (wins >= winTarget) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            val locations = game.allPlayers.filter { it !== player }
                    .flatMap { it.units }.map { it.location }.toSet()
            strategy.updateTargets(locations)
            strategy.setRequiredUnits((winTarget - wins) * 3)
        }
    }
}
