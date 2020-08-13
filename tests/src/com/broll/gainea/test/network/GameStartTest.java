package com.broll.gainea.test.network;

import com.broll.gainea.test.NetworkTest;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.client.LobbyGameClient;
import com.broll.networklib.server.impl.Player;
import com.broll.networklib.server.impl.ServerLobby;

import org.junit.Test;

import static org.junit.Assert.*;

public class GameStartTest extends NetworkTest {

    @Test
    public void connect(){
        LobbyData data = new LobbyData();
        data.setExpansionSetting(ExpansionSetting.BASIC_GAME);
        ServerLobby<LobbyData, PlayerData> lobby = openGameLobby(data,"TestLobby");

        LobbyGameClient peter = testClient("Peter", lobby);
        LobbyGameClient pan = testClient("Pan", lobby);

        assertEquals(2, lobby.getPlayers().size());
        dropPackages();
    }

    @Test
    public void lobbyTransfer() {
        LobbyData data = new LobbyData();
        data.setExpansionSetting(ExpansionSetting.BASIC_GAME);
        ServerLobby<LobbyData, PlayerData> lobby1 = openGameLobby(data,"TestLobby");
        ServerLobby<LobbyData, PlayerData> lobby2 = openGameLobby(data,"TestLobby2");
        assertEquals(2, gameServer.getLobbyHandler().getLobbies().size());
        LobbyGameClient peter = testClient("Peter", lobby1);
        assertEquals(1, lobby1.getPlayers().size());
        assertEquals(0, lobby2.getPlayers().size());
        Player<PlayerData> serverPeter = lobby1.getPlayers().iterator().next();
        joinLobby(peter, lobby2);
        assertEquals(0, lobby1.getPlayers().size());
        assertTrue(lobby1.isClosed());
        assertEquals(1, gameServer.getLobbyHandler().getLobbies().size());
        assertEquals(1, lobby2.getPlayers().size());
        assertEquals(serverPeter, lobby2.getPlayers().iterator().next());
        dropPackages();
    }

    @Test
    public void doubleJoin() {
        LobbyData data = new LobbyData();
        data.setExpansionSetting(ExpansionSetting.BASIC_GAME);
        ServerLobby<LobbyData, PlayerData> lobby = openGameLobby(data,"TestLobby");
        LobbyGameClient peter = testClient("Peter", lobby);
        assertEquals(1, lobby.getPlayers().size());
        joinLobby(peter, lobby);
        assertEquals(1, lobby.getPlayers().size());
        dropPackages();
    }

    @Test
    public void kickPlayer()  {
        LobbyData data = new LobbyData();
        data.setExpansionSetting(ExpansionSetting.BASIC_GAME);
        ServerLobby<LobbyData, PlayerData> lobby = openGameLobby(data,"TestLobby");
        LobbyGameClient peter = testClient("Peter", lobby);
        LobbyGameClient hans = testClient("Hans", lobby);
        assertEquals(2, lobby.getPlayers().size());
        lobby.kickPlayer(lobby.getPlayer(0));
        assertEquals(1, lobby.getPlayers().size());
        dropPackages();
    }

}
