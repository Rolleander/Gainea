package com.broll.gainea.client;

import com.badlogic.gdx.Gdx;
import com.broll.gainea.server.init.NetworkSetup;
import com.broll.networklib.client.ClientSite;
import com.broll.networklib.client.LobbyGameClient;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.tasks.DiscoveredLobbies;
import com.broll.networklib.network.INetworkRequestAttempt;
import com.broll.networklib.site.SiteReceiver;
import com.esotericsoftware.minlog.Log;

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

    public ClientHandler() {
        client = new LobbyGameClient(NetworkSetup::registerNetwork);
        client.setSiteReceiver(new SiteReceiver<ClientSite, com.broll.networklib.client.GameClient.ClientConnection>() {
            @Override
            public void receive(com.broll.networklib.client.GameClient.ClientConnection context, ClientSite site, Method receiver, Object object) {
                //perform operations from received packages on main thread
                runOnGdx(() -> super.receive(context, site, receiver, object));
            }
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
        clientExecute(() -> client.reconnectCheck(), this::connectedLobby, "Unable to reconnect to lobby");
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
                clientListener.connectionFailure(errorMessage);
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

}
