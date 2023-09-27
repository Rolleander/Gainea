package com.broll.gainea.server.core.goals.impl.e2

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.ExpansionTypeimport com.broll.gainea.server.core.utils.LocationUtils
class G_AllMountainsAndLakes : OccupyGoal(GoalDifficulty.HARD, "Erobere alle Berge und Seen") {
    override fun initOccupations() {
        occupy({ it: Area? -> LocationUtils.isAreaType(it, AreaType.LAKE, AreaType.MOUNTAIN) }, ExpansionType.ICELANDS)
    }
}
