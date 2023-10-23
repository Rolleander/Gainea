package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.OccupyGoal
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.objects.Unit

class G_StackUnits : OccupyGoal(GoalDifficulty.EASY, "") {
    private lateinit var area: Area

    init {
        autoUpdateProgressions = false
        progressionGoal = COUNT
    }

    override fun initOccupations() {
        area = game.map.allAreas.random()
        text = "Besetze " + area.name + " mit mindestens " + COUNT + " Einheiten"

        condition(occupy(area), {
            it.inhabitants.count { it.owner == player && it is Unit } >= COUNT
        })
    }

    override fun check() {
        val playerUnits = area.inhabitants.count { it.owner === player && it is Unit }
        updateProgression(playerUnits)
        super.check()
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setRequiredUnits(COUNT)
        strategy.updateTargets(setOf(area))
    }


    companion object {
        private const val COUNT = 6
    }
}
