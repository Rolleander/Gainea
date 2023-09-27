package com.broll.gainea.server.init

import com.broll.gainea.server.sites.GameBoardSite
import com.broll.gainea.server.sites.GameLobbySite
import com.broll.gainea.server.sites.GameStartSite
import com.broll.networklib.server.LobbyGameServer

object ServerSetup {
    fun setup(server: LobbyGameServer<LobbyData, PlayerData>) {
        val gameBoardSite = GameBoardSite()
        val gameStartSite = GameStartSite()
        val gameLobbySite = GameLobbySite()
        server.register(gameBoardSite, gameStartSite, gameLobbySite)
    }
}
