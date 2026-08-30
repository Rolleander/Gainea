package com.broll.gainea.server.core.goals.impl.e2

import com.broll.gainea.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.core.map.ExpansionType
import com.broll.gainea.server.core.map.AreaType

class G_AllMountains : OccupyGoal(GoalDifficulty.EASY, "Erobere alle Berge") {
    override fun initOccupations() {
        occupy({ it.type == AreaType.MOUNTAIN }, ExpansionType.ICELANDS)
    }
}
