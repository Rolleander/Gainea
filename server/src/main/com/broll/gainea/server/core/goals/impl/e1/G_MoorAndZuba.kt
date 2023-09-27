package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.impl .GaineaMap
class G_MoorAndZuba : OccupyGoal(GoalDifficulty.HARD, "Erobere den Kontinent Moor und Zuba") {
    override fun initOccupations() {
        occupy(GaineaMap.Continents.MOOR)
        occupy(GaineaMap.Continents.ZUBA)
    }
}
