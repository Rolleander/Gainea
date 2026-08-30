package com.broll.gainea.server.init

import com.broll.gainea.server.sites.GameBoardSite
import com.broll.gainea.server.sites.GameLobbySite
import com.broll.gainea.server.sites.GameStartSite
import com.broll.gainea.server.sites.GameVoteSite
import com.broll.mpnll.server.MpnllServer

object ServerSetup {
    fun setup(server: MpnllServer) {
        server.addSite(GameBoardSite())
        server.addSite(GameStartSite())
        server.addSite(GameLobbySite())
        server.addSite(GameVoteSite())
    }
}
