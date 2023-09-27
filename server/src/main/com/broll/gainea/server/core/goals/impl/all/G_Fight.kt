package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResultimport

com.broll.gainea.server.core.bot.BotUtilsimport com.broll.gainea.server.core.bot.strategy.GoalStrategyimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.goals.RoundGoal
class G_Fight : RoundGoal(GoalDifficulty.EASY, "Sei für 5 aufeinanderfolgende Runden an Kämpfen beteiligt", 5) {
    private var fighting = false
    override fun battleResult(result: BattleResult) {
        if (result.isParticipating(player)) {
            fighting = true
        }
    }

    override fun check() {
        if (fighting) {
            progressRound()
        } else {
            resetRounds()
        }
        fighting = false
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setPrepareStrategy {
            strategy.updateTargets(BotUtils.huntOtherPlayersTargets(player!!, game!!))
            strategy.setRequiredUnits(5)
        }
    }
}
