package com.broll.gainea.server.core.goals.impl.e2

import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.CustomOccupyGoal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.utils.LocationUtils

class G_Snow : CustomOccupyGoal(GoalDifficulty.EASY, "Erobere " + COUNT + " Schneegebiete") {
    init {
        setExpansionRestriction(ExpansionType.ICELANDS)
        setProgressionGoal(COUNT)
    }

    override fun check() {
        val count = LocationUtils.getControlledLocationsIn(player, ExpansionType.ICELANDS)
                .count { LocationUtils.isAreaType(it, AreaType.SNOW) }
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
