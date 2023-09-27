package com.broll.gainea.server.core.goals.impl.e2

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.impl .IcelandMap
class G_AllIslands : OccupyGoal(GoalDifficulty.MEDIUM, "Erobere alle Inseln") {
    override fun initOccupations() {
        occupy(*IcelandMap.Islands.entries.toTypedArray())
    }
}
