package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.bot.BotUtils
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.RoundGoal
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaCollection
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.player.Player
import java.util.stream.Collectors

class G_OccupyRounds : RoundGoal(GoalDifficulty.MEDIUM, "", ROUND_TARGET) {
    private var container: AreaCollection? = null
    override fun init(game: GameContainer, player: Player?): Boolean {
        container = RandomUtils.pickRandom(game.map.allAreas).container
        text = "Sei für " + ROUND_TARGET + " Runden der einzige Spieler mit Einheiten auf " + container.getName()
        val containerSize = container.getAreas().size
        if (containerSize <= 5) {
            difficulty = GoalDifficulty.EASY
        } else {
            difficulty = GoalDifficulty.MEDIUM
        }
        return super.init(game, player)
    }

    override fun moved(units: List<MapObject?>?, location: Location?) {
        if (location.getContainer() === container && location !is Ship &&
                units!!.stream().anyMatch { `object`: MapObject? -> `object`.getOwner() != null && `object`.getOwner() !== player }) {
            resetRounds()
        }
    }

    override fun spawned(`object`: MapObject, location: Location) {
        if (location.container === container && location !is Ship && `object`.owner != null && `object`.owner !== player) {
            resetRounds()
        }
    }

    override fun check() {
        val owners = container.getAreas().stream().flatMap { it: Area? -> it.getInhabitants().stream().map { `object`: MapObject -> `object`.owner } }.filter { it: Player? -> it != null }.distinct().collect(Collectors.toList())
        if (owners.size == 1 && owners[0] === player) {
            progressRound()
        } else {
            resetRounds()
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setPrepareStrategy {
            strategy.updateTargets(BotUtils.huntOtherPlayersTargets(player!!, game!!))
            strategy.setRequiredUnits(3)
        }
    }

    companion object {
        private const val ROUND_TARGET = 5
    }
}
