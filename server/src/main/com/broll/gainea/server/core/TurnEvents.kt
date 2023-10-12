package com.broll.gainea.server.core

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.net.NT_PlayerWait
import com.broll.gainea.server.core.cards.EventCard
import com.broll.gainea.server.core.cards.RandomEventContainer
import com.broll.gainea.server.core.cards.events.E_GetCards
import com.broll.gainea.server.core.cards.events.E_SpawnGoddrake
import com.broll.gainea.server.core.cards.events.E_SpawnMonster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter
import com.broll.gainea.server.core.utils.countNeutralMonsters
import com.broll.gainea.server.core.utils.getTotalStartMonsters
import com.broll.gainea.server.core.utils.sendUpdate

class TurnEvents(private val game: Game) : GameUpdateReceiverAdapter() {

    private val randomEvents = RandomEventContainer()
    private val randomEventChance = 0.1f

    private fun turnEvent(event: EventCard) {
        val nt = NT_PlayerWait()
        nt.playersTurn = -1
        game.sendUpdate(nt)
        EventCard.run(event, game)
    }

    override fun roundStarted() {
        val turn = game.rounds
        if (turn % GET_CARDS_TURNS == 0) {
            turnEvent(E_GetCards())
        }
        if (turn >= SPAWN_TURNS_START) {
            if (turn % SPAWN_GODDRAKE_TURNS == 0) {
                if (!E_SpawnGoddrake.isGoddrakeAlive(game)) {
                    turnEvent(E_SpawnGoddrake())
                    return
                }
            }
            if (turn % SPAWN_MONSTER_TURNS == 0) {
                val totalAtStart = game.getTotalStartMonsters()
                val currentMonsters = game.countNeutralMonsters()
                val missing = totalAtStart - currentMonsters
                if (missing > 0) {
                    turnEvent(E_SpawnMonster())
                }
            }
        }
    }


    override fun turnStarted(player: Player) {
        if (game.rounds > 1 && RandomUtils.randomBoolean(randomEventChance)) {
            //TODO activate when more events are done
            //   randomEvents.run(game)
        }
    }


    companion object {
        private const val SPAWN_MONSTER_TURNS = 2
        private const val GET_CARDS_TURNS = 5
        private const val SPAWN_GODDRAKE_TURNS = SPAWN_MONSTER_TURNS * 5
        private const val SPAWN_TURNS_START = SPAWN_GODDRAKE_TURNS //first spawn is goddrake
    }
}
