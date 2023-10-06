package com.broll.gainea.test

import com.broll.gainea.server.GaineaServer
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.init.LobbyData

fun testGame(): Game {
    val server = GaineaServer("")
    val lobby = server.server.lobbyHandler.openLobby("test", LobbyData())
    val game = Game(lobby)
    lobby.data.game = game
    return game
}