package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.impl.GaineaMap

class G_GaineaComplete : OccupyGoal(GoalDifficulty.HARD, "Erobere Gainea und das Totemgebirge/Insel") {
    override fun initOccupations() {
        occupy(GaineaMap.Continents.GAINEA)
        occupy(GaineaMap.Islands.TOTEMINSEL)
    }
}
