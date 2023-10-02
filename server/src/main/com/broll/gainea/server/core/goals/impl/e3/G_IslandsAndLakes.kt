package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.impl.BoglandMap

class G_IslandsAndLakes : OccupyGoal(GoalDifficulty.MEDIUM, "Besetze alle Inseln und Seen") {
    override fun initOccupations() {
        occupy(*BoglandMap.Islands.entries.toTypedArray())
        occupy({ it.type == AreaType.LAKE }, ExpansionType.BOGLANDS)
    }
}
