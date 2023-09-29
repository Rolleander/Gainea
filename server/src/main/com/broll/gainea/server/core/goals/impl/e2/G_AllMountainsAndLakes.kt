package com.broll.gainea.server.core.goals.impl.e2

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.utils.LocationUtils

class G_AllMountainsAndLakes : OccupyGoal(GoalDifficulty.HARD, "Erobere alle Berge und Seen") {
    override fun initOccupations() {
        occupy({ LocationUtils.isAreaType(it, AreaType.LAKE, AreaType.MOUNTAIN) }, ExpansionType.ICELANDS)
    }
}
