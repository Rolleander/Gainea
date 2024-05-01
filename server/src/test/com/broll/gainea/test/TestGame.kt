package com.broll.gainea.test

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.init.ExpansionSetting
import com.broll.gainea.server.init.ExpansionSetting.FULL
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.DummyPlayer
import com.broll.networklib.server.impl.DummyServerLobby

fun testGame(expansionSetting: ExpansionSetting = FULL, playersCount: Int = 0): Game {
    val lobby = DummyServerLobby<LobbyData, PlayerData>()
    lobby.data = LobbyData()
    (1..playersCount).forEach {
        val player = DummyPlayer<PlayerData>(it)
        player.name = "Tester#$it"
        lobby.addDummyPlayer(player)
    }
    lobby.players.forEach { it.data = PlayerData(FractionType.entries.random()) }
    lobby.data.expansionSetting = expansionSetting
    return Game(lobby)
}
