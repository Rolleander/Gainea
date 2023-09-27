package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.battle.BattleResultimport

com.broll.gainea.server.core.bot.strategy.GoalStrategyimport com.broll.gainea.server.core.goals.Goalimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.monster.GodDragonimport com.google.common.collect.Setsimport java.util.stream.Stream
class G_Goddrake : Goal(GoalDifficulty.EASY, "Töte den Götterdrachen im Kampf") {
    override fun battleResult(result: BattleResult) {
        Stream.concat(result.attackers.stream(), result.defenders.stream())
                .filter { it: Unit -> it is GodDragon && it.getOwner() == null }
                .forEach { godDragon: Unit? ->
                    if (result.getKillingPlayers(godDragon)!!.contains(player)) {
                        success()
                    }
                }
    }

    override fun check() {}
    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setPrepareStrategy {
            val dragonLocation = game.objects.stream().filter { it: MapObject? -> it is GodDragon }.map { obj: MapObject? -> obj.getLocation() }.findFirst().orElse(null)
            if (dragonLocation == null) {
                strategy.updateTargets(HashSet())
            } else {
                strategy.updateTargets(Sets.newHashSet(dragonLocation))
                strategy.setRequiredUnits(5)
            }
        }
    }
}
