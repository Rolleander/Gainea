package com.broll.gainea.server.core.events

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.AreaType.LAKE
import com.broll.gainea.server.core.map.Continent
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.utils.getRandomFree

abstract class RandomEvent {

    lateinit var game: Game
    lateinit var location: Location

    fun init(game: Game): Boolean {
        this.game = game
        val spot = pickSpot()
        if (spot != null) {
            this.location = spot
        }
        return spot != null
    }

    abstract fun pickSpot(): Location?

    abstract fun run()

}

fun Game.freeArea() = map.allAreas.getRandomFree()

fun Game.freeContinentArea() =
    map.allContinents.flatMap { it.areas }.filter { it.free }.randomOrNull()

fun Game.freeBuildingSpot(onlyContinents: Boolean = false) =
    map.allAreas.filter { it.free && it.type != LAKE && (it.container is Continent || !onlyContinents) }
        .randomOrNull()