package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import java.util.Objects
import java.util.Optional
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.Stream

object PlayerUtils {
    fun iteratePlayers(game: GameContainer, pauseBetween: Int, consumer: Consumer<Player?>) {
        val current = game.currentTurn
        val players = ArrayList(game.activePlayers)
        for (i in players.indices) {
            val nr = (current + i) % players.size
            consumer.accept(players[nr])
            ProcessingUtils.pause(pauseBetween)
        }
    }

    fun isCommanderAlive(player: Player?): Boolean {
        return getCommander(player).isPresent
    }

    fun getOtherPlayers(game: GameContainer, player: Player) = game.allPlayers.filter {  it !== player }


    fun getCommander(player: Player?): Optional<Soldier?> {
        return player.getUnits().stream().filter { obj: Unit? -> isCommander() }.map { it: Unit? -> it as Soldier? }.findFirst()
    }

    fun isCommander(unit: Unit?): Boolean {
        return unit is Soldier && unit.isCommander
    }

    fun getUnits(player: Player?, location: Location?): List<Unit?> {
        return player.getUnits().stream().filter { it: Unit? -> it.getLocation() === location }.collect(Collectors.toList())
    }

    fun getHostileArmy(player: Player?, location: Location?): List<Unit?> {
        return location.getInhabitants().stream().filter { inhabitant: MapObject? -> isHostile(player, inhabitant) }
                .map { o: MapObject? -> o as Unit? }.collect(Collectors.toList())
    }

    private fun isHostile(player: Player?, `object`: MapObject?): Boolean {
        return if (`object` is Unit) {
            player.getFraction().isHostile(`object`)
        } else false
    }

    fun hasHostileArmy(player: Player?, location: Location?): Boolean {
        return location.getInhabitants().stream().anyMatch { inhabitant: MapObject? -> isHostile(player, inhabitant) }
    }

    fun getHostileLocations(game: GameContainer, player: Player?): Set<Location?> {
        val locations: MutableSet<Location?> = HashSet()
        getOtherPlayers(game, player).map { obj: Player? -> obj.getControlledLocations() }.forEach { collection: List<Location?>? -> locations.addAll(collection!!) }
        locations.removeIf { location: Location? -> getHostileArmy(player, location).isEmpty() }
        return locations
    }

    fun getOwner(units: List<Unit?>?): Player? {
        return units!!.stream().map { obj: Unit? -> obj.getOwner() }.filter { obj: Player? -> Objects.nonNull(obj) }.findFirst().orElse(null)
    }
}
