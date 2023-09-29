package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.map.Continent
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.LocationUtils

class G_KillAllMonsters : Goal(GoalDifficulty.MEDIUM, "") {
    private lateinit var continent: Continent
    override fun init(game: GameContainer, player: Player): Boolean {
        for (continent in game.map.allContinents.shuffled()) {
            this.continent = continent
            val monsterCount = monsterCount
            if (monsterCount >= 2) {
                difficulty = if (monsterCount >= 5) GoalDifficulty.MEDIUM else GoalDifficulty.EASY
                setExpansionRestriction(continent.expansion.type)
                text = "Auf dem Kontinent " + continent.name + " darf es keine wilden Monster mehr geben"
                return super.init(game, player)
            }
        }
        return false
    }

    private val monsterCount: Int
        get() = continent.areas.flatMap { LocationUtils.getMonsters(it) }.count { it.owner.isNeutral() && it.alive }

    override fun check() {
        if (monsterCount == 0) {
            success()
        }
    }

    override fun killed(unit: Unit, throughBattle: BattleResult?) {
        if (unit is Monster && unit.owner.isNeutral()) {
            check()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            val monsters = continent.areas.flatMap { LocationUtils.getMonsters(it) }
            val stars = monsters.sumOf { it.stars }
            strategy.updateTargets(monsters.map { it.location }.toSet())
            strategy.setRequiredUnits(stars)
        }
    }
}
