package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.CustomOccupyGoal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.map.Area

open class G_AnyAreas(difficulty: GoalDifficulty = GoalDifficulty.EASY, val count: Int = 7) : CustomOccupyGoal(difficulty, "Besetze $count beliebige Gebiete") {

    init {
        setProgressionGoal(count)
    }

    override fun check() {
        val current = player.controlledLocations.filterIsInstance<Area>().count()
        updateProgression(current)
        if (current >= count) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.updateTargets(game.map.allAreas.toSet())
        strategy.setRequiredUnits(count)
    }
}
