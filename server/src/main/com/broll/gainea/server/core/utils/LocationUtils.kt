package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.Continent
import com.broll.gainea.server.core.map.ContinentID
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.Island
import com.broll.gainea.server.core.map.IslandID
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.MapContainer
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import org.apache.commons.lang3.ArrayUtils


object LocationUtils {
    fun getLocationNumbers(locations: Collection<Location>) =
            locations.map { it.number.toShort() }.toShortArray()


    fun isAreaType(location: Location, vararg type: AreaType): Boolean {
        if (location is Area) {
            return ArrayUtils.contains(type, location.type)
        }
        return false
    }

    fun filterByType(locations: List<Location>, vararg type: AreaType) = locations.filter { isAreaType(it, *type) }.map { it as Area }


    fun getControlledLocationsIn(player: Player, expansionType: ExpansionType) = player.controlledLocations.filter { it.container.expansion.type == expansionType }

    fun emptyOrControlled(location: Location, player: Player): Boolean {
        if (location.units.any { it.owner == player }) {
            return true
        }
        return location.free
    }

    fun emptyOrWildMonster(location: Location): Boolean {
        if (location.units.all { it.isNeutralMonster() }) {
            return true
        }
        return location.free
    }

    fun noPlayerUnits(location: Location) = location.units.all { it.owner.isNeutral() }

    fun getWildMonsterLocations(game: GameContainer) =
            game.objects.filterIsInstance<Monster>().map { it.location }.distinct()

    fun getMonsters(location: Location) = location.units.filterIsInstance<Monster>()

    fun getRandomFree(locations: List<Location>) = locations.filter { it.free }.randomOrNull()

    fun getRandomFree(locations: List<Location>, count: Int) = locations.filter { it.free }.shuffled().take(count)

    fun isInContinent(location: Location, id: ContinentID) =
            if (location !is Ship && location.container is Continent) {
                (location.container as Continent).id === id
            } else false


    fun isInIsland(location: Location, id: IslandID) =
            if (location !is Ship && location.container is Island) {
                (location.container as Island).id === id
            } else false


    fun pickRandom(map: MapContainer, amount: Int) = map.allAreas.shuffled().take(amount)

    fun pickRandomEmpty(map: MapContainer, amount: Int) = map.allAreas.filter { it.free }.shuffled().take(amount)

    private fun routes(obj: MapObject) = { location: Location ->
        obj.location = location
        location.connectedLocations.filter { obj.canMoveTo(it) }
    }

    @JvmStatic
    fun getWalkingDistance(obj: MapObject, from: Location, to: Location): Int? {
        val originalLocation = obj.location
        val visited = mutableListOf(from)
        var remaining: MutableList<Location>
        val routes = routes(obj)
        var distance = 0
        if (from === to) {
            return 0
        }
        remaining = routes(from).toMutableList()
        do {
            distance++
            for (area in remaining) {
                if (area === to) {
                    obj.location = originalLocation
                    return distance
                }
            }
            visited.addAll(remaining)
            remaining = remaining.flatMap { routes(it) }.toMutableList()
            remaining.removeAll(visited)
        } while (remaining.isNotEmpty())
        obj.location = originalLocation
        return null
    }

    fun getConnectedLocations(location: Location, maxDistance: Int): List<Location> {
        val visited = mutableListOf<Location>()
        var remaining = mutableListOf(location)
        var distance = 0
        while (distance < maxDistance) {
            distance++
            visited.addAll(remaining)
            remaining = remaining.flatMap { it.connectedLocations }.toMutableList()
            remaining.removeAll(visited)
        }
        visited.addAll(remaining)
        visited.remove(location)
        return visited
    }
}
