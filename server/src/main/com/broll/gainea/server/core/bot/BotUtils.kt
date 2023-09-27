package com.broll.gainea.server.core.bot

import com.broll.gainea.net.NT_Goalimport

com.broll.gainea.net.NT_Unitimport com.broll.gainea.server.core.GameContainerimport com.broll.gainea.server.core.goals.Goalimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.LocationUtilsimport org.apache.commons.lang3.tuple.Pairimport java.util.Arraysimport java.util.function.Functionimport java.util.stream.Collectors
object BotUtils {
    fun getLocation(game: GameContainer, id: Int): Location? {
        return game.map.getLocation(id)
    }

    fun getObjects(game: GameContainer, units: Array<NT_Unit>?): List<Unit?> {
        return Arrays.stream(units).map { it: NT_Unit -> getObject(game, it) }.collect(Collectors.toList())
    }

    fun getObject(game: GameContainer, unit: NT_Unit): Unit? {
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
        return null
    }

    fun getGoal(game: GameContainer, nt: NT_Goal): Goal? {
        return game.allPlayers.stream().flatMap { p: Player? -> p.getGoalHandler().goals.stream() }
                .filter { goal: Goal? -> goal.getId() == nt.id }.findFirst().orElse(null)
    }

    fun getLocations(game: GameContainer, options: ShortArray): List<Location?> {
        val locations: MutableList<Location?> = ArrayList()
        for (id in options) {
            locations.add(getLocation(game, id.toInt()))
        }
        return locations
    }

    fun <E> getHighestScoreEntry(list: List<E>, scoring: Function<E, Int>): E {
        return list[getHighestScoreIndex(list, scoring)]
    }

    fun <E> getHighestScoreIndex(list: List<E>, scoring: Function<E, Int>): Int {
        var score = Int.MIN_VALUE
        var index = 0
        for (i in list.indices) {
            val s = scoring.apply(list[i])
            if (s > score) {
                score = s
                index = i
            }
        }
        return index
    }

    fun <E> getLowestScoreEntry(list: List<E>, scoring: Function<E, Int>): E {
        return list[getLowestScoreIndex(list, scoring)]
    }

    fun <E> getLowestScoreIndex(list: List<E>, scoring: Function<E, Int>): Int {
        var score = Int.MAX_VALUE
        var index = 0
        for (i in list.indices) {
            val s = scoring.apply(list[i])
            if (s < score) {
                score = s
                index = i
            }
        }
        return index
    }

    fun getBestPath(player: Player, `object`: Unit?, fromOptions: Collection<Location?>, to: Location?): Pair<Location?, Int> {
        var distance = Int.MAX_VALUE
        var units = 0
        var location = fromOptions.iterator().next()
        for (from in fromOptions) {
            val d = LocationUtils.getWalkingDistance(`object`, from, to)
            val u = LocationUtils.getUnits(from).stream().filter { it: Unit? -> it.getOwner() === player }.count().toInt()
            if (d != -1 && d < distance) {
                distance = d
                location = from
                units = u
            } else if (d == distance && u > units) {
                units = u
                location = from
            }
        }
        return Pair.of(location, distance)
    }

    fun getBestPath(`object`: Unit?, from: Location?, toOptions: Collection<Location?>?): Pair<Location?, Int> {
        var distance = Int.MAX_VALUE
        var units = 0
        var location = toOptions!!.iterator().next()
        for (to in toOptions) {
            val d = LocationUtils.getWalkingDistance(`object`, from, to)
            val u = LocationUtils.getUnits(to).stream().filter { it: Unit? -> it.getOwner() === `object`.getOwner() }.count().toInt()
            if (d != -1 && d < distance) {
                distance = d
                location = to
            } else if (d == distance && u > units) {
                units = u
                location = to
            }
        }
        return Pair.of(location, distance)
    }

    fun huntOtherPlayersTargets(owner: Player, game: GameContainer): Set<Location?> {
        return game.allPlayers.stream().filter { it: Player? -> it !== owner }.flatMap { it: Player? -> it.getControlledLocations().stream() }.collect(Collectors.toSet())
    }

    fun huntPlayerTargets(player: Player?): Set<Location?> {
        return HashSet(player.getControlledLocations())
    }
}
