package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty

open class G_DealDamage(
    difficulty: GoalDifficulty = GoalDifficulty.EASY,
    private val target: Int = 15
) : Goal(difficulty, "Teile $target Schaden in Kämpfen aus") {
    private var damageDone = 0

    init {
        progressionGoal = target
    }

    override fun battleResult(result: BattleResult) {
        if (!result.isParticipating(player)) return
        damageDone += result.getDamageDealt(player)
    }

    override fun check() {
        updateProgression(damageDone)
        if (damageDone >= target) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {

    }
}
