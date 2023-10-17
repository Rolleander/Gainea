package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral


fun Game.endTurn() = reactionHandler.actionHandlers.reactionActions.endTurn()
fun Game.noActivePlayersRemaining() = activePlayers.isEmpty() || activePlayers.all { it.serverPlayer.isBot }

fun Game.isGameEnd(): Boolean {
    val maxScore = allPlayers.maxOf { it.goalHandler.score }
    val round = rounds
    val roundLimit = gameSettings.roundLimit
    val scoreLimit = gameSettings.pointLimit
    val roundLimitReached = roundLimit in 1..<round
    val scoreLimitReached = scoreLimit in 1..maxScore
    if (noActivePlayersRemaining()) {
        return true
    }
    return if (scoreLimit > 0 && roundLimit > 0) {
        roundLimitReached && scoreLimitReached
    } else {
        roundLimitReached || scoreLimitReached
    }
}

fun Game.sendUpdate(player: Player, update: Any, updateForOtherPlayers: Any) {
    player.serverPlayer.sendTCP(update)
    sendUpdateExceptFor(updateForOtherPlayers, player)
}

fun Game.sendUpdateExceptFor(update: Any, exceptPlayer: Player) {
    activePlayers.filter { it !== exceptPlayer }.forEach { it.serverPlayer.sendTCP(update) }
}

fun Game.sendUpdate(update: Any) {
    reactionHandler.actionHandlers.reactionActions.sendGameUpdate(update)
}

fun Game.getAllUnits() = listOf(objects.getUnits(), activePlayers.flatMap { it.units }).flatten()


fun List<MapObject>.getUnits() = filterIsInstance<Unit>()


fun Game.remove(target: MapObject): Boolean {
    target.location.inhabitants.remove(target)
    target.owner
    val removed = if (target.owner.isNeutral()) {
        objects.remove(target)
    } else {
        target.owner.units.remove(target)
    }
    if (removed) {
        updateReceiver.unregister(target)
    }
    return removed
}

fun MapObject.place(location: Location) {
    this.location.inhabitants.remove(this)
    this.location = location
    location.inhabitants += this
}

fun Game.getTotalStartMonsters(): Int {
    val expansions = map.expansions.size
    val monstersPerExpansion = gameSettings.monsterCount
    return expansions * monstersPerExpansion
}

fun Game.countNeutralMonsters() = objects.count { it is Monster }
