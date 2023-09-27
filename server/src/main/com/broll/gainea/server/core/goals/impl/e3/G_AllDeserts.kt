package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.impl .BoglandMap
class G_AllDeserts : OccupyGoal(GoalDifficulty.EASY, "Erobere alle Wüsten") {
    override fun initOccupations() {
        occupy({ area: Area? -> area.getType() == AreaType.DESERT }, *BoglandMap.Areas.entries.toTypedArray())
    }
}
