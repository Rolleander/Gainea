package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResultimport

com.broll.gainea.server.core.bot.BotUtilsimport com.broll.gainea.server.core.bot.strategy.GoalStrategyimport com.broll.gainea.server.core.goals.Goalimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.objects.Soldierimport com.broll.gainea.server.core.objects.Unit
open class G_KillUnits @JvmOverloads constructor(difficulty: GoalDifficulty = GoalDifficulty.EASY, private val killTarget: Int = 6) : Goal(difficulty, "Vernichte $killTarget Soldaten anderer Spieler durch Kämpfe") {
    private var kills = 0

    init {
        setProgressionGoal(killTarget)
    }

    override fun battleResult(result: BattleResult) {
        if (result.isAttacker(player)) {
            killTarget += result.killedDefenders.stream().filter { it: Unit? -> it is Soldier }.count().toInt()
            check()
        } else if (result.isDefender(player)) {
            killTarget += result.killedAttackers.stream().filter { it: Unit? -> it is Soldier }.count().toInt()
            check()
        }
    }

    override fun check() {
        updateProgression(killTarget)
        if (killTarget >= killTarget) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            strategy.updateTargets(BotUtils.huntOtherPlayersTargets(player!!, game!!))
            strategy.setRequiredUnits(killTarget - killTarget)
        }
    }
}
