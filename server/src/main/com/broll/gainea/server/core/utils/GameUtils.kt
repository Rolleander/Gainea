package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.map.*

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.actions.AbstractActionHandler
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.network.gameData
import com.google.protobuf.Message

fun <T : AbstractActionHandler<*, *>> Game.getActionHandler(handlerClass: Class<T>) =
    reactionHandler.actionHandlers.getHandler(handlerClass)

fun Game.endPlayersTurn(player: Player) {
    if (currentPlayer == player && lobby.gameData.gameRoundsStarted) {
        endTurn()
    }
}

fun Game.skipTurn() {
    battleHandler.reset()
    reactionHandler.skipRequiredActions()
    displayMessage("Zug übersrprungen!", sound = "smash.ogg")
    endTurn()
}

fun Game.endTurn() = reactionHandler.actionHandlers.reactionActions.endTurn()
fun Game.noActivePlayersRemaining() =
    activePlayers.isEmpty() || activePlayers.all { it.serverPlayer.isBot }

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

fun Game.sendUpdate(player: Player, update: Message, updateForOtherPlayers: Message) {
    player.serverPlayer.send(update)
    sendUpdateExceptFor(updateForOtherPlayers, player)
}

fun Game.sendUpdateExceptFor(update: Message, exceptPlayer: Player) {
    activePlayers.filter { it !== exceptPlayer }.forEach { it.serverPlayer.send(update) }
}

fun Game.sendUpdate(update: Message) {
    reactionHandler.actionHandlers.reactionActions.sendGameUpdate(update)
}

fun Game.getAllUnits() = listOf(objects.getUnits(), activePlayers.flatMap { it.units }).flatten()


fun List<MapObject>.getUnits() = filterIsInstance<Unit>()


fun Game.remove(target: MapObject): Boolean {
    target.location.inhabitants.remove(target)
    updateReceiver.unregister(target)
    return objects.remove(target) ||
            allPlayers.any { it.units.remove(target) }
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
