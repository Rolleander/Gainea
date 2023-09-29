package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.ExpansionType

class G_AllMountains : OccupyGoal(GoalDifficulty.EASY, "Erobere alle Berge") {
    override fun initOccupations() {
        occupy({ it.type == AreaType.MOUNTAIN }, ExpansionType.GAINEA)
    }
}
