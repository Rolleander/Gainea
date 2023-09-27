package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.impl .GaineaMap
class G_AllIslands : OccupyGoal(GoalDifficulty.MEDIUM, "Erobere alle Inseln") {
    override fun initOccupations() {
        occupy(*GaineaMap.Islands.entries.toTypedArray())
    }
}
