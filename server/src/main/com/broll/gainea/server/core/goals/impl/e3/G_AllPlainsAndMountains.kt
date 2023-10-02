package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.impl.BoglandMap

class G_AllPlainsAndMountains : OccupyGoal(GoalDifficulty.HARD, "Erobere alle Steppen und Berge") {
    override fun initOccupations() {
        occupy({ it.type == AreaType.PLAINS || it.type == AreaType.MOUNTAIN }, *BoglandMap.Areas.entries.toTypedArray())
    }
}
