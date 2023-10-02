package com.broll.gainea.server.core.bot.strategy

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player

object FallbackStrategy {

    class FallbackGoal(game: Game, owner: Player) : Goal(difficulty = GoalDifficulty.EASY, text = "fallback") {

        init {
            init(game, owner)
        }

        override fun check() {
        }

        override fun botStrategy(strategy: GoalStrategy) {
        }

    }

    fun create(botStrategy: BotStrategy, player: Player, game: Game, constants: StrategyConstants): GoalStrategy {
        val goal = FallbackGoal(game, player)
        val strategy = GoalStrategy(botStrategy, goal, player, game, constants)
        strategy.setPrepareStrategy {
            //attack monsters
            val locations = game.objects.filterIsInstance(Monster::class.java).map { it.location }.toSet()
            strategy.updateTargets(locations)
            strategy.setRequiredUnits(0)
        }
        return strategy
    }
}

