package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty

open class G_WinStreak(
    difficulty: GoalDifficulty = GoalDifficulty.EASY,
    private val winTarget: Int = 3
) : Goal(difficulty, "Gewinne $winTarget Kämpfe in Folge, ohne eine Einheit dabei zu verlieren") {
    private var wins = 0

    init {
        progressionGoal = winTarget
    }

    override fun battleResult(result: BattleResult) {
        if (!result.isParticipating(player)) return
        if (result.isWinner(player) && result.getUnits(player).none { it.dead }) {
            wins++
            check()
        } else {
            wins = 0;
            resetProgression()
        }
    }

    override fun check() {
        updateProgression(wins)
        if (wins >= winTarget) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {

    }
}
