package com.broll.gainea.server.init;

import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_RoundStatistic;
import com.broll.gainea.server.sites.GameLobbySite;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.sites.GameBoardSite;
import com.broll.gainea.server.sites.GameStartSite;
import com.broll.networklib.NetworkRegister;
import com.broll.networklib.client.LobbyGameClient;
import com.broll.networklib.server.LobbyGameServer;

public class NetworkSetup {

    public static final String NETWORK_PACKAGE = "com.broll.gainea.net";

    public final static void setup(LobbyGameServer<LobbyData, PlayerData> server) {
        GameBoardSite gameBoardSite = new GameBoardSite();
        GameStartSite gameStartSite = new GameStartSite();
        GameLobbySite gameLobbySite = new GameLobbySite();
        server.register(gameBoardSite, gameStartSite, gameLobbySite);
    }

    public static void registerNetwork(NetworkRegister register) {
        register.registerNetworkPackage(NETWORK_PACKAGE);
        register.registerNetworkType(NT_Action[].class);
        register.registerNetworkType(NT_BoardObject[].class);
        register.registerNetworkType(NT_Unit[].class);
        register.registerNetworkType(NT_Player[].class);
        register.registerNetworkType(NT_Goal[].class);
        register.registerNetworkType(NT_Card[].class);
        register.registerNetworkType(int[].class);
        register.registerNetworkType(String[].class);
        register.registerNetworkType(Object[].class);
        register.registerNetworkType(NT_RoundStatistic[].class);
    }
}
