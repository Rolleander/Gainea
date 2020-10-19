package com.broll.gainea.client.game;

import com.badlogic.gdx.Gdx;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.IClientListener;
import com.broll.gainea.client.game.sites.AbstractGameSite;
import com.broll.gainea.client.game.sites.GameActionSite;
import com.broll.gainea.client.game.sites.GameBattleSite;
import com.broll.gainea.client.game.sites.GameBoardSite;
import com.broll.gainea.client.game.sites.GameEventSite;
import com.broll.gainea.client.game.sites.GameLobbySite;
import com.broll.gainea.client.game.sites.GameStartSite;
import com.broll.gainea.client.game.sites.GameTurnSite;
import com.broll.gainea.server.init.NetworkSetup;
import com.broll.networklib.client.ClientSite;
import com.broll.networklib.client.LobbyGameClient;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.tasks.DiscoveredLobbies;
import com.broll.networklib.network.INetworkRequestAttempt;
import com.broll.networklib.site.SiteReceiver;
import com.esotericsoftware.minlog.Log;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ClientHandler {

    private LobbyGameClient client;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean clientBusy = false;
    private IClientListener clientListener;

    public ClientHandler(Gainea game) {
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

    public void initGameSites(Gainea game) {
        GameState state = new GameState(game);
        game.state = state;
        client.clearSites();
        GameActionSite actionSite = new GameActionSite();
        Lists.newArrayList(actionSite, new GameBattleSite(), new GameBoardSite(), new GameEventSite(), new GameStartSite(), new GameTurnSite(actionSite)).forEach(site -> {
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

    private void connectedLobby(GameLobby lobby) {
        runOnGdx(() -> clientListener.connectedLobby(lobby));
    }

    public void reconnectCheck() {
        clientExecute(() -> client.reconnectCheck(), lobby -> {
            if (lobby != null) {
                connectedLobby(lobby);
            }
        }, "Unable to reconnect to lobby");
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
