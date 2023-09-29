package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.FELSENWUESTE
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.FELSWALD
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.GRUENLAND
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.KUESTENGEBIET
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.WEIDESTEPPE
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.XOMDELTA

class G_Felswald : OccupyGoal(GoalDifficulty.EASY, "Erobere Felswald und Grünland, sowie alle deren benachbarten Länder") {
    override fun initOccupations() {
        occupy(FELSWALD, FELSENWUESTE, KUESTENGEBIET, GRUENLAND, XOMDELTA, WEIDESTEPPE)
    }
}
