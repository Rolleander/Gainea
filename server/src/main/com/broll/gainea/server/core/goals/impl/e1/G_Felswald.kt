package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.impl .GaineaMap
class G_Felswald : OccupyGoal(GoalDifficulty.EASY, "Erobere Felswald und Grünland, sowie alle deren benachbarten Länder") {
    override fun initOccupations() {
        occupy(GaineaMap.Areas.FELSWALD, GaineaMap.Areas.FELSENWUESTE, GaineaMap.Areas.KUESTENGEBIET, GaineaMap.Areas.GRUENLAND, GaineaMap.Areas.XOMDELTA, GaineaMap.Areas.WEIDESTEPPE)
    }
}
