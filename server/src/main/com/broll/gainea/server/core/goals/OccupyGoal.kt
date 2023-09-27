package com.broll.gainea.server.core.goals

import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaCollection
import com.broll.gainea.server.core.map.AreaID
import com.broll.gainea.server.core.map.Continent
import com.broll.gainea.server.core.map.ContinentID
import com.broll.gainea.server.core.map.Expansion
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.Island
import com.broll.gainea.server.core.map.IslandID
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.MapContainer
import com.broll.gainea.server.core.objects.MapObject
import org.slf4j.LoggerFactory
import java.util.Arrays
import java.util.Objects
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

abstract class OccupyGoal(difficulty: GoalDifficulty, text: String?) : Goal(difficulty, text) {
    private val conditions: MutableMap<Location, Function<Location, Boolean>> = HashMap()
    protected var map: MapContainer? = null
    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setRequiredUnits(locations.size)
        strategy.updateTargets(locations.stream().collect(Collectors.toSet()))
    }

    override fun validForGame(): Boolean {
        map = game.map
        try {
            initOccupations()
        } catch (e: MissingExpansionException) {
            return false
        }
        //if any area is null, then its not part of the expansions, so goal is invalid
        for (area in locations) {
            if (area == null) {
                return false
            }
        }
        if (locations.isEmpty()) {
            //no locations -> missing expansion
            return false
        }
        //set expansion restrictions by required locations
        setExpansionRestriction(*Arrays.stream<ExpansionType>(ExpansionType.entries.toTypedArray()).filter { type: ExpansionType -> hasLocationsOf(type) }.toArray<ExpansionType> { _Dummy_.__Array__() })
        setProgressionGoal(locations.size)
        return true
    }

    private fun hasLocationsOf(type: ExpansionType): Boolean {
        for (location in locations) {
            val expansion = map!!.getExpansion(type)
            if (expansion != null && expansion.allLocations.contains(location)) {
                return true
            }
        }
        return false
    }

    protected abstract fun initOccupations()
    private fun assureIslandExists(vararg islands: IslandID) {
        for (island in islands) {
            if (map!!.getIsland(island) == null) {
                throw MissingExpansionException()
            }
        }
    }

    private fun assureContinentExists(vararg continents: ContinentID) {
        for (continent in continents) {
            if (map!!.getContinent(continent) == null) {
                throw MissingExpansionException()
            }
        }
    }

    private fun assureExpansionExists(vararg expansions: ExpansionType) {
        for (expansion in expansions) {
            if (map!!.getExpansion(expansion) == null) {
                throw MissingExpansionException()
            }
        }
    }

    protected fun occupy(vararg areas: AreaID): List<Area?> {
        val list = Arrays.stream(areas).map { id: AreaID -> map!!.getArea(id) }.filter { obj: Area? -> Objects.nonNull(obj) }.collect(Collectors.toList())
        locations.addAll(list)
        return list
    }

    protected fun occupy(filter: Function<Area?, Boolean?>, vararg areas: AreaID): List<Area?> {
        val list = Arrays.stream(areas).map { id: AreaID -> map!!.getArea(id) }.filter { obj: Area? -> Objects.nonNull(obj) }.collect(Collectors.toList())
        list.removeIf { it: Area? -> !filter.apply(it)!! }
        locations.addAll(list)
        return list
    }

    protected fun occupy(vararg islands: IslandID): List<Area?> {
        assureIslandExists(*islands)
        val list: MutableList<Area?> = ArrayList()
        Arrays.stream(islands).map { id: IslandID -> map!!.getIsland(id) }.filter { obj: Island? -> Objects.nonNull(obj) }.map { obj: Island? -> obj.getAreas() }.forEach { areas: List<Area?>? -> list.addAll(areas!!) }
        locations.addAll(list)
        return list
    }

    protected fun occupy(filter: Function<Area?, Boolean?>, vararg islands: IslandID): List<Area?> {
        assureIslandExists(*islands)
        val list: MutableList<Area?> = ArrayList()
        Arrays.stream(islands).map { id: IslandID -> map!!.getIsland(id) }.filter { obj: Island? -> Objects.nonNull(obj) }.map { obj: Island? -> obj.getAreas() }.forEach { areas: List<Area?>? -> list.addAll(areas!!) }
        list.removeIf { it: Area? -> !filter.apply(it)!! }
        locations.addAll(list)
        return list
    }

    protected fun occupy(vararg continents: ContinentID): List<Area?> {
        assureContinentExists(*continents)
        val list: MutableList<Area?> = ArrayList()
        Arrays.stream(continents).map { id: ContinentID -> map!!.getContinent(id) }.filter { obj: Continent? -> Objects.nonNull(obj) }.map { obj: Continent? -> obj.getAreas() }.forEach { areas: List<Area?>? -> list.addAll(areas!!) }
        locations.addAll(list)
        return list
    }

    protected fun occupy(filter: Function<Area?, Boolean?>, vararg continents: ContinentID): List<Area?> {
        assureContinentExists(*continents)
        val list: MutableList<Area?> = ArrayList()
        Arrays.stream(continents).map { id: ContinentID -> map!!.getContinent(id) }.filter { obj: Continent? -> Objects.nonNull(obj) }.map { obj: Continent? -> obj.getAreas() }.forEach { areas: List<Area?>? -> list.addAll(areas!!) }
        list.removeIf { it: Area? -> !filter.apply(it)!! }
        locations.addAll(list)
        return list
    }

    protected fun occupy(vararg expansions: ExpansionType): List<Area?> {
        assureExpansionExists(*expansions)
        val list: MutableList<Area?> = ArrayList()
        Arrays.stream(expansions).map { type: ExpansionType -> map!!.getExpansion(type) }.filter { obj: Expansion? -> Objects.nonNull(obj) }.map { obj: Expansion? -> obj.getContents() }.forEach { col: List<AreaCollection?>? -> col!!.stream().map { obj: AreaCollection? -> obj.getAreas() }.forEach { areas: List<Area?>? -> list.addAll(areas!!) } }
        locations.addAll(list)
        return list
    }

    protected fun occupy(filter: Function<Area?, Boolean?>, vararg expansions: ExpansionType): List<Area?> {
        assureExpansionExists(*expansions)
        val list: MutableList<Area?> = ArrayList()
        Arrays.stream(expansions).map { type: ExpansionType -> map!!.getExpansion(type) }.filter { obj: Expansion? -> Objects.nonNull(obj) }.map { obj: Expansion? -> obj.getContents() }.forEach { col: List<AreaCollection?>? -> col!!.stream().map { obj: AreaCollection? -> obj.getAreas() }.forEach { areas: List<Area?>? -> list.addAll(areas!!) } }
        list.removeIf { it: Area? -> !filter.apply(it)!! }
        locations.addAll(list)
        return list
    }

    protected fun occupy(locations: List<Location>): List<Location> {
        this.locations.addAll(locations)
        return locations
    }

    protected fun occupy(vararg locations: Location?): List<Location> {
        this.locations.addAll(Arrays.asList(*locations))
        return Arrays.asList(*locations)
    }

    protected fun occupy(stream: Stream<out Location?>): List<Location?> {
        val list = stream.collect(Collectors.toList())
        locations.addAll(list)
        return list
    }

    protected fun condition(locations: List<Location>, vararg conditions: Function<Location, Boolean>) {
        locations.forEach(Consumer { location: Location -> this.conditions[location] = Function { loc: Location -> Arrays.stream(conditions).map { it: Function<Location, Boolean> -> it.apply(loc) }.reduce(true) { a: Boolean, b: Boolean -> java.lang.Boolean.logicalAnd(a, b) } } })
    }

    override fun check() {
        val occupiedLocations = player.controlledLocations
        var success = true
        var progress = 0
        for (location in locations) {
            val condition = conditions[location]
            if (condition != null) {
                if (condition.apply(location)) {
                    progress++
                } else {
                    //condition for the location not satisfied
                    success = false
                }
            } else {
                //default condition: simply occupied
                if (occupiedLocations!!.contains(location)) {
                    progress++
                } else {
                    //area not occupied by player
                    success = false
                }
            }
        }
        updateProgression(progress)
        if (success) {
            success()
        }
    }

    override fun moved(units: List<MapObject?>?, location: Location?) {
        if (units!![0].getOwner() === player) {
            //unit of this player moved, check occupy condition
            check()
        }
    }

    override fun spawned(`object`: MapObject, location: Location) {
        if (`object`.owner === player) {
            //unit of this player spawned, check occupy condition
            check()
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(OccupyGoal::class.java)
    }
}
