package com.broll.gainea.server.core.goals

import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject

abstract class CustomOccupyGoal(difficulty: GoalDifficulty, text: String) : Goal(difficulty, text) {
    override fun moved(units: List<MapObject>, location: Location) {
        if (units.any { it.owner === player }) {
            check()
        }
    }

    override fun spawned(`object`: MapObject, location: Location) {
        if (`object`.owner === player) {
            check()
        }
    }
}
