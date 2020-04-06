package com.broll.gainea.test.network;

import com.broll.gainea.test.NetworkTest;
import com.broll.gainea.server.ExpansionSetting;
import com.broll.gainea.server.LobbyData;
import com.broll.gainea.server.PlayerData;
import com.broll.networklib.client.LobbyGameClient;
import com.broll.networklib.server.impl.ServerLobby;

import org.junit.Test;

import static org.junit.Assert.*;

public class GameStartTest extends NetworkTest {

    @Test
    public void connect(){
        LobbyData data = new LobbyData();
        data.setExpansionSetting(ExpansionSetting.BASIC_GAME);
        ServerLobby<LobbyData, PlayerData> lobby = openGameLobby(data);

        LobbyGameClient peter = testClient("Peter", lobby);
        LobbyGameClient pan = testClient("Pan", lobby);

        assertEquals(2, lobby.getPlayers().size());

        dropPackages();

    }


}
