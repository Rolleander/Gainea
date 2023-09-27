package com.broll.gainea.server.core.stats

import com.broll.gainea.net.NT_GameStatistic
import com.broll.gainea.net.NT_RoundStatistic
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter
import com.broll.gainea.server.core.utils.GameUtils
import java.util.function.Consumer

class GameStatistic(private val game: GameContainer) : GameUpdateReceiverAdapter() {
    private val turnStatistics: MutableList<TurnStatistic> = ArrayList()
    override fun roundStarted() {
        registerRound()
    }

    fun registerRound() {
        turnStatistics.add(calcTurnStatistic())
    }

    fun sendStatistic() {
        calcTurnStatistic()
        val nt = NT_GameStatistic()
        nt.rounds = turnStatistics.stream().map<NT_RoundStatistic?> { obj: TurnStatistic -> obj.get() }.toArray<NT_RoundStatistic> { _Dummy_.__Array__() }
        GameUtils.sendUpdate(game, nt)
    }

    private fun calcTurnStatistic(): TurnStatistic {
        val statistic = TurnStatistic()
        statistic.players = game.allPlayers.stream().map<PlayerStatistic> { player: Player? -> calcPlayerStatistic(player) }.toArray<PlayerStatistic?> { _Dummy_.__Array__() }
        return statistic
    }

    private fun calcPlayerStatistic(player: Player?): PlayerStatistic {
        val statistic = PlayerStatistic()
        statistic.controlledLocations = player.getControlledLocations().size.toShort()
        statistic.cards = player.getCardHandler().cards.size.toShort()
        statistic.stars = player.getGoalHandler().stars.toShort()
        statistic.points = player.getGoalHandler().score.toShort()
        player.getUnits().forEach(Consumer { unit: Unit? ->
            statistic.units++
            statistic.totalPower += unit.getPower().value
            statistic.totalHealth += unit.getHealth().value
        })
        return statistic
    }
}
