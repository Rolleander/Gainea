package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral

object GameUtils {
    fun isGameEnd(game: GameContainer): Boolean {
        val maxScore = game.allPlayers.maxOf { it.goalHandler.score }
        val round = game.rounds
        val roundLimit = game.gameSettings.roundLimit
        val scoreLimit = game.gameSettings.pointLimit
        val roundLimitReached = roundLimit in 1..<round
        val scoreLimitReached = scoreLimit in 1..maxScore
        if (noActivePlayersRemaining(game)) {
            return true
        }
        return if (scoreLimit > 0 && roundLimit > 0) {
            roundLimitReached && scoreLimitReached
        } else {
            roundLimitReached || scoreLimitReached
        }
    }

    fun noActivePlayersRemaining(game: GameContainer) =
            game.activePlayers.isEmpty() || game.activePlayers.all { it.serverPlayer.isBot }


    fun sendUpdate(game: GameContainer, player: Player, update: Any, updateForOtherPlayers: Any) {
        player.serverPlayer.sendTCP(update)
        sendUpdateExceptFor(game, updateForOtherPlayers, player)
    }

    fun sendUpdateExceptFor(game: GameContainer, update: Any, exceptPlayer: Player) {
        game.activePlayers.filter { it !== exceptPlayer }.forEach { it.serverPlayer.sendTCP(update) }
    }

    fun sendUpdate(game: GameContainer, update: Any) {
        game.reactionHandler.actionHandlers.reactionActions.sendGameUpdate(update)
    }

    fun getAllUnits(game: GameContainer) = listOf(getUnits(game.objects), game.activePlayers.flatMap { it.units }).flatten()
    fun getUnits(objects: List<MapObject>) = objects.filterIsInstance(Unit::class.java)

    fun remove(game: GameContainer, target: MapObject): Boolean {
        target.location.inhabitants.remove(target)
        target.owner
        val removed = if (target.owner.isNeutral()) {
            game.objects.remove(target)
        } else {
            target.owner.units.remove(target)
        }
        if (removed) {
            game.updateReceiver.unregister(target)
        }
        return removed
    }

    fun place(target: MapObject, location: Location) {
        target.location.inhabitants.remove(target)
        target.location = location
        location.inhabitants += target
    }

    fun getTotalStartMonsters(game: GameContainer): Int {
        val expansions = game.map.expansions.size
        val monstersPerExpansion = game.gameSettings.monsterCount
        return expansions * monstersPerExpansion
    }

    fun countNeutralMonsters(game: GameContainer) = game.objects.count { it is Monster }

}
