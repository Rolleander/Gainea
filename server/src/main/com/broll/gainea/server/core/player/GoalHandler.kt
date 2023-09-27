package com.broll.gainea.server.core.player

import com.broll.gainea.net.NT_Event_OtherPlayerReceivedGoal
import com.broll.gainea.net.NT_Event_ReceivedGoal
import com.broll.gainea.net.NT_Event_ReceivedPoints
import com.broll.gainea.net.NT_Event_ReceivedStars
import com.broll.gainea.net.NT_Event_RemoveGoal
import com.broll.gainea.net.NT_Goal
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.utils.GameUtils
import com.broll.gainea.server.core.utils.ProcessingUtils

class GoalHandler(private val game: GameContainer, private val player: Player) {
    var score = 0
        private set
    var stars = 0
        private set

    val goals = mutableListOf<Goal>()
    fun addPoints(points: Int) {
        score += points
        val nt = NT_Event_ReceivedPoints()
        nt.player = player.serverPlayer.id
        nt.points = points
        GameUtils.sendUpdate(game, nt)
        ProcessingUtils.pause(500)
        if (GameUtils.isGameEnd(game)) {
            game.end()
        }
    }

    fun addStars(stars: Int) {
        this.stars += stars
        val nt = NT_Event_ReceivedStars()
        nt.player = player.serverPlayer.id
        nt.stars = stars
        GameUtils.sendUpdate(game, nt)
        ProcessingUtils.pause(500)
        game.updateReceiver.earnedStars(player, stars)
    }

    fun getGoals(): List<Goal> {
        return goals
    }

    fun removeGoal(oldGoal: Goal) {
        goals.remove(oldGoal)
        game.updateReceiver.unregister(oldGoal)
        val nt = NT_Event_RemoveGoal()
        nt.goal = oldGoal!!.nt()
        player.serverPlayer.sendTCP(nt)
    }

    fun newGoal(goal: Goal) {
        goals.add(goal)
        game.updateReceiver.register(goal)
        val nt = NT_Event_ReceivedGoal()
        nt.sound = "chime.ogg"
        nt.goal = goal.nt()
        val nt2 = NT_Event_OtherPlayerReceivedGoal()
        nt2.player = player.serverPlayer.id
        GameUtils.sendUpdate(game, player, nt, nt2)
        //directly check goal for completion
        game.processingCore.execute({ goal.check() }, 100)
    }

    fun ntGoals(): Array<NT_Goal> = goals.map { it.nt() }.toTypedArray()
}
