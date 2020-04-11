package com.broll.gainea.server;

import com.broll.gainea.server.sites.GameLobbySite;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_Location;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.sites.GameBoardSite;
import com.broll.gainea.server.sites.GameStartSite;
import com.broll.networklib.NetworkRegister;
import com.broll.networklib.client.LobbyGameClient;
import com.broll.networklib.network.nt.NT_LobbyInformation;
import com.broll.networklib.server.LobbyGameServer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;

public class NetworkSetup {

    public static final String NETWORK_PACKAGE = "com.broll.gainea.net";

    public final static void setup(LobbyGameServer<LobbyData, PlayerData> server) {
        GameBoardSite gameBoardSite = new GameBoardSite();
        GameStartSite gameStartSite = new GameStartSite(gameBoardSite);
        GameLobbySite gameLobbySite = new GameLobbySite(gameStartSite);
        server.register(gameBoardSite, gameStartSite, gameLobbySite);
    }

    public static void setup(LobbyGameClient client) {
    }

    public static void registerNetwork(NetworkRegister register) {
        register.registerNetworkPackage(NETWORK_PACKAGE);
        register.registerNetworkType(NT_Action[].class);
        register.registerNetworkType(NT_BoardObject[].class);
        register.registerNetworkType(NT_Unit[].class);
        register.registerNetworkType(NT_Location[].class);
        register.registerNetworkType(NT_Player[].class);
        register.registerNetworkType(NT_Goal[].class);
        register.registerNetworkType(int[].class);
        register.registerNetworkType(String[].class);
        register.registerNetworkType(Object[].class);
    }
}
