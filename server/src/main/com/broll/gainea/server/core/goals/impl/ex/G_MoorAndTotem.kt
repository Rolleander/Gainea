package com.broll.gainea.server.core.goals.impl.ex

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.impl.GaineaMap
import com.broll.gainea.server.core.map.impl.IcelandMap

class G_MoorAndTotem : OccupyGoal(GoalDifficulty.MEDIUM, "Erobere die Kontinente Moor und Totem") {
    override fun initOccupations() {
        occupy(IcelandMap.Continents.TOTEM)
        occupy(GaineaMap.Continents.MOOR)
    }
}
