package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.bot.strategy.GoalStrategy
import com.broll.gainea.server.core.goals.CustomOccupyGoal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.PlayerUtils

open class G_MoveUnit(difficulty: GoalDifficulty = GoalDifficulty.EASY, private val distance: Int = 6) : CustomOccupyGoal(difficulty, "") {
    private lateinit var from: Area
    private lateinit var to: Area
    private val walkingUnits = mutableListOf<Unit>()

    private fun findPath(): Area? {
        var step = 0
        val visited: MutableList<Location?> = ArrayList()
        val remaining = from.walkableNeighbours.toMutableList()
        do {
            step++
            if (step == distance) {
                remaining.shuffle()
                val target = remaining.filterIsInstance(Area::class.java).firstOrNull()
                if (target != null) {
                    return target
                }
            }
            visited.addAll(remaining)
            remaining += remaining.flatMap { it.walkableNeighbours }.distinct().toMutableList()
            remaining.removeAll(visited)
        } while (remaining.isNotEmpty())
        return null
    }

    override fun init(game: GameContainer, player: Player): Boolean {
        this.game = game
        this.player = player
        val startingPoints = game.map.allAreas.shuffled().toMutableList()
        var target: Area? = null
        while (startingPoints.isNotEmpty() && target == null) {
            from = startingPoints.removeAt(0)
            target = findPath()
        }
        if (target == null) {
            return false
        }
        to = target
        locations.add(from)
        locations.add(to)
        text = "Bewege eine Einheit von " + from.name + " nach " + to.name
        setProgressionGoal(distance)
        return super.init(game, player)
    }

    override fun check() {
        walkingUnits.removeIf { it.dead }
        walkingUnits.addAll(PlayerUtils.getUnits(player, from))
        if (walkingUnits.any { it.location === to }) {
            updateProgression(distance)
            success()
        } else {
            val closestDistance = walkingUnits.minOf { LocationUtils.getWalkingDistance(it, it.location, to) }
            updateProgression(distance - closestDistance)
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setRequiredUnits(1)
        strategy.isSpreadUnits = false
        strategy.updateTargets(setOf(from))
        strategy.setPrepareStrategy { strategy.units.filter { walkingUnits.contains(it) }.forEach { strategy.botStrategy.moveTargets[it] = to } }
    }
}
