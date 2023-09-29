package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.impl.BoglandMap
import com.broll.gainea.server.core.utils.ShipUtils

class G_IslandShips : OccupyGoal(GoalDifficulty.EASY, "Besetze alle Schiffe die um die obere und untere Insel führen") {
    override fun initOccupations() {
        val expansion = game.map.getExpansion(ExpansionType.BOGLANDS) ?: return
        val kleinspalt = expansion.getIsland(BoglandMap.Islands.KLEINSPALT)!!
        occupy(ShipUtils.getAllShips(kleinspalt))
    }
}
