package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import java.util.function.Consumer

object PlayerUtils {
    fun iteratePlayers(game: GameContainer, pauseBetween: Int, consumer: Consumer<Player>) {
        val current = game.currentTurn
        val players = ArrayList(game.activePlayers)
        for (i in players.indices) {
            val nr = (current + i) % players.size
            consumer.accept(players[nr])
            ProcessingUtils.pause(pauseBetween)
        }
    }

    fun isCommanderAlive(player: Player) = getCommander(player) != null

    fun getOtherPlayers(game: GameContainer, player: Player) = game.allPlayers.filter { it !== player }


    fun getCommander(player: Player) = player.units.filterIsInstance(Soldier::class.java).find { it.isCommander }

    fun isCommander(unit: Unit) = unit is Soldier && unit.isCommander

    fun getUnits(player: Player, location: Location) =
            location.units.filter { it.owner == player }

    fun getHostileArmy(player: Player, location: Location) =
            location.units.filter { isHostile(player, it) }

    private fun isHostile(player: Player, obj: MapObject) =
            if (obj is Unit) {
                player.fraction.isHostile(obj)
            } else false


    fun hasHostileArmy(player: Player, location: Location) = getHostileArmy(player, location).isNotEmpty()

    fun getHostileLocations(game: GameContainer, player: Player) =
            getOtherPlayers(game, player).flatMap { it.controlledLocations }.filter { hasHostileArmy(player, it) }

    fun getOwner(units: List<Unit>) = units.first().owner
}
