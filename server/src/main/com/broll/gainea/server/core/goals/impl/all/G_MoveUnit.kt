package com.broll.gainea.server.core.goals.impl.all

import com.broll.gainea.server.core.GameContainerimport

com.broll.gainea.server.core.bot.strategy.GoalStrategyimport com.broll.gainea.server.core.goals.CustomOccupyGoalimport com.broll.gainea.server.core.goals.GoalDifficultyimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.LocationUtilsimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.google.common.collect.Setsimport java.util.Collectionsimport java.util.stream.Collectors
open class G_MoveUnit @JvmOverloads constructor(difficulty: GoalDifficulty = GoalDifficulty.EASY, private val distance: Int = 6) : CustomOccupyGoal(difficulty, "") {
    private var from: Area? = null
    private var to: Area? = null
    private val walkingUnits: MutableList<Unit?> = ArrayList()
    private fun findPath() {
        from = LocationUtils.getRandomFree(game.map.allAreas) as Area
        var step = 0
        val visited: MutableList<Location?> = ArrayList()
        var remaining = from.getWalkableNeighbours()
        do {
            step++
            if (step == distance) {
                Collections.shuffle(remaining)
                to = remaining!!.stream().filter { it: Location? -> it is Area }.findFirst().orElse(null)
                if (to != null) {
                    return
                }
            }
            visited.addAll(remaining!!)
            remaining = remaining!!.stream().flatMap { it: Location? -> it.getWalkableNeighbours().stream() }.collect(Collectors.toList())
            remaining.removeAll(visited)
        } while (!remaining!!.isEmpty())
    }

    override fun init(game: GameContainer, player: Player?): Boolean {
        this.game = game
        this.player = player
        val startingPoints = game.map.allAreas
        Collections.shuffle(startingPoints)
        while (!startingPoints!!.isEmpty() && to == null) {
            from = startingPoints.removeAt(0)
            findPath()
        }
        if (to == null) {
            return false
        }
        locations.add(from)
        locations.add(to)
        text = "Bewege eine Einheit von " + from.getName() + " nach " + to.getName()
        setProgressionGoal(distance)
        return super.init(game, player)
    }

    override fun check() {
        walkingUnits.removeIf { obj: Unit? -> obj!!.isDead }
        walkingUnits.addAll(PlayerUtils.getUnits(player, from))
        if (walkingUnits.stream().anyMatch { it: Unit? -> it.getLocation() === to }) {
            updateProgression(distance)
            success()
        } else {
            val closestDistance = walkingUnits.stream().map { it: Unit? -> LocationUtils.getWalkingDistance(it, it.getLocation(), to) }.reduce(distance) { a: Int, b: Int -> Math.min(a, b) }
            updateProgression(distance - closestDistance)
        }
    }

    override fun botStrategy(strategy: GoalStrategy) {
        strategy.setRequiredUnits(1)
        strategy.isSpreadUnits = false
        strategy.updateTargets(Sets.newHashSet<Location?>(from))
        strategy.setPrepareStrategy { strategy.units.stream().filter { it: Unit? -> walkingUnits.contains(it) }.forEach { unit: Unit? -> strategy.botStrategy.moveTargets[unit] = to } }
    }
}
