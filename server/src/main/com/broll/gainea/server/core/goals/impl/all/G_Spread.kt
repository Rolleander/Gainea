package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.CustomOccupyGoal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.map.Ship

class G_Spread : CustomOccupyGoal(GoalDifficulty.MEDIUM, "Kontrolliere Einheiten auf " + COUNT + " verschiedenen Landmassen") {
    init {
        setProgressionGoal(COUNT)
    }

    override fun check() {
        val containers = player.controlledLocations.filter { it !is Ship }.map { it.container }.distinct().count()
        updateProgression(containers)
        if (containers >= COUNT) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        val areas = game.map.allContainers.map { it.areas.random() }
        strategy.setRequiredUnits(COUNT)
        strategy.updateTargets(areas.toSet())
    }

    companion object {
        private const val COUNT = 6
    }
}
