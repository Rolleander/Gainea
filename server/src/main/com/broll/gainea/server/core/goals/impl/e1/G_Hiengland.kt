package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.DUNKLESMEER
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.HIENGLAND
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.KIESSTRAND
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.KORBERG
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.WEIDESTEPPE
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.ZWINGSEE

class G_Hiengland : OccupyGoal(GoalDifficulty.EASY, "Erobere Hiengland und alle angrenzenden Areale") {
    override fun initOccupations() {
        occupy(HIENGLAND, WEIDESTEPPE, DUNKLESMEER, KIESSTRAND, ZWINGSEE, KORBERG)
    }
}
