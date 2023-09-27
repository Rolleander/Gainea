package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.impl .GaineaMap
class G_GaineaAllPlains : OccupyGoal(GoalDifficulty.EASY, "Erobere alle Steppen auf Gainea") {
    override fun initOccupations() {
        occupy({ it: Area? -> it.getType() == AreaType.PLAINS }, GaineaMap.Continents.GAINEA)
    }
}
