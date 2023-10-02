package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.impl.GaineaMap

class G_ZubaAndVulkaninsel : OccupyGoal(GoalDifficulty.MEDIUM, "Erobere den Kontinent Zuba und die Vulkaninsel mit Vulkanberg") {
    override fun initOccupations() {
        occupy(GaineaMap.Continents.ZUBA)
        occupy(GaineaMap.Islands.VULKANINSEL)
    }
}
