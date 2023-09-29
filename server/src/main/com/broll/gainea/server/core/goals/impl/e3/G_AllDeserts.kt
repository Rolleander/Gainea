package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.impl.BoglandMap

class G_AllDeserts : OccupyGoal(GoalDifficulty.EASY, "Erobere alle Wüsten") {
    override fun initOccupations() {
        occupy({ it.type == AreaType.DESERT }, *BoglandMap.Areas.entries.toTypedArray())
    }
}
