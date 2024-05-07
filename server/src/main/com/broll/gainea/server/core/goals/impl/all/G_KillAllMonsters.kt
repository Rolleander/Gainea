package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty.EASY
import com.broll.gainea.server.core.goals.GoalDifficulty.MEDIUM
import com.broll.gainea.server.core.map.Continent
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.getMonsters

class G_KillAllMonsters : Goal(MEDIUM, "") {

    init {
        libraryText = text("X")
        libraryDifficulties += EASY
        libraryDifficulties += MEDIUM
    }

    private lateinit var continent: Continent
    override fun init(game: Game, player: Player): Boolean {
        for (continent in game.map.allContinents.shuffled()) {
            this.continent = continent
            val monsterCount = monsterCount
            if (monsterCount >= 2) {
                difficulty = if (monsterCount >= 5) MEDIUM else EASY
                setExpansionRestriction(continent.expansion.type)
                text = text(continent.name)
                return super.init(game, player)
            }
        }
        return false
    }

    private fun text(continent: String) =
        "Auf dem Kontinet $continent darf es keine wilden Monster mehr geben"

    private val monsterCount: Int
        get() = continent.areas.flatMap { it.getMonsters() }
            .count { it.owner.isNeutral() && it.alive }

    override fun check() {
        if (monsterCount == 0) {
            success()
        }
    }

    override fun unitKilled(unit: Unit, throughBattle: BattleResult?) {
        if (unit is Monster && unit.owner.isNeutral()) {
            check()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            val monsters = continent.areas.flatMap { it.getMonsters() }
            val stars = monsters.sumOf { it.stars }
            strategy.updateTargets(monsters.map { it.location }.toSet())
            strategy.setRequiredUnits(stars)
        }
    }
}
