package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.CustomOccupyGoal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.utils.filterByType
import com.broll.gainea.server.core.utils.getControlledLocationsIn

class G_AreaTypes : CustomOccupyGoal(GoalDifficulty.MEDIUM, "Erobere zwei beliebige Steppen, Wüsten, Meere und Berge") {
    init {
        setExpansionRestriction(ExpansionType.GAINEA)
        progressionGoal = 8
    }

    override fun check() {
        var count = 0
        val locations = player.getControlledLocationsIn(ExpansionType.GAINEA)
        for (type in TYPES) {
            count += Math.min(2, locations.filterByType(type).count())
        }
        updateProgression(count)
        if (count == TYPES.size * 2) {
            success()
        }
    }

    private fun getUnoccupiedOfType(type: AreaType): Set<Location> {
        val areas = game.map.getExpansion(ExpansionType.GAINEA)!!.allAreas.filter { it.type == type }
        val controlled = areas.filter { area -> area.inhabitants.any { it.owner === player } }
        return if (controlled.size >= 2) {
            setOf()
        } else areas.subtract(controlled)
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setPrepareStrategy {
            val targets = TYPES.flatMap { getUnoccupiedOfType(it) }.toSet()
            strategy.updateTargets(targets)
        }
    }

    companion object {
        private val TYPES = listOf(AreaType.PLAINS, AreaType.DESERT, AreaType.LAKE, AreaType.MOUNTAIN)
    }
}
