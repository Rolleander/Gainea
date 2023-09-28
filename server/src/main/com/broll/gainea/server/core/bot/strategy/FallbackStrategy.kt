package com.broll.gainea.server.core.bot.strategy

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player

object FallbackStrategy {

    object FallbackGoal : Goal(difficulty = GoalDifficulty.EASY, text = "fallback") {
        override fun check() {
        }

        override fun botStrategy(strategy: GoalStrategy) {
        }

    }

    fun create(botStrategy: BotStrategy, player: Player, game: GameContainer, constants: StrategyConstants): GoalStrategy {
        val strategy = GoalStrategy(botStrategy, FallbackGoal, player, game, constants)
        strategy.setPrepareStrategy {
            //attack monsters
            val locations = game.objects.filterIsInstance(Monster::class.java).mapNotNull { it.location }.toHashSet()
            strategy.updateTargets(locations)
            strategy.setRequiredUnits(0)
        }
        return strategy
    }
}

