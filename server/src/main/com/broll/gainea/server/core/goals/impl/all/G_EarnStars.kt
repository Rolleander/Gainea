package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player

open class G_EarnStars(
    difficulty: GoalDifficulty = GoalDifficulty.EASY,
    private val starsTarget: Int = 7
) : Goal(difficulty, "Erhalte $starsTarget Sterne") {
    private var stars = 0

    init {
        progressionGoal = starsTarget
    }

    override fun earnedStars(player: Player, stars: Int) {
        if (player === this.player) {
            this.stars += stars
            check()
        }
    }

    override fun check() {
        updateProgression(stars)
        if (stars >= starsTarget) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            val locations = game.objects.filterIsInstance<Monster>().map { it.location }.toSet()
            strategy.updateTargets(locations)
            strategy.setRequiredUnits(starsTarget - stars)
        }
    }
}
