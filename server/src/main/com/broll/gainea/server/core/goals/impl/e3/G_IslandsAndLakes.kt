package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.ExpansionTypeimport com.broll.gainea.server.core.map.impl .BoglandMap
class G_IslandsAndLakes : OccupyGoal(GoalDifficulty.MEDIUM, "Besetze alle Inseln und Seen") {
    override fun initOccupations() {
        occupy(*BoglandMap.Islands.entries.toTypedArray())
        occupy({ it: Area? -> it.getType() == AreaType.LAKE }, ExpansionType.BOGLANDS)
    }
}
