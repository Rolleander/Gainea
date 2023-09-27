package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.Continent
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.LocationUtils
import java.util.Collections
import java.util.stream.Collectors

class G_KillAllMonsters : Goal(GoalDifficulty.MEDIUM, "") {
    private var continent: Continent? = null
    override fun init(game: GameContainer, player: Player?): Boolean {
        val continets = game.map.allContinents
        Collections.shuffle(continets)
        for (continent in continets!!) {
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
        private get() = continent.getAreas().stream().flatMap { it: Area? -> LocationUtils.getMonsters(it).stream() }
                .filter { it: Monster? -> it.getOwner() == null }.filter { obj: Monster? -> obj!!.isAlive }.count().toInt()

    override fun check() {
        if (monsterCount == 0) {
            success()
        }
    }

    override fun killed(unit: Unit?, throughBattle: BattleResult?) {
        if (unit is Monster && unit.getOwner() == null) {
            check()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            val monsters = continent.getAreas().stream().flatMap { it: Area? -> LocationUtils.getMonsters(it).stream() }.collect(Collectors.toList())
            val stars = monsters.stream().mapToInt { it: Monster? -> it.getStars() }.sum()
            strategy.updateTargets(monsters.stream().map { obj: Monster? -> obj.getLocation() }.collect(Collectors.toSet()))
            strategy.setRequiredUnits(stars)
        }
    }
}
