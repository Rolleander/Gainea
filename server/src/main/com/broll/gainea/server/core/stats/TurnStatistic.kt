package com.broll.gainea.server.core.stats

import com.broll.gainea.server.core.map.*

import com.broll.gainea.net.proto.NT_RoundStatistic

class TurnStatistic(val players: List<PlayerStatistic>) {

    fun nt(): NT_RoundStatistic {
        val fightingPower = mutableListOf<Int>()
        val locations = mutableListOf<Int>()
        val totals = mutableListOf<Int>()
        val units = mutableListOf<Int>()
        players.forEach { player ->
            val power = player.totalPower + player.totalHealth
            val location = player.controlledLocations
            fightingPower += power
            units += player.units.toInt()
            locations += location.toInt()
            var total = power * POINTS_POWER
            total += player.cards * POINTS_CARD
            total += player.stars * POINTS_STAR
            total += player.points * POINTS_SCORE
            total += location * POINTS_LOCATION
            totals += total
        }
        return NT_RoundStatistic.newBuilder()
            .addAllFightingPower(fightingPower)
            .addAllLocations(locations)
            .addAllTotal(totals)
            .addAllUnits(units)
            .build()
    }

    companion object {
        private const val POINTS_LOCATION = 2
        private const val POINTS_CARD = 3
        private const val POINTS_STAR = 1
        private const val POINTS_SCORE = 10
        private const val POINTS_POWER = 1
    }
}
