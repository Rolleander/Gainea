package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.impl .GaineaMap
class G_GaineaComplete : OccupyGoal(GoalDifficulty.HARD, "Erobere Gainea und das Totemgebirge/Insel") {
    override fun initOccupations() {
        occupy(GaineaMap.Continents.GAINEA)
        occupy(GaineaMap.Islands.TOTEMINSEL)
    }
}
