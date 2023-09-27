package com.broll.gainea.server.core.bot.strategy

import com.broll.gainea.server.core.GameContainerimport

com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.player.Playerimport java.util.stream.Collectors
object FallbackStrategy {
    fun create(botStrategy: BotStrategy, player: Player?, game: GameContainer?, constants: StrategyConstants): GoalStrategy {
        val strategy = GoalStrategy(botStrategy, null, player, game, constants)
        strategy.setPrepareStrategy {

            //attack monsters
            val locations = game.getObjects().stream().filter { it: MapObject? -> it is Monster }.map { obj: MapObject? -> obj.getLocation() }.collect(Collectors.toSet())
            strategy.updateTargets(locations)
            strategy.setRequiredUnits(0)
        }
        return strategy
    }
}
