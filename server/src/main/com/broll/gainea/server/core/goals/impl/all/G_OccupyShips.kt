package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.CustomOccupyGoal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship

open class G_OccupyShips @JvmOverloads constructor(difficulty: GoalDifficulty = GoalDifficulty.EASY, private val ships: Int = 5) : CustomOccupyGoal(difficulty, "Erobere $ships Schiffe") {
    init {
        setProgressionGoal(ships)
    }

    override fun check() {
        val count = player.controlledLocations.stream().filter { it: Location? -> it is Ship }.count()
        updateProgression(count.toInt())
        if (count >= ships) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setRequiredUnits(ships)
        strategy.updateTargets(HashSet<Location?>(game.map.allShips))
    }
}