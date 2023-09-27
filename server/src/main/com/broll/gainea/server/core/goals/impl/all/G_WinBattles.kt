package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResultimport

com.broll.gainea.server.core.bot.strategy.GoalStrategyimport com.broll.gainea.server.core.goals.Goalimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Playerimport java.util.stream.Collectors
open class G_WinBattles @JvmOverloads constructor(difficulty: GoalDifficulty = GoalDifficulty.MEDIUM, private val winTarget: Int = 6) : Goal(difficulty, "Gewinne $winTarget Schlachten gegen andere Spieler") {
    private var wins = 0

    init {
        setProgressionGoal(winTarget)
    }

    override fun battleResult(result: BattleResult) {
        if (result.isWinner(player) && !result.isNeutralLoser) {
            winTarget++
            check()
        }
    }

    override fun check() {
        updateProgression(winTarget)
        if (winTarget >= winTarget) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            val locations = game.allPlayers.stream().filter { it: Player? -> it !== player }
                    .flatMap { it: Player? -> it.getUnits().stream() }.map { obj: Unit? -> obj.getLocation() }.collect(Collectors.toSet())
            strategy.updateTargets(locations)
            strategy.setRequiredUnits((winTarget - winTarget) * 3)
        }
    }
}
