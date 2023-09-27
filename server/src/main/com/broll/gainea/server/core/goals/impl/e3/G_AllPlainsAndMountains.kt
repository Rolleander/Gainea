package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.impl .BoglandMap
class G_AllPlainsAndMountains : OccupyGoal(GoalDifficulty.HARD, "Erobere alle Steppen und Berge") {
    override fun initOccupations() {
        occupy({ area: Area? -> area.getType() == AreaType.PLAINS || area.getType() == AreaType.MOUNTAIN }, *BoglandMap.Areas.entries.toTypedArray())
    }
}
