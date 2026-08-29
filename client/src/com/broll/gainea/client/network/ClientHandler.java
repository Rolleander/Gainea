package com.broll.gainea.client.network;

import com.badlogic.gdx.Gdx;
import com.broll.gainea.Gainea;
import com.broll.gainea.MessageRegistry;
import com.broll.gainea.client.network.sites.*;
import com.broll.mpnll.client.MpnllClient;
import com.broll.mpnll.client.async.ClientFuture;
import com.broll.mpnll.client.lobby.Lobby;
import com.broll.mpnll.client.lobby.LobbyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ClientHandler {
    private static final Logger Log = LoggerFactory.getLogger(ClientHandler.class);
    private final MpnllClient client;
    private boolean clientBusy;
    private IClientListener clientListener;
    private final Gainea game;

    public ClientHandler(Gainea game) {
        this.game = game;
        client = new MpnllClient();
        client.registerMessages(MessageRegistry::register);
        initGameSites(game);
    }

    private void initGameSites(Gainea game) {
        client.clearSites();
        GameActionSite actionSite = new GameActionSite();
        java.util.Arrays.asList(actionSite, new GameBattleSite(), new GameBoardSite(), new GameEventSite(),
                new GameStateSite(), new GameTurnSite(actionSite), new GameVoteSite()).forEach(site -> {
            site.init(game);
            client.addSite(site);
        });
    }

    public void setClientListener(IClientListener clientListener) { this.clientListener = clientListener; }

    public void listLobbies(String ip) {
        clientExecute(() -> client.listLobbies(ip), result -> {
            Lobby reconnected = client.getConnectedLobby();
            if (reconnected != null) listenLobbyUpdates(reconnected);
            else clientListener.discoveredLobbies(result);
        }, "Failed to list lobbies");
    }

    public void joinLobby(String playerName, LobbyInfo lobby) {
        clientExecute(() -> client.joinLobby(lobby, playerName), this::connectedLobby, "Unable to join lobby");
    }

    public void createLobby(String playerName) {
        clientExecute(() -> client.createLobby(playerName, null), this::connectedLobby, "Unable to create lobby");
    }

    private void connectedLobby(Lobby lobby) {
        listenLobbyUpdates(lobby);
        runOnGdx(() -> clientListener.connectedLobby(lobby));
    }

    public void reconnectCheck() {
        clientExecute(client::reconnectCheck, lobby -> { if (lobby != null) listenLobbyUpdates(lobby); }, "Unable to reconnect to lobby");
    }

    private void listenLobbyUpdates(Lobby lobby) { lobby.addLobbyListener(new ClientLobbyListener(game)); }
    private void runOnGdx(Runnable runnable) { Gdx.app.postRunnable(runnable); }

    private synchronized <T> void clientExecute(Supplier<ClientFuture<T>> operation, Consumer<T> resultHandler, String errorMessage) {
        if (clientBusy) { Log.info("client busy"); return; }
        clientBusy = true;
        runOnGdx(() -> clientListener.loadingStateUpdate(true));
        try {
            operation.get().onSuccess(result -> runOnGdx(() -> {
                finishOperation();
                resultHandler.accept(result);
            })).onFailure(error -> runOnGdx(() -> {
                finishOperation();
                Log.error(errorMessage, error);
                String detail = error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
                clientListener.connectionFailure(errorMessage + "\n (" + detail + ")");
            }));
        } catch (Throwable error) {
            finishOperation();
            Log.error(errorMessage, error);
            clientListener.connectionFailure(errorMessage + "\n (" + error.getMessage() + ")");
        }
    }

    private synchronized void finishOperation() {
        clientBusy = false;
        clientListener.loadingStateUpdate(false);
    }

    public void shutdown() { client.shutdown(); }
    public MpnllClient getClient() { return client; }
}
