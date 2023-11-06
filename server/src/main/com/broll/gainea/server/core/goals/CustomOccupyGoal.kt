package com.broll.gainea.server.core.goals

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit

abstract class CustomOccupyGoal(difficulty: GoalDifficulty, text: String) : Goal(difficulty, text) {
    override fun unitsMoved(objects: List<MapObject>, location: Location) {
        if (objects.any { it.owner === player }) {
            check()
        }
    }

    override fun unitSpawned(obj: MapObject, location: Location) {
        if (obj.owner === player) {
            check()
        }
    }

    override fun unitKilled(unit: Unit, throughBattle: BattleResult?) {
        if (unit.owner == player) {
            check()
        }
    }
}
