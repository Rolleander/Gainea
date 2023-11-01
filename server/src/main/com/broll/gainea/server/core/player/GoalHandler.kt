package com.broll.gainea.server.core.player

import com.broll.gainea.net.NT_Event_OtherPlayerReceivedGoal
import com.broll.gainea.net.NT_Event_ReceivedGoal
import com.broll.gainea.net.NT_Event_ReceivedPoints
import com.broll.gainea.net.NT_Event_ReceivedStars
import com.broll.gainea.net.NT_Event_RemoveGoal
import com.broll.gainea.net.NT_Goal
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.isGameEnd
import com.broll.gainea.server.core.utils.sendUpdate

class GoalHandler(private val game: Game, private val player: Player) {

    private val previousGoals = mutableListOf<Goal>()
    var score = 0
        private set
    var stars = 0
        private set

    val goals = mutableListOf<Goal>()

    fun isValidNewGoal(goal: Goal) =
            goals.none { it::class == goal::class } && previousGoals.none { it::class == goal::class }

    fun addPoints(points: Int) {
        if (player.isNeutral()) return
        score += points
        val nt = NT_Event_ReceivedPoints()
        nt.player = player.serverPlayer.id
        nt.points = points
        game.sendUpdate(nt)
        ProcessingUtils.pause(500)
        if (game.isGameEnd()) {
            game.end()
        }
    }

    fun addStars(stars: Int) {
        if (player.isNeutral()) return
        this.stars += stars
        val nt = NT_Event_ReceivedStars()
        nt.player = player.serverPlayer.id
        nt.stars = stars
        game.sendUpdate(nt)
        ProcessingUtils.pause(500)
        game.updateReceiver.earnedStars(player, stars)
    }

    fun removeGoal(oldGoal: Goal) {
        if (player.isNeutral()) return
        goals.remove(oldGoal)
        game.updateReceiver.unregister(oldGoal)
        val nt = NT_Event_RemoveGoal()
        nt.goal = oldGoal.nt()
        player.serverPlayer.sendTCP(nt)
        previousGoals.add(oldGoal)
    }

    fun newGoal(goal: Goal) {
        if (player.isNeutral()) return
        goals.add(goal)
        game.updateReceiver.register(goal)
        val nt = NT_Event_ReceivedGoal()
        nt.sound = "chime.ogg"
        nt.goal = goal.nt()
        val nt2 = NT_Event_OtherPlayerReceivedGoal()
        nt2.player = player.serverPlayer.id
        game.sendUpdate(player, nt, nt2)
        //directly check goal for completion
        game.processingCore.execute({ goal.check() }, 100)
    }

    fun ntGoals(): Array<NT_Goal> = goals.map { it.nt() }.toTypedArray()
}
