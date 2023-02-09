package com.broll.gainea.server.init;

import com.broll.gainea.NetworkSetup;
import com.broll.gainea.server.sites.GameBoardSite;
import com.broll.gainea.server.sites.GameLobbySite;
import com.broll.gainea.server.sites.GameStartSite;
import com.broll.networklib.server.LobbyGameServer;

public class ServerSetup {
    public final static void setup(LobbyGameServer<LobbyData, PlayerData> server) {
        GameBoardSite gameBoardSite = new GameBoardSite();
        GameStartSite gameStartSite = new GameStartSite();
        GameLobbySite gameLobbySite = new GameLobbySite();
        server.register(gameBoardSite, gameStartSite, gameLobbySite);
    }

}
