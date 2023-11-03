package com.broll.gainea.test

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.init.ExpansionSetting.FULL
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.DummyServerLobby

fun testGame(): Game {
    val lobby = DummyServerLobby<LobbyData, PlayerData>()
    lobby.data = LobbyData()
    lobby.data.expansionSetting = FULL
    return Game(lobby)
}