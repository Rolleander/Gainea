package com.broll.gainea.test.network;

import com.broll.gainea.net.NT_PlayerReady;
import com.broll.gainea.net.NT_StartGame;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.init.LobbyFactory;
import com.broll.gainea.server.init.NetworkSetup;
import com.broll.networklib.NetworkRegister;
import com.broll.networklib.network.IRegisterNetwork;
import com.broll.networklib.server.LobbyGameServer;
import com.broll.networklib.test.NetworkTest;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.client.LobbyGameClient;
import com.broll.networklib.server.impl.Player;
import com.broll.networklib.server.impl.ServerLobby;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class GameStartTest extends NetworkTest<LobbyData, PlayerData> {

    @Override
    protected IRegisterNetwork registerNetwork() {
        return NetworkSetup::registerNetwork;
    }

    @Override
    public void registerServerSites(LobbyGameServer<LobbyData, PlayerData> server) {
        NetworkSetup.setup(server);
    }

    @Override
    public void registerClientSites(LobbyGameClient client) {
    }

    @Test
    public void gameStart() {
        ServerLobby<LobbyData, PlayerData> lobby = openGameLobby(null, "TestLobby");
        LobbyFactory.initLobby(lobby, ExpansionSetting.BASIC_GAME);

        LobbyGameClient hans = testClient("Hans", lobby);
        assertFalse(lobby.isLocked());
        assertNull(lobby.getData().getGame());
        LobbyGameClient peter = testClient("Peter", lobby);

        dropPackages();
        NT_PlayerReady ready = new NT_PlayerReady();
        ready.ready = true;
        getTestClient(hans).sendTCP(ready);
        getTestClient(peter).sendTCP(ready);
        assertTrue(lobby.isLocked());
        GameContainer game = lobby.getData().getGame();
        assertNotNull(game);

        getTestClient(hans).assureReceived(NT_StartGame.class);
        getTestClient(peter).assureReceived(NT_StartGame.class);
    }

}
