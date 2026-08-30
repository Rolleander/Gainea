package com.broll.gainea.server.core.player

import com.broll.gainea.net.proto.NT_Event
import com.broll.gainea.net.proto.NT_Event_OtherPlayerReceivedGoal
import com.broll.gainea.net.proto.NT_Event_ReceivedGoal
import com.broll.gainea.net.proto.NT_Event_ReceivedPoints
import com.broll.gainea.net.proto.NT_Event_ReceivedStars
import com.broll.gainea.net.proto.NT_Event_RemoveGoal
import com.broll.gainea.net.proto.NT_Goal
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

    fun isValidNewGoal(goal: Goal) = with(goal.javaClass) {
        goals.none { isInstance(it) } && previousGoals.none { isInstance(it) }
    }

    fun addPoints(points: Int) {
        if (player.isNeutral()) return
        score += points
        game.sendUpdate(
            NT_Event_ReceivedPoints.newBuilder()
                .setBase(NT_Event.newBuilder().setPlayer(player.serverPlayer.id).build())
                .setPoints(points).build()
        )
        ProcessingUtils.pause(500)
        if (game.isGameEnd()) {
            game.end()
        }
    }

    fun addStars(stars: Int) {
        if (player.isNeutral()) return
        this.stars += stars
        game.sendUpdate(
            NT_Event_ReceivedStars.newBuilder()
                .setBase(NT_Event.newBuilder().setPlayer(player.serverPlayer.id).build())
                .setStars(stars).build()
        )
        ProcessingUtils.pause(500)
        game.updateReceiver.earnedStars(player, stars)
    }

    fun removeStars(stars: Int) {
        if (player.isNeutral()) return
        this.stars -= stars
        game.sendUpdate(
            NT_Event_ReceivedStars.newBuilder()
                .setBase(NT_Event.newBuilder().setPlayer(player.serverPlayer.id).build())
                .setStars(-stars).build()
        )
    }

    fun removeGoal(oldGoal: Goal) {
        if (player.isNeutral()) return
        goals.remove(oldGoal)
        game.updateReceiver.unregister(oldGoal)
        player.serverPlayer.send(NT_Event_RemoveGoal.newBuilder().setGoal(oldGoal.nt()).build())
        previousGoals.add(oldGoal)
    }

    fun newGoal(goal: Goal) {
        if (player.isNeutral()) return
        goals.add(goal)
        game.updateReceiver.register(goal)
        val nt = NT_Event_ReceivedGoal.newBuilder()
            .setBase(NT_Event.newBuilder().setSound("swush.ogg").build())
            .setGoal(goal.nt()).build()
        val nt2 = NT_Event_OtherPlayerReceivedGoal.newBuilder()
            .setBase(NT_Event.newBuilder().setPlayer(player.serverPlayer.id).build()).build()
        game.sendUpdate(player, nt, nt2)
        //directly check goal for completion
        game.processingCore.execute({ goal.check() }, 100)
    }

    fun ntGoals(): List<NT_Goal> = goals.map { it.nt() }
}
