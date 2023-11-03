package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.goals.GoalDifficulty.MEDIUM
import com.broll.gainea.server.core.goals.MissingExpansionException
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.utils.getDistance

class G_Bridge : OccupyGoal(difficulty = MEDIUM, text = "") {

    override fun initOccupations() {
        val start = game.map.allAreas.random()
        var current: Location = start
        val locations = mutableListOf<Location>(start)
        for (i in 1..LENGTH) {
            val last = i == LENGTH
            current =
                current.connectedLocations.filter { !locations.contains(it) && (!last || it !is Ship) }
                    .maxByOrNull {
                        start.getDistance(it) ?: -1
                    } ?: throw MissingExpansionException()
            locations += current
        }
        occupy(locations)
        text = "Erobere den markierten Pfad von ${start.name} bis ${(current as Area).name}"
    }

    companion object {
        private const val LENGTH = 8
    }
}