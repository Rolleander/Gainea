package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.PlayerUtils
import java.util.stream.Collectors

class G_KillCommander : Goal(GoalDifficulty.EASY, "Besiege den Feldherr eines anderen Spielers im Kampf") {
    override fun killed(unit: Unit?, throughBattle: BattleResult?) {
        if (throughBattle != null) {
            if (throughBattle.isAttacker(player) && throughBattle.defenders.stream().anyMatch { it: Unit -> PlayerUtils.isCommander(it) && it.isDead }) {
                success()
            } else if (throughBattle.isDefender(player) && throughBattle.attackers.stream().anyMatch { it: Unit -> PlayerUtils.isCommander(it) && it.isDead }) {
                success()
            }
        }
    }

    override fun check() {}
    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            val locations = game.allPlayers.stream().filter { it: Player? -> it !== player }.flatMap { it: Player? -> it.getUnits().stream() }
                    .filter { obj: Unit? -> PlayerUtils.isCommander() }.map { obj: Unit? -> obj.getLocation() }.collect(Collectors.toSet())
            strategy.updateTargets(locations)
            strategy.setRequiredUnits(5)
        }
    }
}
