package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.impl.GaineaMap

class G_AllDeserts : OccupyGoal(GoalDifficulty.MEDIUM, "Erobere alle Wüsten") {
    override fun initOccupations() {
        occupy({ it.type == AreaType.DESERT }, *GaineaMap.Areas.entries.toTypedArray())
    }
}
