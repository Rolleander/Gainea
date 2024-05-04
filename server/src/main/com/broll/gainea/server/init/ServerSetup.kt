package com.broll.gainea.server.init

import com.broll.gainea.server.sites.GameBoardSite
import com.broll.gainea.server.sites.GameLobbySite
import com.broll.gainea.server.sites.GameStartSite
import com.broll.gainea.server.sites.GameVoteSite
import com.broll.networklib.server.LobbyGameServer

object ServerSetup {
    fun setup(server: LobbyGameServer<LobbyData, PlayerData>) {
        server.register(GameBoardSite(), GameStartSite(), GameLobbySite(), GameVoteSite())
    }
}
