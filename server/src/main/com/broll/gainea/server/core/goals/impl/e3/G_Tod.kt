package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.impl.BoglandMap

class G_Tod : OccupyGoal(GoalDifficulty.MEDIUM, "Erobere den Kontinent Tod") {
    override fun initOccupations() {
        occupy(BoglandMap.Continents.TOD)
    }
}
