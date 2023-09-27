package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import java.util.stream.Collectors

open class G_KillMonsters @JvmOverloads constructor(difficulty: GoalDifficulty = GoalDifficulty.EASY, stars: Int = 7) : Goal(difficulty, "Erledige Monster mit insgesamt $stars Sternen") {
    private val starsTarget: Int
    private var stars = 0

    init {
        setProgressionGoal(stars)
        starsTarget = stars
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
            val locations = game.objects.stream().filter { it: MapObject? -> it is Monster }.map { obj: MapObject? -> obj.getLocation() }.collect(Collectors.toSet())
            strategy.updateTargets(locations)
            strategy.setRequiredUnits(starsTarget - stars)
        }
    }
}
