package com.broll.gainea.server.core.goals.impl.e3

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.CustomOccupyGoal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.ExpansionType
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.getUnits
import com.broll.gainea.server.core.utils.isAreaType

class G_IcePlates : CustomOccupyGoal(GoalDifficulty.EASY, "Besetze alle Eisgebiete mit jeweils " + COUNT + " Einheiten") {
    init {
        setExpansionRestriction(ExpansionType.BOGLANDS)
    }

    override fun init(game: Game, player: Player): Boolean {
        val init = super.init(game, player)
        if (init) {
            locations.addAll(targets)
        }
        return init
    }

    private val targets: List<Area>
        get() = game.map.getExpansion(ExpansionType.BOGLANDS)!!.allAreas

    override fun check() {
        val playerUnits = targets
                .filter { it.isAreaType(AreaType.SNOW) }
                .sumOf { Math.min(COUNT, player.getUnits(it).size) }
        updateProgression(playerUnits)
        if (playerUnits >= COUNT) {
            success()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        val locations = targets.filter { it.type == AreaType.SNOW }.toSet()
        strategy.updateTargets(locations)
        strategy.setRequiredUnits(locations.size * COUNT)
    }

    companion object {
        private const val COUNT = 3
    }
}
