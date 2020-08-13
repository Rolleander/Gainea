package com.broll.gainea.test;

import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.NetworkSetup;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.client.ClientAuthenticationKey;
import com.broll.networklib.client.LobbyGameClient;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.impl.ILobbyDiscovery;
import com.broll.networklib.network.INetworkRequestAttempt;
import com.broll.networklib.server.LobbyGameServer;
import com.broll.networklib.server.impl.ServerLobby;
import com.broll.networklib.test.TestClient;
import com.broll.networklib.test.TestServer;
import com.esotericsoftware.minlog.Log;

import org.junit.Before;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;

public class NetworkTest {

    private final static int TIMEOUT = 5000;
    private final static String IP = "localhost";
    protected TestServer server;
    protected LobbyGameServer<LobbyData, PlayerData> gameServer;
    protected Map<LobbyGameClient, TestClientData> clients = new HashMap<>();

    @Before
    public void before() {
        Log.INFO();
        server = new TestServer(NetworkSetup::registerNetwork, TIMEOUT);
        gameServer = new LobbyGameServer<>(server, "TestServer");
        NetworkSetup.setup(gameServer);
    }

    public LobbyGameClient testClient(String name) {
        TestClient testClient = new TestClient(NetworkSetup::registerNetwork, TIMEOUT);
        testClient.connect(server);
        LobbyGameClient client = new LobbyGameClient(testClient);
        NetworkSetup.registerNetwork(client);
        TestClientData data = new TestClientData();
        data.playerName = name;
        data.testClient = testClient;
        clients.put(client, data);
        client.setClientAuthenticationKey(ClientAuthenticationKey.custom(name));
        client.connectToServer(IP);
        return client;
    }

    public LobbyGameClient testClient(String name, ServerLobby lobbyToConnect) {
        LobbyGameClient client = testClient(name);
        joinLobby(client, lobbyToConnect);
        return client;
    }

    public void clientSend(LobbyGameClient client, Object o) {
        clients.get(client).testClient.sendTCP(o);
    }

    public void serverSend(Object o) {
        server.sendToAllTCP(o);
    }

    public void serverSend(LobbyGameClient client, Object o) {
        server.getConnection(getTestClient(client)).sendTCP(o);
    }

    public void dropPackages() {
        dropClientsPackages();
        dropServerPackages();
    }

    public void dropServerPackages() {
        server.dropReceivedPackages();
    }

    public void dropClientsPackages() {
        clients.values().forEach(e -> e.testClient.dropReceivedPackages());
    }

    public <T> T assertClientReceived(LobbyGameClient client, Class<T> type) {
        return clients.get(client).testClient.assureReceived(type);
    }

    public <T> T assertServerReceived(Class<T> type, LobbyGameClient from) {
        return server.assureReceived(type, clients.get(from).testClient);
    }

    public TestClient getTestClient(LobbyGameClient client) {
        return clients.get(client).testClient;
    }

    public ServerLobby<LobbyData, PlayerData> openGameLobby(LobbyData data, String name) {
        ServerLobby<LobbyData, PlayerData> lobby = gameServer.getLobbyHandler().openLobby(name);
        lobby.setData(data);
        return lobby;
    }

    private void fail(String reason) {
        throw new RuntimeException("Operation failed: " + reason);
    }

    public void joinLobby(LobbyGameClient client, ServerLobby serverLobby) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AsyncTask.doAsync(task ->
                client.listLobbies(new ILobbyDiscovery() {
                    @Override
                    public void discovered(String serverIp, String serverName, List<GameLobby> lobbies) {
                        for (GameLobby lobby : lobbies) {
                            if (lobby.getLobbyId() == serverLobby.getId()) {
                                connectToLobby(client, lobby);
                                task.done(true);
                                return;
                            }
                        }
                        fail("lobby not found");
                    }

                    @Override
                    public void noLobbiesDiscovered() {
                        fail("no lobbies discovered");
                    }

                    @Override
                    public void discoveryDone() {

                    }
                })
        );
    }

    private void connectToLobby(LobbyGameClient client, GameLobby lobby) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AsyncTask.doAsync(task -> {
                    String name = clients.get(client).playerName;
                    client.connectToLobby(lobby, name, new INetworkRequestAttempt<GameLobby>() {
                        @Override
                        public void failure(String reason) {
                            fail(reason);
                        }

                        @Override
                        public void receive(GameLobby response) {
                            //joined lobby
                            task.done(true);
                        }
                    });
                }
        );
    }

    private class TestClientData {
        public String playerName;
        public TestClient testClient;
    }
}
