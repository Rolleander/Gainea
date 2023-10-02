package com.broll.gainea.server.core.stats

import com.broll.gainea.net.NT_RoundStatistic

class TurnStatistic(val players: List<PlayerStatistic>) {

    fun nt(): NT_RoundStatistic {
        val nt = NT_RoundStatistic()
        nt.fightingPower = ShortArray(players.size)
        nt.locations = ByteArray(players.size)
        nt.total = ShortArray(players.size)
        nt.units = ByteArray(players.size)
        for (i in players.indices) {
            val fightingPower = players[i].totalPower + players[i].totalHealth
            val units = players[i].units
            val location = players[i].controlledLocations
            nt.fightingPower[i] = fightingPower.toShort()
            nt.units[i] = units.toByte()
            nt.locations[i] = location.toByte()
            var total = fightingPower * POINTS_POWER
            total += players[i].cards * POINTS_CARD
            total += players[i].stars * POINTS_STAR
            total += players[i].points * POINTS_SCORE
            total += location * POINTS_LOCATION
            nt.total[i] = total.toShort()
        }
        return nt
    }

    companion object {
        private const val POINTS_LOCATION = 2
        private const val POINTS_CARD = 3
        private const val POINTS_STAR = 1
        private const val POINTS_SCORE = 10
        private const val POINTS_POWER = 1
    }
}
