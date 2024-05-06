package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.MissingExpansionException
import com.broll.gainea.server.core.goals.OccupyGoal

class G_Spread2 : OccupyGoal(GoalDifficulty.HARD, "") {

    init {
        libraryText = "Erobere $LOCATIONS zufällig bestimmte Felder"
    }


    override fun initOccupations() {
        val continents = game.map.allContinents.shuffled()
        val locations = continents.take(LOCATIONS).map { it.areas.random() }
        if (locations.size < LOCATIONS) {
            throw MissingExpansionException()
        }
        occupy(locations)
        text = "Erobere " + locations.joinToString(", ") { it.name }
    }

    companion object {
        private const val LOCATIONS = 5
    }
}
