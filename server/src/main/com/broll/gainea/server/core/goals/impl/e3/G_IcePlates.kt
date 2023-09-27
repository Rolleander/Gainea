package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.GameContainerimport

com.broll.gainea.server.core.bot.strategy.GoalStrategyimport com.broll.gainea.server.core.goals.CustomOccupyGoalimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.ExpansionTypeimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.LocationUtilsimport com.broll.gainea.server.core.utils.PlayerUtilsimport java.util.stream.Collectors
class G_IcePlates : CustomOccupyGoal(GoalDifficulty.EASY, "Besetze alle Eisgebiete mit jeweils " + COUNT + " Einheiten") {
    init {
        setExpansionRestriction(ExpansionType.BOGLANDS)
    }

    override fun init(game: GameContainer, player: Player?): Boolean {
        val init = super.init(game, player)
        if (init) {
            locations.addAll(targets)
        }
        return init
    }

    private val targets: List<Area?>?
        private get() = game.map.getExpansion(ExpansionType.BOGLANDS).allAreas

    override fun check() {
        val playerUnits = targets!!.stream()
                .filter { it: Area? -> LocationUtils.isAreaType(it, AreaType.SNOW) }
                .map { it: Area? -> Math.min(COUNT, PlayerUtils.getUnits(player, it).size) }.reduce(0) { a: Int, b: Int -> Integer.sum(a, b) }
        updateProgression(playerUnits)
        if (playerUnits >= COUNT) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        val locations = game.map.getExpansion(ExpansionType.BOGLANDS).allAreas.stream().filter { it: Area? -> it.getType() == AreaType.SNOW }.collect(Collectors.toSet())
        strategy.updateTargets(locations)
        strategy.setRequiredUnits(locations.size * COUNT)
    }

    companion object {
        private const val COUNT = 3
    }
}
