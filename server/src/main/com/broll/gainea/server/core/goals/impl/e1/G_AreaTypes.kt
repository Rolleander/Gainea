package com.broll.gainea.server.core.goals.impl.e1

import com.broll.gainea.server.core.bot.strategy.GoalStrategyimport

com.broll.gainea.server.core.goals.CustomOccupyGoalimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.ExpansionTypeimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.utils.LocationUtilsimport org.apache.commons.collections4.ListUtilsimport java.util.Arraysimport java.util.stream.Collectorsimport java.util.stream.Stream
class G_AreaTypes : CustomOccupyGoal(GoalDifficulty.MEDIUM, "Erobere zwei beliebige Steppen, Wüsten, Meere und Berge") {
    init {
        setExpansionRestriction(ExpansionType.GAINEA)
        setProgressionGoal(8)
    }

    override fun check() {
        var count = 0
        val locations = LocationUtils.getControlledLocationsIn(player!!, ExpansionType.GAINEA)
        for (type in TYPES) {
            count += Math.min(2, LocationUtils.filterByType(locations, type).count()).toInt()
        }
        updateProgression(count)
        if (count == TYPES.size * 2) {
            success()
        }
    }

    private fun getUnoccupiedOfType(type: AreaType): Stream<Location?> {
        val areas = game.map.getExpansion(ExpansionType.GAINEA).allAreas.stream()
                .filter { it: Area? -> it.getType() == type }.collect(Collectors.toList())
        val controlled = areas.stream().filter { it: Location? -> it.getInhabitants().stream().anyMatch { unit: MapObject? -> unit.getOwner() === player } }.collect(Collectors.toList())
        return if (controlled.size >= 2) {
            Stream.of()
        } else ListUtils.subtract(areas, controlled).stream()
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setPrepareStrategy {
            val targets = Arrays.stream(TYPES).flatMap { type: AreaType -> getUnoccupiedOfType(type) }.collect(Collectors.toSet())
            strategy.updateTargets(targets)
        }
    }

    companion object {
        private val TYPES = arrayOf(AreaType.PLAINS, AreaType.DESERT, AreaType.LAKE, AreaType.MOUNTAIN)
    }
}
