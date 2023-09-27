package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.misc.RandomUtilsimport

com.broll.gainea.server.core.GameContainerimport com.broll.gainea.server.core.battle.BattleResultimport com.broll.gainea.server.core.bot.BotUtilsimport com.broll.gainea.server.core.bot.strategy.GoalStrategyimport com.broll.gainea.server.core.goals.Goalimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Playerimport java.util.stream.Collectors
class G_KillPlayer : Goal(GoalDifficulty.MEDIUM, "") {
    private var target: Player? = null
    override fun init(game: GameContainer, player: Player?): Boolean {
        target = RandomUtils.pickRandom(game.activePlayers.stream().filter { it: Player? -> it !== player && !it.getUnits().isEmpty() }.collect(Collectors.toList()))
        if (target == null) {
            return false
        }
        text = target.getServerPlayer().name + " darf keine Einheiten mehr besitzen"
        difficulty = if (target.getUnits().size >= 8) GoalDifficulty.HARD else GoalDifficulty.MEDIUM
        return super.init(game, player)
    }

    override fun killed(unit: Unit?, throughBattle: BattleResult?) {
        if (unit.getOwner() === target) {
            check()
        }
    }

    override fun check() {
        if (target.getUnits().isEmpty()) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.isSpreadUnits = false
        strategy.setPrepareStrategy {
            strategy.updateTargets(BotUtils.huntPlayerTargets(target))
            strategy.setRequiredUnits(target.getUnits().size + 1)
        }
    }
}
