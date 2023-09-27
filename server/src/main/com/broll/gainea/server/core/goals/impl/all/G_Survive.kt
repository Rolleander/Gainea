package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResultimport

com.broll.gainea.server.core.bot.strategy.GoalStrategyimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.goals.RoundGoalimport com.broll.gainea.server.core.objects.Unit
class G_Survive : RoundGoal(GoalDifficulty.EASY, "Verliere für " + TARGET + " Runden keine Einheit", TARGET) {
    override fun killed(unit: Unit?, throughBattle: BattleResult?) {
        if (unit.getOwner() === player) {
            resetRounds()
        }
    }

    override fun check() {
        progressRound()
    }

    override fun botStrategy(strategy: GoalStrategy) {
        //todo
    }

    companion object {
        private const val TARGET = 5
    }
}
