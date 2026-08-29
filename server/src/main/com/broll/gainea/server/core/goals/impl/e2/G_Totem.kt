package com.broll.gainea.server.core.goals.impl.e2

import com.broll.gainea.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.impl.IcelandMap

class G_Totem : OccupyGoal(GoalDifficulty.EASY, "Erobere Totem und Weißes Gebiet") {
    override fun initOccupations() {
        occupy(IcelandMap.Continents.TOTEM)
        occupy(IcelandMap.Areas.WEISSESGEBIET)
    }
}
