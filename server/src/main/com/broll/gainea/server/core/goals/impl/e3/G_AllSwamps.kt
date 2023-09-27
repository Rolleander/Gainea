package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.ExpansionType
class G_AllSwamps : OccupyGoal(GoalDifficulty.MEDIUM, "Erobere alle Sümpfe") {
    override fun initOccupations() {
        occupy({ it: Area? -> it.getType() == AreaType.BOG }, ExpansionType.BOGLANDS)
    }
}
