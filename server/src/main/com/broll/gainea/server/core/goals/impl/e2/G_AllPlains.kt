package com.broll.gainea.server.core.goals.impl.e2

import com.broll.gainea.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.core.map.ExpansionType

class G_AllPlains : OccupyGoal(GoalDifficulty.EASY, "Erobere alle Steppen") {
    override fun initOccupations() {
        occupy({ it.type == AreaType.PLAINS }, ExpansionType.ICELANDS)
    }
}
