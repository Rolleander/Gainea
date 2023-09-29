package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.impl.GaineaMap

class G_AllIslandsAndLakes : OccupyGoal(GoalDifficulty.HARD, "Erobere alle Inseln und Meere") {
    override fun initOccupations() {
        occupy({ it.type == AreaType.LAKE }, ExpansionType.GAINEA)
        occupy(*GaineaMap.Islands.entries.toTypedArray())
    }
}
