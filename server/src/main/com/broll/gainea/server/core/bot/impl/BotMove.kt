package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.NT_Actionimport

com.broll.gainea.net.NT_Action_Moveimport com.broll.gainea.net.NT_Reactionimport com.broll.gainea.net.NT_Unitimport com.broll.gainea.server.core.bot.BotOptionalActionimport com.broll.gainea.server.core.bot.BotUtilsimport com.broll.gainea.server.core.bot.impl .BotMove.MoveOptionimport com.broll.gainea.server.core.bot.strategy.GoalStrategyimport com.broll.gainea.server.core.bot.strategy.LocationDangerimport com.broll.gainea.server.core.utils.LocationUtils com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.Unit
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListenerimport

org.apache.commons.lang3.ArrayUtilsimport org.slf4j.LoggerFactoryimport java.util.ArrayListimport java.util.Arraysimport java.util.HashMapimport java.util.Objectsimport java.util.concurrent.atomic.AtomicIntegerimport java.util.function.Consumerimport java.util.function.Functionimport java.util.stream.Collectors
class BotMove : BotOptionalAction<NT_Action_Move, MoveOption?>() {
    override fun react(action: NT_Action_Move, reaction: NT_Reaction) {
        reaction.options = selectedOption.moveUnits
    }

    override fun score(action: NT_Action_Move): MoveOption? {
        val location = BotUtils.getLocation(game!!, action.location.toInt())
        val units = BotUtils.getObjects(game!!, action.units)
        val goalStrategies = units!!.stream().map { it: Unit? -> strategy!!.getStrategy(it) }.filter { obj: GoalStrategy? -> Objects.nonNull(obj) }.distinct().collect(Collectors.toList())
        for (goalStrategy in goalStrategies) {
            val goalUnits = units.stream().filter { it: Unit? -> strategy!!.getStrategy(it) === goalStrategy }.collect(Collectors.toList())
            val move = chooseUnits(goalStrategy, action.units, goalUnits, location)
            if (move != null) {
                return move
            }
        }
        Log.trace("no goal strategies for moving units")
        return null
    }

    override val actionClass: Class<out NT_Action?>?
        get() = NT_Action_Move::class.java

    private fun chooseUnits(goalStrategy: GoalStrategy?, nt_units: Array<NT_Unit>, units: List<Unit?>, to: Location?): MoveOption? {
        val unitTargets = getPathTargets(units, goalStrategy)
        var distance = Int.MAX_VALUE
        val moveTogether: MutableList<Unit?> = ArrayList()
        var moveFrom: Location? = null
        var flee = false
        val annihilationChances: MutableMap<Location?, Double> = HashMap()
        units.stream().map { obj: Unit? -> obj.getLocation() }.distinct().forEach { location: Location? -> annihilationChances[location] = LocationDanger.getAnnihilationChance(bot!!, location) }
        for (i in unitTargets.indices) {
            val unit = units[i]
            val unitTarget = unitTargets[i]
            if (!flee) {
                val annihilationChance = annihilationChances[unit.getLocation()]!!
                if (annihilationChance >= 0.9) {
                    flee = true
                }
            }
            if (!flee) {
                if (unitTarget == null || unitTarget === unit.getLocation()) {
                    continue
                }
                val currentTargetDistance = LocationUtils.getWalkingDistance(unit, unit.getLocation(), unitTarget)
                val moveTargetDistance = LocationUtils.getWalkingDistance(unit, to, unitTarget)
                if (moveTargetDistance > currentTargetDistance) {
                    continue
                }
                distance = Math.min(moveTargetDistance, distance)
            }
            if (moveFrom == null) {
                moveFrom = unit.getLocation()
            }
            if (unit.getLocation() === moveFrom) {
                moveTogether.add(unit)
            }
        }
        if (moveTogether.isEmpty()) {
            return null
        }
        val unitIds = Arrays.stream(nt_units).mapToInt { it: NT_Unit -> it.id.toInt() }.toArray()
        var score = Math.max(MOVE_SCORE - distance, 1)
        if (flee) {
            score = LocationDanger.getFleeToScore(bot!!, to)
        }
        val moveOption = MoveOption(score.toFloat())
        moveOption.location = to
        moveOption.moveUnits = moveTogether.stream().mapToInt { it: Unit? -> ArrayUtils.indexOf(unitIds, it.getId()) }.toArray()
        return moveOption
    }

    private fun getPathTargets(units: List<Unit?>, goalStrategy: GoalStrategy?): List<Location> {
        return units.stream().map { it: Unit? ->
            val target = strategy.moveTargets[it] ?: return@map createPath(it, goalStrategy)
            target
        }.collect(Collectors.toList())
    }

    private fun createPath(unit: Unit?, goalStrategy: GoalStrategy?): Location? {
        val target = provideNextTarget(unit, goalStrategy)
        Log.trace("Set target for $unit to $target")
        if (target != null) {
            strategy.moveTargets[unit] = target
        }
        return target
    }

    private fun provideNextTarget(unit: Unit?, goalStrategy: GoalStrategy?): Location? {
        val targets = goalStrategy.getTargetLocations()
        if (targets!!.size == 1) {
            return targets!!.iterator().next()
        }
        if (targets!!.isEmpty()) {
            Log.trace(goalStrategy.toString() + " has no targets...")
            return null
        }
        val targetCounts: MutableMap<Location?, AtomicInteger> = HashMap()
        targets!!.forEach(Consumer { it: Location? ->
            var distance = LocationUtils.getWalkingDistance(unit, unit.getLocation(), it)
            if (distance == -1) {
                distance = 100
            }
            targetCounts[it] = AtomicInteger(distance)
        })
        if (goalStrategy!!.isSpreadUnits) {
            goalStrategy.units.stream().map { it: Unit? -> strategy.moveTargets[it] }.forEach { target: Location? ->
                val count = targetCounts[target]
                count?.addAndGet(1000)
            }
        }
        //todo check if target is same as existing unit but closer,
        return BotUtils.getLowestScoreEntry<Map.Entry<Location, AtomicInteger>>(ArrayList<Map.Entry<Location?, AtomicInteger>>(targetCounts.entries),
                Function<Map.Entry<Location?, AtomicInteger>, Int> { (_, value): Map.Entry<Location?, AtomicInteger> -> value.get() }).key
    }

    class MoveOption(score: Float) : BotOption(score) {
        val moveUnits: IntArray
        val location: Location? = null
        override fun toString(): String {
            return "move to " + location + " with " + moveUnits.size + " units"
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(BotMove::class.java)
        const val MOVE_SCORE = 10
    }
}
