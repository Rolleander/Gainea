package com.broll.gainea.server.core.utils

import com.broll.gainea.misc.RandomUtils
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
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.google.common.collect.Lists
import org.apache.commons.lang3.ArrayUtils
import java.util.Collections
import java.util.function.Function
import java.util.stream.Collectors

object LocationUtils {
    fun getLocationNumbers(locations: Collection<Location?>?): ShortArray {
        val numbers = ShortArray(locations!!.size)
        val iterator = locations.iterator()
        var i = 0
        while (iterator.hasNext()) {
            numbers[i] = iterator.next().getNumber().toShort()
            i++
        }
        return numbers
    }

    fun isAreaType(location: Location?, vararg type: AreaType?): Boolean {
        if (location is Area) {
            return ArrayUtils.contains(type, location.type)
        }
        return false
    }

    fun filterByType(locations: List<Location>, vararg type: AreaType): List<Area> {
        return locations.filter { isAreaType(it, *type) }.map { it as Area }
    }

    fun getControlledLocationsIn(player: Player, expansionType: ExpansionType): List<Location?> {
        return player.controlledLocations.stream().filter { it: Location? -> it.getContainer().expansion.type == expansionType }.collect(Collectors.toList())
    }

    fun emptyOrControlled(location: Location?, player: Player): Boolean {
        return location.getInhabitants().stream().map { it: MapObject? ->
            if (it is Unit) {
                return@map it.owner === player
            }
            true
        }.reduce(true) { a: Boolean, b: Boolean -> java.lang.Boolean.logicalAnd(a, b) }
    }

    fun emptyOrWildMonster(location: Location?): Boolean {
        return location.getInhabitants().stream().map { it: MapObject? ->
            if (it is Monster) {
                return@map it.owner == null
            }
            false
        }.reduce(true) { a: Boolean, b: Boolean -> java.lang.Boolean.logicalAnd(a, b) }
    }

    fun noControlledUnits(location: Location): Boolean {
        return !location.inhabitants.stream().anyMatch { it: MapObject? ->
            if (it is Unit) {
                return@anyMatch it.owner != null
            }
            false
        }
    }

    fun getWildMonsterLocations(game: GameContainer): List<Location> {
        return game.objects.stream().filter { it: MapObject? -> it is Monster }.map { obj: MapObject? -> obj.getLocation() }.distinct().collect(Collectors.toList())
    }

    fun getMonsters(location: Location): List<Monster> {
        return location.getInhabitants().stream().filter { it: MapObject? -> it is Monster }.map { it: MapObject? -> it as Monster? }.collect(Collectors.toList())
    }

    fun getUnits(location: Location): List<Unit> {
        return location.getInhabitants().stream().filter { it: MapObject? -> it is Unit }.map { it: MapObject? -> it as Unit? }.collect(Collectors.toList())
    }

    fun getRandomFree(locations: List<Location>): Location {
        val free = locations!!.stream().filter { obj: Location? -> obj!!.isFree }.collect(Collectors.toList())
        return RandomUtils.pickRandom(free)
    }

    fun isInContinent(location: Location, id: ContinentID): Boolean {
        return if (location !is Ship && location.container is Continent) {
            (location.container as Continent).id === id
        } else false
    }

    fun isInIsland(location: Location, id: IslandID): Boolean {
        return if (location !is Ship && location.container is Island) {
            (location.container as Island).id === id
        } else false
    }

    fun pickRandom(map: MapContainer, amount: Int): List<Area?> {
        val areas = map.allAreas
        Collections.shuffle(areas)
        return areas!!.stream().limit(amount.toLong()).collect(Collectors.toList())
    }

    fun pickRandomEmpty(map: MapContainer?, amount: Int): List<Area?> {
        val areas = map.getAllAreas()
        Collections.shuffle(areas)
        return areas!!.stream().filter { it: Area? -> it.getInhabitants().isEmpty() }.limit(amount.toLong()).collect(Collectors.toList())
    }

    private fun routes(`object`: MapObject?): Function<Location?, MutableList<Location?>> {
        return Function { location: Location? ->
            `object`.setLocation(location)
            location.getConnectedLocations().stream().filter { to: Location? -> `object`!!.canMoveTo(to) }.collect(Collectors.toList())
        }
    }

    @JvmStatic
    fun getWalkingDistance(`object`: MapObject?, from: Location?, to: Location?): Int {
        val originalLocation = `object`.getLocation()
        val visited: MutableList<Location?> = Lists.newArrayList(from)
        var remaining: MutableList<Location?>
        val routes = routes(`object`)
        var distance = 0
        if (from === to) {
            return 0
        }
        remaining = routes.apply(from)
        do {
            distance++
            for (area in remaining) {
                if (area === to) {
                    `object`.setLocation(originalLocation)
                    return distance
                }
            }
            visited.addAll(remaining)
            remaining = remaining.stream().flatMap { it: Location? -> routes.apply(it).stream() }.collect(Collectors.toList())
            remaining.removeAll(visited)
        } while (!remaining.isEmpty())
        `object`.setLocation(originalLocation)
        return -1
    }

    fun getConnectedLocations(location: Location, maxDistance: Int): List<Location> {
        val visited: MutableList<Location?> = ArrayList()
        var remaining: MutableList<Location?> = Lists.newArrayList(location)
        var distance = 0
        while (distance < maxDistance) {
            distance++
            visited.addAll(remaining)
            remaining = remaining.stream().flatMap { it: Location? -> it.getConnectedLocations().stream() }.collect(Collectors.toList())
            remaining.removeAll(visited)
        }
        visited.addAll(remaining)
        visited.remove(location)
        return visited
    }
}
