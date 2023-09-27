package com.broll.gainea.server.core.goals.impl.e2

import com.broll.gainea.server.core.bot.strategy.GoalStrategyimport

com.broll.gainea.server.core.goals.CustomOccupyGoalimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.ExpansionTypeimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.utils.LocationUtilsimport java.util.stream.Collectors
class G_Snow : CustomOccupyGoal(GoalDifficulty.EASY, "Erobere " + COUNT + " Schneegebiete") {
    init {
        setExpansionRestriction(ExpansionType.ICELANDS)
        setProgressionGoal(COUNT)
    }

    override fun check() {
        val count = LocationUtils.getControlledLocationsIn(player!!, ExpansionType.ICELANDS).stream().filter { it: Location? -> LocationUtils.isAreaType(it, AreaType.SNOW) }.count().toInt()
        updateProgression(count)
        if (count >= COUNT) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setRequiredUnits(COUNT)
        strategy.updateTargets(game.map.allAreas.stream().filter { it: Area? -> it.getType() == AreaType.SNOW }.collect(Collectors.toSet()))
    }

    companion object {
        private const val COUNT = 5
    }
}
