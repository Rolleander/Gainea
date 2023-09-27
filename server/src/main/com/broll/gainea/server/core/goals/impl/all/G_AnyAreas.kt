package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.bot.strategy.GoalStrategyimport

com.broll.gainea.server.core.goals.CustomOccupyGoalimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.Locationimport java.util.stream.Collectors
open class G_AnyAreas @JvmOverloads constructor(difficulty: GoalDifficulty = GoalDifficulty.EASY, count: Int = 7) : CustomOccupyGoal(difficulty, "Besetze $count beliebige Gebiete") {
    private val count = 0

    init {
        this.count = count
        setProgressionGoal(count)
    }

    override fun check() {
        val current = player.controlledLocations.stream().filter { it: Location? -> it is Area }.count().toInt()
        updateProgression(current)
        if (current >= count) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.updateTargets(game.map.allAreas.stream().collect(Collectors.toSet()))
        strategy.setRequiredUnits(count)
    }
}
