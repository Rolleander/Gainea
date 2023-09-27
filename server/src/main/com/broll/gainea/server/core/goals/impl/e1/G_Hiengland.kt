package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.impl .GaineaMap
class G_Hiengland : OccupyGoal(GoalDifficulty.EASY, "Erobere Hiengland und alle angrenzenden Areale") {
    override fun initOccupations() {
        occupy(GaineaMap.Areas.HIENGLAND, GaineaMap.Areas.WEIDESTEPPE, GaineaMap.Areas.DUNKLESMEER, GaineaMap.Areas.KIESSTRAND, GaineaMap.Areas.ZWINGSEE, GaineaMap.Areas.KORBERG)
    }
}
