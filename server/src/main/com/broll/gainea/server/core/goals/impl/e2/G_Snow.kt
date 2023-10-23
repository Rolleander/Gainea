package com.broll.gainea.server.core.goals.impl.e2

import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.CustomOccupyGoal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.utils.getControlledLocationsIn
import com.broll.gainea.server.core.utils.isAreaType

class G_Snow : CustomOccupyGoal(GoalDifficulty.EASY, "Erobere " + COUNT + " Schneegebiete") {
    init {
        setExpansionRestriction(ExpansionType.ICELANDS)
        progressionGoal = COUNT
    }

    override fun check() {
        val count = player.getControlledLocationsIn(ExpansionType.ICELANDS)
                .count { it.isAreaType(AreaType.SNOW) }
        updateProgression(count)
        if (count >= COUNT) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setRequiredUnits(COUNT)
        strategy.updateTargets(game.map.allAreas.filter { it.type == AreaType.SNOW }.toSet())
    }

    companion object {
        private const val COUNT = 5
    }
}
