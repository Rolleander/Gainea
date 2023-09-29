package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.ExpansionType

class G_AllSwamps : OccupyGoal(GoalDifficulty.MEDIUM, "Erobere alle Sümpfe") {
    override fun initOccupations() {
        occupy({ it.type == AreaType.BOG }, ExpansionType.BOGLANDS)
    }
}
