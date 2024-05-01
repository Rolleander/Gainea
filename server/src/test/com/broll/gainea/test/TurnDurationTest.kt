package com.broll.gainea.test

import com.broll.gainea.server.core.processing.TurnDuration
import org.amshove.kluent.`should be false`
import org.amshove.kluent.`should be true`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TurnDurationTest {

    private val game = testGame(playersCount = 2)
    private val durations = mutableListOf<TurnDuration>()

    @BeforeEach
    fun initGame() {
        //has to be called once to actually start turn progressing
        game.nextTurn()
    }

    @Test
    fun `turns are correct`() {
        //round 1, player 1 turn
        val duration = TurnDuration(turns = 3)
        register(duration)
        duration.completed().`should be false`()
        progress()
        //round 1, player 2 turn
        duration.completed().`should be false`()
        progress()
        //round 2, player 1 turn
        duration.completed().`should be false`()
        progress()
        //round 2, player 2 turn
        duration.completed().`should be true`()
    }

    @Test
    fun `rounds are correct`() {
        //round 1, player 1 turn
        val duration = TurnDuration(rounds = 2)
        register(duration)
        duration.completed().`should be false`()
        progress()
        //round 1, player 2 turn
        duration.completed().`should be false`()
        progress()
        //round 2, player 1 turn
        duration.completed().`should be false`()
        progress()
        //round 2, player 2 turn
        duration.completed().`should be false`()
        progress()
        //round 3, player 1 turn
        duration.completed().`should be true`()
    }

    @Test
    fun `mid-rounds are correct`() {
        //round 1, player 1 turn
        progress()
        //round 1, player 2 turn
        val duration = TurnDuration(rounds = 2)
        register(duration)
        duration.completed().`should be false`()
        progress()
        //round 2, player 1 turn
        duration.completed().`should be false`()
        progress()
        //round 2, player 2 turn
        duration.completed().`should be false`()
        progress()
        //round 3, player 1 turn
        duration.completed().`should be false`()
        progress()
        //round 3, player 2 turn
        duration.completed().`should be true`()
    }

    private fun register(duration: TurnDuration) {
        duration.register(game)
        durations += duration
    }

    private fun progress() {
        game.nextTurn()
        durations.forEach { it.progress() }
    }

}