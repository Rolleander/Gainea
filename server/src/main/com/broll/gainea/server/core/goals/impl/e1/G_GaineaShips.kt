package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficultyimport

com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.ExpansionTypeimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.map.impl .GaineaMapimport com.broll.gainea.server.core.utils.ShipUtils
class G_GaineaShips : OccupyGoal(GoalDifficulty.MEDIUM, "Besetze alle Schiffe die von Moor, Zuba und der Vulkaninsel nach Gainea führen") {
    override fun initOccupations() {
        val expansion = game.map.getExpansion(ExpansionType.GAINEA)
        if (expansion != null) {
            val gainea = expansion.getContinent(GaineaMap.Continents.GAINEA)
            val moor = expansion.getContinent(GaineaMap.Continents.MOOR)
            val zuba = expansion.getContinent(GaineaMap.Continents.ZUBA)
            val vulkanInsel = expansion.getIsland(GaineaMap.Islands.VULKANINSEL)
            val ships: MutableList<Location?> = ArrayList()
            ships.addAll(ShipUtils.getAllShips(zuba, gainea))
            ships.addAll(ShipUtils.getAllShips(moor, gainea))
            ships.addAll(ShipUtils.getAllShips(vulkanInsel, gainea))
            occupy(ships)
        }
    }
}
