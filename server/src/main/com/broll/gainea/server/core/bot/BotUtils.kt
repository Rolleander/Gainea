package com.broll.gainea.server.core.bot

import com.broll.gainea.net.NT_Goal
import com.broll.gainea.net.NT_Unit
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.getWalkingDistance

object BotUtils {
    fun getLocation(game: Game, id: Int) = game.map.getLocation(id)

    fun getObjects(game: Game, units: Array<NT_Unit>) = units.map { getObject(game, it) }

    fun getObject(game: Game, unit: NT_Unit): Unit {
        if (unit.owner == NT_Unit.NO_OWNER.toShort()) {
            for (`object` in game.objects) {
                if (`object`.id == unit.id.toInt() && `object` is Unit) {
                    return `object`
                }
            }
        }
        for (player in game.allPlayers) {
            if (player.serverPlayer.id == unit.owner.toInt()) {
                for (`object` in player.units) {
                    if (`object`.id == unit.id.toInt()) {
                        return `object`
                    }
                }
            }
        }
        throw RuntimeException("unknown object")
    }

    fun getGoal(game: Game, nt: NT_Goal) = game.allPlayers.flatMap { it.goalHandler.goals }.first { it.id == nt.id }


    fun getLocations(game: Game, options: ShortArray): List<Location> {
        val locations: MutableList<Location> = ArrayList()
        for (id in options) {
            locations.add(getLocation(game, id.toInt()))
        }
        return locations
    }

    fun <E> getHighestScoreEntry(list: List<E>, scoring: (E) -> Int): E {
        return list[getHighestScoreIndex(list, scoring)]
    }

    fun <E> getHighestScoreIndex(list: List<E>, scoring: (E) -> Int): Int {
        var score = Int.MIN_VALUE
        var index = 0
        for (i in list.indices) {
            val s = scoring(list[i])
            if (s > score) {
                score = s
                index = i
            }
        }
        return index
    }

    fun <E> getLowestScoreEntry(list: List<E>, scoring: (E) -> Int): E {
        return list[getLowestScoreIndex(list, scoring)]
    }

    fun <E> getLowestScoreIndex(list: List<E>, scoring: (E) -> Int): Int {
        var score = Int.MAX_VALUE
        var index = 0
        for (i in list.indices) {
            val s = scoring(list[i])
            if (s < score) {
                score = s
                index = i
            }
        }
        return index
    }

    fun getBestPath(player: Player, obj: Unit, fromOptions: Collection<Location>, to: Location): Pair<Location, Int> {
        var distance = Int.MAX_VALUE
        var units = 0
        var location = fromOptions.iterator().next()
        for (from in fromOptions) {
            val d = obj.getWalkingDistance(from, to)
            val u = from.units.filter { it.owner === player }.count()
            if (d != null && d < distance) {
                distance = d
                location = from
                units = u
            } else if (d == distance && u > units) {
                units = u
                location = from
            }
        }
        return location to distance
    }

    fun getBestPath(obj: Unit, from: Location, toOptions: Collection<Location>): Pair<Location, Int> {
        var distance = Int.MAX_VALUE
        var units = 0
        var location = toOptions.iterator().next()
        for (to in toOptions) {
            val d = obj.getWalkingDistance(from, to)
            val u = to.units.filter { it.owner === obj.owner }.count()
            if (d != null && d < distance) {
                distance = d
                location = to
            } else if (d == distance && u > units) {
                units = u
                location = to
            }
        }
        return location to distance
    }

    fun huntOtherPlayersTargets(owner: Player, game: Game) = game.allPlayers.filter { it !== owner }.flatMap { it.controlledLocations }.toHashSet()


    fun huntPlayerTargets(player: Player) = player.controlledLocations.toHashSet()
}
