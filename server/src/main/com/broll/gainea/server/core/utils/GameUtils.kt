package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import java.util.stream.Collectors

object GameUtils {
    fun isGameEnd(game: GameContainer): Boolean {
        val maxScore = game.allPlayers.stream().mapToInt { it: Player? -> it.getGoalHandler().score }.max().asInt
        val round = game.rounds
        val roundLimit = game.gameSettings.roundLimit
        val scoreLimit = game.gameSettings.pointLimit
        val roundLimitReached = roundLimit > 0 && round > roundLimit
        val scoreLimitReached = scoreLimit > 0 && maxScore >= scoreLimit
        var end: Boolean
        end = if (scoreLimit > 0 && roundLimit > 0) {
            roundLimitReached && scoreLimitReached
        } else {
            roundLimitReached || scoreLimitReached
        }
        if (noActivePlayersRemaining(game)) {
            end = true
        }
        return end
    }

    fun noActivePlayersRemaining(game: GameContainer?): Boolean {
        return game.getActivePlayers().isEmpty() ||
                game.getActivePlayers().stream().allMatch { it: Player? -> it.getServerPlayer().isBot }
    }

    fun sendUpdate(game: GameContainer, player: Player, update: Any?, updateForOtherPlayers: Any?) {
        player.serverPlayer.sendTCP(update)
        sendUpdateExceptFor(game, updateForOtherPlayers, player)
    }

    fun sendUpdateExceptFor(game: GameContainer, update: Any?, exceptPlayer: Player?) {
        game.activePlayers.stream().filter { p: Player? -> p !== exceptPlayer }.forEach { p: Player? -> p.getServerPlayer().sendTCP(update) }
    }

    fun sendUpdate(game: GameContainer?, update: Any?) {
        game.getReactionHandler().actionHandlers.reactionActions.sendGameUpdate(update)
    }

    fun getUnits(objects: List<MapObject?>?): List<Unit?> {
        return objects!!.stream().filter { it: MapObject? -> it is Unit }.map { it: MapObject? -> it as Unit? }.collect(Collectors.toList())
    }

    fun remove(game: GameContainer, `object`: MapObject?): Boolean {
        val owner = `object`.getOwner()
        `object`.getLocation().inhabitants.remove(`object`)
        val removed: Boolean
        removed = owner?.units?.remove(`object`) ?: game.objects.remove(`object`)
        if (removed) {
            game.updateReceiver.unregister(`object`)
        }
        return removed
    }

    fun place(`object`: MapObject, location: Location?) {
        val previous = `object`.location
        previous?.inhabitants?.remove(`object`)
        `object`.location = location
        location.getInhabitants().add(`object`)
    }

    fun getTotalStartMonsters(game: GameContainer): Int {
        val expansions = game.map.expansions.size
        val monstersPerExpansion = game.gameSettings.monsterCount
        return expansions * monstersPerExpansion
    }

    fun getCurrentMonsters(game: GameContainer): Int {
        return game.objects.stream().filter { it: MapObject? -> it is Monster }.count().toInt()
    }
}
