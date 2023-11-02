package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.bot.BotUtils
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player

class G_KillPlayer : Goal(GoalDifficulty.MEDIUM, "") {
    private lateinit var target: Player
    override fun init(game: Game, player: Player): Boolean {
        target = game.activePlayers.filter { it !== player && it.units.isNotEmpty() }.randomOrNull()
            ?: return false
        text = target.serverPlayer.name + " darf keine Einheiten mehr besitzen"
        difficulty = if (target.units.size >= 8) GoalDifficulty.HARD else GoalDifficulty.MEDIUM
        return super.init(game, player)
    }

    override fun unitKilled(unit: Unit, throughBattle: BattleResult?) {
        if (unit.owner === target) {
            check()
        }
    }

    override fun check() {
        if (target.units.isEmpty()) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            strategy.updateTargets(BotUtils.huntPlayerTargets(target))
            strategy.setRequiredUnits(target.units.size + 1)
        }
    }
}
