package com.broll.gainea.server.init;

import com.broll.gainea.server.sites.LobbyListener;
import com.broll.networklib.server.impl.ServerLobby;

public class LobbyFactory {

    public static void initLobby(ServerLobby<LobbyData, PlayerData> lobby, ExpansionSetting expansionSetting) {
        LobbyData data = new LobbyData();
        data.setExpansionSetting(expansionSetting);
        lobby.setData(data);
        lobby.setListener(new LobbyListener());
        lobby.setPlayerLimit(9);
    }

}
