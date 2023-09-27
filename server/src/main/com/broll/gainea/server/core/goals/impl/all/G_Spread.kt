package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.misc.RandomUtilsimport

com.broll.gainea.server.core.bot.strategy.GoalStrategyimport com.broll.gainea.server.core.goals.CustomOccupyGoalimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.map.AreaCollectionimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.map.Shipimport java.util.stream.Collectors
class G_Spread : CustomOccupyGoal(GoalDifficulty.MEDIUM, "Kontrolliere Einheiten auf " + COUNT + " verschiedenen Landmassen") {
    init {
        setProgressionGoal(COUNT)
    }

    override fun check() {
        val containers = player.controlledLocations.stream().filter { it: Location? -> it !is Ship }.map { obj: Location? -> obj.getContainer() }.distinct().count().toInt()
        updateProgression(containers)
        if (containers >= COUNT) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        val areas = game.map.allContainers.stream().map { it: AreaCollection? -> RandomUtils.pickRandom(it.getAreas()) }.collect(Collectors.toSet())
        strategy.setRequiredUnits(COUNT)
        strategy.updateTargets(areas)
    }

    companion object {
        private const val COUNT = 6
    }
}
