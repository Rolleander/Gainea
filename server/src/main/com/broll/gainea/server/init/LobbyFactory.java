package com.broll.gainea.server.init;

import com.broll.gainea.server.sites.LobbyListener;
import com.broll.networklib.server.impl.ServerLobby;

public class LobbyFactory {

    public static void initLobby(ServerLobby<LobbyData, PlayerData> lobby) {
        LobbyData data = new LobbyData();
        lobby.setData(data);
        lobby.addListener(new LobbyListener());
        lobby.setPlayerLimit(9);
    }

}
