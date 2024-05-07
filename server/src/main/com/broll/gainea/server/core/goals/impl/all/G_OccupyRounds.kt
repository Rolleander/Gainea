package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.bot.BotUtils
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.GoalDifficulty.EASY
import com.broll.gainea.server.core.goals.GoalDifficulty.MEDIUM
import com.broll.gainea.server.core.goals.RoundGoal
import com.broll.gainea.server.core.map.AreaCollection
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral

class G_OccupyRounds : RoundGoal(MEDIUM, "", ROUND_TARGET) {
    init {
        libraryText = text("X")
        libraryDifficulties += EASY
        libraryDifficulties += MEDIUM
    }

    private lateinit var container: AreaCollection

    override fun init(game: Game, player: Player): Boolean {
        container = game.map.allContainers.random()
        locations += container.areas
        text = text(container.name)
        val containerSize = container.areas.size
        difficulty = if (containerSize <= 5) {
            EASY
        } else {
            MEDIUM
        }
        return super.init(game, player)
    }

    private fun text(container: String) =
        "Sei für $ROUND_TARGET Runden der einzige Spieler mit Einheiten auf $container"

    override fun unitsMoved(units: List<MapObject>, location: Location) {
        if (location.container === container && location !is Ship &&
            units.any { !it.owner.isNeutral() && it.owner !== player }
        ) {
            resetRounds()
        }
    }

    override fun unitSpawned(it: MapObject, location: Location) {
        if (location.container === container && location !is Ship && !it.owner.isNeutral() && it.owner !== player) {
            resetRounds()
        }
    }

    override fun check() {
        val owners = container.areas.flatMap { area -> area.inhabitants.map { it.owner } }
            .filter { !it.isNeutral() }.distinct()
        if (owners.size == 1 && owners[0] === player) {
            progressRound()
        } else {
            resetRounds()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setPrepareStrategy {
            strategy.updateTargets(BotUtils.huntOtherPlayersTargets(player, game))
            strategy.setRequiredUnits(3)
        }
    }

    companion object {
        private const val ROUND_TARGET = 5
    }
}
