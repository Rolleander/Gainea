package com.broll.gainea.server.core.goals

import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaID
import com.broll.gainea.server.core.map.ContinentID
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.IslandID
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.MapContainer
import com.broll.gainea.server.core.objects.MapObject
import org.slf4j.LoggerFactory

abstract class OccupyGoal(difficulty: GoalDifficulty, text: String) : Goal(difficulty, text) {
    private val conditions = HashMap<Location, (Location) -> Boolean>()
    protected lateinit var map: MapContainer
    protected var autoCheckProgressions = true
    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setRequiredUnits(locations.size)
        strategy.updateTargets(locations.toHashSet())
    }

    override fun validForGame(): Boolean {
        map = game.map
        try {
            initOccupations()
        } catch (e: MissingExpansionException) {
            return false
        }
        if (locations.isEmpty()) {
            //no locations -> missing expansion
            return false
        }
        //set expansion restrictions by required locations
        setExpansionRestriction(*ExpansionType.entries.filter { hasLocationsOf(it) }.toTypedArray())
        if (autoCheckProgressions) {
            progressionGoal = locations.size
        }
        return true
    }

    private fun hasLocationsOf(type: ExpansionType): Boolean {
        for (location in locations) {
            val expansion = map.getExpansion(type)
            if (expansion != null && expansion.allLocations.contains(location)) {
                return true
            }
        }
        return false
    }

    protected abstract fun initOccupations()

    private fun assureAreaExists(vararg areas: AreaID) {
        if (areas.any { map.getArea(it) == null }) {
            throw MissingExpansionException()
        }
    }

    private fun assureIslandExists(vararg islands: IslandID) {
        if (islands.any { map.getIsland(it) == null }) {
            throw MissingExpansionException()
        }
    }

    private fun assureContinentExists(vararg continents: ContinentID) {
        if (continents.any { map.getContinent(it) == null }) {
            throw MissingExpansionException()
        }
    }

    private fun assureExpansionExists(vararg expansions: ExpansionType) {
        if (expansions.any { map.getExpansion(it) == null }) {
            throw MissingExpansionException()
        }
    }

    protected fun occupy(vararg areas: AreaID): List<Area> {
        assureAreaExists(*areas)
        val list = areas.mapNotNull { map.getArea(it) }
        locations.addAll(list)
        return list
    }

    protected fun occupy(filter: (Area) -> Boolean, vararg areas: AreaID): List<Area> {
        assureAreaExists(*areas)
        val list = areas.mapNotNull { map.getArea(it) }.filter(filter)
        locations.addAll(list)
        return list
    }

    protected fun occupy(vararg islands: IslandID): List<Area> {
        assureIslandExists(*islands)
        val list = islands.mapNotNull { map.getIsland(it) }.flatMap { it.areas }
        locations.addAll(list)
        return list
    }

    protected fun occupy(filter: (Area) -> Boolean, vararg islands: IslandID): List<Area> {
        assureIslandExists(*islands)
        val list = islands.mapNotNull { map.getIsland(it) }.flatMap { it.areas }.filter(filter)
        locations.addAll(list)
        return list
    }

    protected fun occupy(vararg continents: ContinentID): List<Area> {
        assureContinentExists(*continents)
        val list = continents.mapNotNull { map.getContinent(it) }.flatMap { it.areas }
        locations.addAll(list)
        return list
    }

    protected fun occupy(filter: (Area) -> Boolean, vararg continents: ContinentID): List<Area> {
        assureContinentExists(*continents)
        val list =
            continents.mapNotNull { map.getContinent(it) }.flatMap { it.areas }.filter(filter)
        locations.addAll(list)
        return list
    }

    protected fun occupy(vararg expansions: ExpansionType): List<Area> {
        assureExpansionExists(*expansions)
        val list = expansions.mapNotNull { map.getExpansion(it) }.flatMap { it.allAreas }
        locations.addAll(list)
        return list
    }

    protected fun occupy(filter: (Area) -> Boolean, vararg expansions: ExpansionType): List<Area> {
        assureExpansionExists(*expansions)
        val list =
            expansions.mapNotNull { map.getExpansion(it) }.flatMap { it.allAreas }.filter(filter)
        locations.addAll(list)
        return list
    }

    protected fun occupy(locations: List<Location>): List<Location> {
        this.locations.addAll(locations.toList())
        return locations.toList()
    }

    protected fun occupy(vararg locations: Location): List<Location> {
        this.locations.addAll(locations.toList())
        return locations.toList()
    }

    protected fun condition(locations: List<Location>, vararg conditions: (Location) -> Boolean) {
        locations.forEach {
            this.conditions[it] = { location ->
                conditions.all { condition -> condition(location) }
            }
        }
    }

    override fun check() {
        val occupiedLocations = player.controlledLocations
        var success = true
        var progress = 0
        for (location in locations) {
            val condition = conditions[location]
            if (condition != null) {
                if (condition(location)) {
                    progress++
                } else {
                    //condition for the location not satisfied
                    success = false
                }
            } else {
                //default condition: simply occupied
                if (occupiedLocations.contains(location)) {
                    progress++
                } else {
                    //area not occupied by player
                    success = false
                }
            }
        }
        if (autoCheckProgressions) {
            updateProgression(progress)
        }
        if (success) {
            success()
        }
    }

    override fun unitsMoved(units: List<MapObject>, location: Location) {
        if (units.any { it.owner === player }) {
            //unit of this player moved, check occupy condition
            check()
        }
    }

    override fun unitSpawned(`object`: MapObject, location: Location) {
        if (`object`.owner === player) {
            //unit of this player spawned, check occupy condition
            check()
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(OccupyGoal::class.java)
    }
}
