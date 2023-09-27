package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.ExpansionTypeimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.map.impl .BoglandMapimport com.broll.gainea.server.core.utils.ShipUtils
class G_IslandShips : OccupyGoal(GoalDifficulty.EASY, "Besetze alle Schiffe die um die obere und untere Insel führen") {
    override fun initOccupations() {
        val expansion = game.map.getExpansion(ExpansionType.BOGLANDS)
        if (expansion != null) {
            val kleinspalt = expansion.getIsland(BoglandMap.Islands.KLEINSPALT)
            val ships: MutableList<Location?> = ArrayList()
            ships.addAll(ShipUtils.getAllShips(kleinspalt))
            occupy(ships)
        }
    }
}
