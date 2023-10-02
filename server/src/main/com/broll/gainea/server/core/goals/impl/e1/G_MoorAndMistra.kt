package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.impl.GaineaMap

class G_MoorAndMistra : OccupyGoal(GoalDifficulty.EASY, "Erobere den Kontinent Moor und die Mistra Insel") {
    override fun initOccupations() {
        occupy(GaineaMap.Continents.MOOR)
        occupy(GaineaMap.Islands.MISTRAINSEL)
    }
}
