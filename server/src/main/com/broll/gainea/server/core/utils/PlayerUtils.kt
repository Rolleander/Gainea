package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.IUnit
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.resolve
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral

fun Game.getWeakestPlayer() =
        activePlayers.minBy { it.goalHandler.score * 15 + it.units.sumOf { unit -> unit.rank() } }

fun Game.iteratePlayers(pauseBetween: Int, consumer: (Player) -> kotlin.Unit) {
    val current = currentTurn
    val players = ArrayList(activePlayers)
    for (i in players.indices) {
        val nr = (current + i) % players.size
        consumer(players[nr])
        ProcessingUtils.pause(pauseBetween)
    }
}

fun Player.isCommanderAlive() = getCommander() != null

fun Game.getOtherPlayers(player: Player) = allPlayers.filter { it !== player }

fun Player.getCommander() = units.filterIsInstance(Soldier::class.java).find { it.isCommander }

fun IUnit.isCommander() = resolve().run { this is Soldier && this.isCommander }

fun Player.getUnits(location: Location) =
        location.units.filter { it.owner == this }

fun Player.getHostileArmy(location: Location) =
        location.units.filter { isHostile(it) }

fun Player.isHostile(obj: MapObject) =
        if (obj is Unit) {
            this.fraction.isHostile(obj)
        } else false


fun Player.hasHostileArmy(location: Location) = getHostileArmy(location).isNotEmpty()

fun Game.getEnemyLocations(player: Player) =
        getOtherPlayers(player).flatMap { it.controlledLocations }.filter { player.hasHostileArmy(it) }

fun List<Unit>.owner() = find { !it.owner.isNeutral() }?.owner ?: first().owner

