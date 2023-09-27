package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.misc.RandomUtilsimport

com.broll.gainea.server.core.GameContainerimport com.broll.gainea.server.core.bot.strategy.GoalStrategyimport com.broll.gainea.server.core.goals.CustomOccupyGoalimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Playerimport com.google.common.collect.Sets
class G_StackUnits : CustomOccupyGoal(GoalDifficulty.EASY, null) {
    private var area: Area? = null
    override fun init(game: GameContainer, player: Player?): Boolean {
        this.game = game
        this.player = player
        area = RandomUtils.pickRandom(game.map.allAreas)
        text = "Besetze " + area.getName() + " mit mindestens " + COUNT + " Einheiten"
        setExpansionRestriction(area.getContainer().expansion.type)
        setProgressionGoal(COUNT)
        locations.add(area)
        return true
    }

    override fun check() {
        val playerUnits = area.getInhabitants().stream().filter { it: MapObject -> it.owner === player && it is Unit }.count().toInt()
        updateProgression(playerUnits)
        if (playerUnits >= COUNT) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setRequiredUnits(COUNT)
        strategy.updateTargets(Sets.newHashSet<Location?>(area))
    }

    companion object {
        private const val COUNT = 6
    }
}
