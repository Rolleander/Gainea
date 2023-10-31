package com.broll.gainea.test

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.battle.UnitSnapshot
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.DummyLobbyPlayer
import com.broll.networklib.server.impl.DummyServerLobby
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PoacherTest {

    @Test
    fun test() {
        val lobby = DummyServerLobby<LobbyData, PlayerData>()
        lobby.data = LobbyData()
        //todo add dummy player
//       lobby.players += DummyLobbyPlayer()
        //       val game = Game(lobby)
        //     Assertions.assertEquals(1, game.allPlayers.size)

        val game = testGame()
        val unit = Soldier(game.neutralPlayer)
        unit.setStats(5, 5)

        val snapshot = UnitSnapshot(unit)

        unit.takeDamage(5)

        Assertions.assertEquals(true, unit.dead)
        Assertions.assertEquals(false, snapshot.dead)

    }

}

