package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.RoundGoal
import com.broll.gainea.server.core.objects.Unit

class G_Survive :
    RoundGoal(GoalDifficulty.EASY, "Verliere für " + TARGET + " Runden keine Einheit", TARGET) {
    override fun unitKilled(unit: Unit, throughBattle: BattleResult?) {
        if (unit.owner === player) {
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
