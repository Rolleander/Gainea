package com.broll.gainea.server.core.utils

import com.broll.gainea.server.core.Game
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
import com.broll.gainea.server.core.utils.UnitControl.isNeutralMonster


fun Collection<Location>.getLocationNumbers() =
        map { it.number.toShort() }.toShortArray()

fun Location.isAreaType(vararg type: AreaType): Boolean {
    if (this is Area) {
        return type.contains(this.type)
    }
    return false
}

fun Location.getHostileUnits(player: Player) =
        units.filter { player.isHostile(it) }

fun List<Location>.filterByType(vararg type: AreaType) = filter { it.isAreaType(*type) }.map { it as Area }

fun Player.getControlledLocationsIn(expansionType: ExpansionType) = controlledLocations.filter { it.container.expansion.type == expansionType }

fun Location.emptyOrControlledBy(player: Player): Boolean {
    if (units.any { it.owner == player }) {
        return true
    }
    return free
}

fun Location.emptyOrWildMonster(): Boolean {
    if (units.all { it.isNeutralMonster() }) {
        return true
    }
    return free
}

fun Location.noPlayerUnits() = units.all { it.owner.isNeutral() }

fun Game.getWildMonsterLocations() =
        objects.filterIsInstance<Monster>().map { it.location }.distinct()

fun Location.getMonsters() = units.filterIsInstance<Monster>()

fun List<Location>.getRandomFree() = filter { it.free }.randomOrNull()

fun List<Location>.getRandomFree(count: Int) = filter { it.free }.shuffled().take(count)

fun Location.isInContinent(id: ContinentID) =
        if (this !is Ship && this.container is Continent) {
            (this.container as Continent).id === id
        } else false


fun Location.isInIsland(id: IslandID) =
        if (this !is Ship && this.container is Island) {
            (this.container as Island).id === id
        } else false


fun MapContainer.pickRandom(amount: Int) = allAreas.shuffled().take(amount)

fun MapContainer.pickRandomEmpty(amount: Int) = allAreas.filter { it.free }.shuffled().take(amount)

private fun routes(obj: MapObject) = { location: Location ->
    obj.location = location
    location.connectedLocations.filter { obj.canMoveTo(it) }
}

fun MapObject.getWalkingDistance(from: Location, to: Location): Int? {
    val originalLocation = location
    val visited = mutableListOf(from)
    var remaining: MutableList<Location>
    val routes = routes(this)
    var distance = 0
    if (from === to) {
        return 0
    }
    remaining = routes(from).toMutableList()
    do {
        distance++
        for (area in remaining) {
            if (area === to) {
                location = originalLocation
                return distance
            }
        }
        visited.addAll(remaining)
        remaining = remaining.flatMap { routes(it) }.toMutableList()
        remaining.removeAll(visited)
    } while (remaining.isNotEmpty())
    location = originalLocation
    return null
}

fun Location.getConnectedLocations(maxDistance: Int): List<Location> {
    val visited = mutableListOf<Location>()
    var remaining = mutableListOf(this)
    var distance = 0
    while (distance < maxDistance) {
        distance++
        visited.addAll(remaining)
        remaining = remaining.flatMap { it.connectedLocations }.toMutableList()
        remaining.removeAll(visited)
    }
    visited.addAll(remaining)
    visited.remove(this)
    return visited
}