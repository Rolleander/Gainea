package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.impl.GaineaMap

class G_GaineaAllPlains : OccupyGoal(GoalDifficulty.EASY, "Erobere alle Steppen auf Gainea") {
    override fun initOccupations() {
        occupy({ it.type == AreaType.PLAINS }, GaineaMap.Continents.GAINEA)
    }
}
