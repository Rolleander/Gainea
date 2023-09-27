package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.misc.RandomUtilsimport

com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.goals.MissingExpansionExceptionimport com.broll.gainea.server.core.goals.OccupyGoalimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.Continentimport java.util.Collectionsimport java.util.stream.Collectors
class G_Spread2 : OccupyGoal(GoalDifficulty.HARD, null) {
    override fun initOccupations() {
        val continents = game.map.allContinents
        Collections.shuffle(continents)
        val locations = continents!!.stream().limit(LOCATIONS.toLong()).map { it: Continent? -> RandomUtils.pickRandom(it.getAreas()) }.collect(Collectors.toList())
        if (locations.size < LOCATIONS) {
            throw MissingExpansionException()
        }
        occupy(locations.stream().map { it: Area? -> it }.collect(Collectors.toList()))
        text = "Erobere " + locations.stream().map { obj: Area? -> obj.getName() }.collect(Collectors.joining(","))
    }

    companion object {
        private const val LOCATIONS = 5
    }
}
