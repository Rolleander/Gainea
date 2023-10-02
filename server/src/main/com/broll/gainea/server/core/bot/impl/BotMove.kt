package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_Action_Move
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.bot.BotOptionalAction
import com.broll.gainea.server.core.bot.BotUtils
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.bot.strategy.LocationDanger
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.getWalkingDistance
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

class BotMove : BotOptionalAction<NT_Action_Move, BotMove.MoveOption>() {
    override fun react(action: NT_Action_Move, reaction: NT_Reaction) {
        reaction.options = selectedOption!!.moveUnits
    }

    override fun score(action: NT_Action_Move): MoveOption? {
        val location = BotUtils.getLocation(game, action.location.toInt())
        val units = BotUtils.getObjects(game, action.units)
        val goalStrategies = units.mapNotNull { strategy.getStrategy(it) }
        for (goalStrategy in goalStrategies) {
            val goalUnits = units.filter { strategy.getStrategy(it) == goalStrategy }
            val move = chooseUnits(goalStrategy, goalUnits, location)
            if (move != null) {
                return move
            }
        }
        Log.trace("no goal strategies for moving units")
        return null
    }

    override val actionClass: Class<out NT_Action>
        get() = NT_Action_Move::class.java

    private fun chooseUnits(goalStrategy: GoalStrategy, units: List<Unit>, to: Location): MoveOption? {
        val unitTargets = getPathTargets(units, goalStrategy)
        var distance = Int.MAX_VALUE
        val moveTogether = mutableListOf<Unit>()
        var moveFrom: Location? = null
        var flee = false
        val annihilationChances = HashMap<Location, Double>()
        units.map { it.location }.distinct().forEach {
            annihilationChances[it] = LocationDanger.getAnnihilationChance(bot, it)
        }
        for (i in unitTargets.indices) {
            val unit = units[i]
            val unitTarget = unitTargets[i]
            if (!flee) {
                val annihilationChance = annihilationChances[unit.location]!!
                if (annihilationChance >= 0.9) {
                    flee = true
                }
            }
            if (!flee) {
                if (unitTarget == null || unitTarget === unit.location) {
                    continue
                }
                val currentTargetDistance = unit.getWalkingDistance(unit.location, unitTarget)
                val moveTargetDistance = unit.getWalkingDistance(to, unitTarget)
                if (currentTargetDistance == null || moveTargetDistance == null) {
                    continue
                }
                if (moveTargetDistance > currentTargetDistance) {
                    continue
                }
                distance = Math.min(moveTargetDistance, distance)
            }
            if (moveFrom == null) {
                moveFrom = unit.location
            }
            if (unit.location === moveFrom) {
                moveTogether.add(unit)
            }
        }
        if (moveTogether.isEmpty()) {
            return null
        }
        var score = Math.max(MOVE_SCORE - distance, 1)
        if (flee) {
            score = LocationDanger.getFleeToScore(bot, to)
        }
        return MoveOption(score.toFloat(), moveTogether.map { it.id }.toIntArray(), to)
    }

    private fun getPathTargets(units: List<Unit>, goalStrategy: GoalStrategy) = units.map {
        strategy.moveTargets[it] ?: createPath(it, goalStrategy)
    }

    private fun createPath(unit: Unit, goalStrategy: GoalStrategy): Location? {
        val target = provideNextTarget(unit, goalStrategy)
        Log.trace("Set target for $unit to $target")
        if (target != null) {
            strategy.moveTargets[unit] = target
        }
        return target
    }

    private fun provideNextTarget(unit: Unit, goalStrategy: GoalStrategy): Location? {
        val targets = goalStrategy.targetLocations
        if (targets.size == 1) {
            return targets.first()
        }
        if (targets.isEmpty()) {
            Log.trace(goalStrategy.toString() + " has no targets...")
            return null
        }
        val targetCounts = HashMap<Location, AtomicInteger>()
        targets.forEach {
            var distance = unit.getWalkingDistance(unit.location, it)
            if (distance == null) {
                distance = 100
            }
            targetCounts[it] = AtomicInteger(distance)
        }
        if (goalStrategy.isSpreadUnits) {
            goalStrategy.units.map { strategy.moveTargets[it] }.forEach { target: Location? ->
                val count = targetCounts[target]
                count?.addAndGet(1000)
            }
        }
        //todo check if target is same as existing unit but closer,
        return BotUtils.getLowestScoreEntry(
                targetCounts.entries.toList()
        ) {
            it.value.toInt()
        }.key
    }

    class MoveOption(score: Float,
                     val moveUnits: IntArray,
                     val location: Location) : BotOption(score) {

        override fun toString(): String {
            return "move to " + location + " with " + moveUnits.size + " units"
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(BotMove::class.java)
        const val MOVE_SCORE = 10
    }
}
