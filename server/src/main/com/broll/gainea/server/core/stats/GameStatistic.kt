package com.broll.gainea.server.core.stats

import com.broll.gainea.net.NT_GameStatistic
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter
import com.broll.gainea.server.core.utils.sendUpdate


class GameStatistic(private val game: Game) : GameUpdateReceiverAdapter() {
    private val turnStatistics = mutableListOf<TurnStatistic>()
    override fun roundStarted() {
        registerRound()
    }

    fun registerRound() {
        turnStatistics.add(calcTurnStatistic())
    }

    fun sendStatistic() {
        calcTurnStatistic()
        val nt = NT_GameStatistic()
        nt.rounds = turnStatistics.map { it.nt() }.toTypedArray()
        game.sendUpdate(nt)
    }

    private fun calcTurnStatistic() = TurnStatistic(game.allPlayers.map { calcPlayerStatistic(it) })

    private fun calcPlayerStatistic(player: Player): PlayerStatistic {
        val statistic = PlayerStatistic()
        statistic.controlledLocations = player.controlledLocations.size.toShort()
        statistic.cards = player.cardHandler.cards.size.toShort()
        statistic.stars = player.goalHandler.stars.toShort()
        statistic.points = player.goalHandler.score.toShort()
        player.units.forEach {
            statistic.units++
            statistic.totalPower += it.power.value
            statistic.totalHealth += it.health.value
        }
        return statistic
    }

}
