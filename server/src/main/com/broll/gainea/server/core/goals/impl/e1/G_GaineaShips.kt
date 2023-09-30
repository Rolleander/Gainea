package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.impl.GaineaMap
import com.broll.gainea.server.core.utils.getAllShips

class G_GaineaShips : OccupyGoal(GoalDifficulty.MEDIUM, "Besetze alle Schiffe die von Moor, Zuba und der Vulkaninsel nach Gainea führen") {
    override fun initOccupations() {
        val expansion = game.map.getExpansion(ExpansionType.GAINEA)
        if (expansion != null) {
            val gainea = expansion.getContinent(GaineaMap.Continents.GAINEA)!!
            val moor = expansion.getContinent(GaineaMap.Continents.MOOR)!!
            val zuba = expansion.getContinent(GaineaMap.Continents.ZUBA)!!
            val vulkanInsel = expansion.getIsland(GaineaMap.Islands.VULKANINSEL)!!
            occupy(listOf(zuba.getAllShips(gainea),
                    moor.getAllShips(gainea),
                    vulkanInsel.getAllShips(gainea)).flatten())
        }
    }
}
