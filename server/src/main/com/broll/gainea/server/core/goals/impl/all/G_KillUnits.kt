package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.BotUtils
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.objects.Soldier

open class G_KillUnits(difficulty: GoalDifficulty = GoalDifficulty.EASY, private val killTarget: Int = 6) : Goal(difficulty, "Vernichte $killTarget Soldaten anderer Spieler durch Kämpfe") {
    private var kills = 0

    init {
        progressionGoal = killTarget
    }

    override fun battleResult(result: BattleResult) {
        val killed = result.getOpposingUnits(player).count { it.source is Soldier && it.dead }
        if (killed > 0) {
            kills += killed
            check()
        }
    }

    override fun check() {
        updateProgression(kills)
        if (kills >= killTarget) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            strategy.updateTargets(BotUtils.huntOtherPlayersTargets(player, game))
            strategy.setRequiredUnits(killTarget - kills)
        }
    }
}
