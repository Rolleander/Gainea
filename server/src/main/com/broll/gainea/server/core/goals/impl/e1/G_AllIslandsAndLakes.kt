package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.ExpansionTypeimport com.broll.gainea.server.core.map.impl .GaineaMap
class G_AllIslandsAndLakes : OccupyGoal(GoalDifficulty.HARD, "Erobere alle Inseln und Meere") {
    override fun initOccupations() {
        occupy({ it: Area? -> it.getType() == AreaType.LAKE }, ExpansionType.GAINEA)
        occupy(*GaineaMap.Islands.entries.toTypedArray())
    }
}
