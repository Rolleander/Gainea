package com.broll.gainea.client.network;

import com.badlogic.gdx.Gdx;
import com.broll.gainea.Gainea;
import com.broll.gainea.NetworkSetup;
import com.broll.gainea.client.network.sites.GameActionSite;
import com.broll.gainea.client.network.sites.GameBattleSite;
import com.broll.gainea.client.network.sites.GameBoardSite;
import com.broll.gainea.client.network.sites.GameEventSite;
import com.broll.gainea.client.network.sites.GameStateSite;
import com.broll.gainea.client.network.sites.GameTurnSite;
import com.broll.networklib.client.ClientSite;
import com.broll.networklib.client.LobbyGameClient;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.site.SiteReceiver;
import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ClientHandler {

    private final static Logger Log = LoggerFactory.getLogger(ClientHandler.class);
    private LobbyGameClient client;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean clientBusy = false;
    private IClientListener clientListener;
    private Gainea game;

    public ClientHandler(Gainea game) {
        this.game = game;
        client = new LobbyGameClient(NetworkSetup::registerNetwork);
        client.setSiteReceiver(new SiteReceiver<ClientSite, com.broll.networklib.client.GameClient.ClientConnection>() {
            @Override
            public void receive(com.broll.networklib.client.GameClient.ClientConnection context, ClientSite site, Method receiver, Object object) {
                //perform operations from received packages on main thread
                runOnGdx(() -> super.receive(context, site, receiver, object));
            }
        });
        initGameSites(game);
    }

    private void initGameSites(Gainea game) {
        client.clearSites();
        GameActionSite actionSite = new GameActionSite();
        Lists.newArrayList(actionSite, new GameBattleSite(), new GameBoardSite(), new GameEventSite(), new GameStateSite(), new GameTurnSite(actionSite)).forEach(site -> {
            site.init(game);
            client.register(site);
        });
    }

    public void setClientListener(IClientListener clientListener) {
        this.clientListener = clientListener;
    }

    public void listLobbies(String ip) {
        clientExecute(() -> client.listLobbies(ip), lobbies -> clientListener.discoveredLobbies(lobbies), "Failed to list lobbies");
    }

    public void joinLobby(String playerName, GameLobby lobby) {
        clientExecute(() -> client.joinLobby(lobby, playerName), this::connectedLobby, "Unable to join lobby");
    }

    public void createLobby(String playerName) {
        clientExecute(() -> client.createLobby(playerName, null), this::connectedLobby, "Unable to create lobby");
    }

    private void connectedLobby(GameLobby lobby) {
        listenLobbyUpdates(lobby);
        runOnGdx(() -> clientListener.connectedLobby(lobby));
    }

    public void reconnectCheck() {
        clientExecute(() -> client.reconnectCheck(), lobby -> {
            //joins game directly, so dont call join lobby listener , will receive a game reconnect message in site and open game directly
            if (lobby != null) {
                listenLobbyUpdates(lobby);
            }
        }, "Unable to reconnect to lobby");
    }

    private void listenLobbyUpdates(GameLobby lobby) {
        lobby.setLobbyUpdateListener(new ClientLobbyListener(game));
    }

    private void runOnGdx(Runnable runnable) {
        Gdx.app.postRunnable(runnable);
    }

    private <T> void clientExecute(Callable<CompletableFuture<T>> runnable, Consumer<T> resultHandler, String errorMessage) {
        clientExecute(() -> {
            try {
                resultHandler.accept(runnable.call().get());
            } catch (Exception e) {
                Log.error(errorMessage, e);
                String[] messageParts = e.getMessage().split("Exception: ");
                clientListener.connectionFailure(errorMessage + "\n (" + messageParts[messageParts.length - 1] + ")");
            }
        });
    }

    private synchronized void clientExecute(Runnable runnable) {
        if (!clientBusy) {
            clientBusy = true;
            runOnGdx(() -> clientListener.loadingStateUpdate(true));
            executor.execute(() -> {
                runnable.run();
                clientBusy = false;
                runOnGdx(() -> clientListener.loadingStateUpdate(false));
            });
        } else {
            Log.info("client busy");
        }
    }

    public void shutdown() {
        client.shutdown();
    }

    public LobbyGameClient getClient() {
        return client;
    }
}
