package com.broll.gainea.server.core.goals.impl.e2

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.ExpansionType
class G_AllMountains : OccupyGoal(GoalDifficulty.EASY, "Erobere alle Berge") {
    override fun initOccupations() {
        occupy({ it: Area? -> it.getType() == AreaType.MOUNTAIN }, ExpansionType.ICELANDS)
    }
}
