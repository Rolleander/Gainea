package com.broll.gainea.server;

import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.server.ILobbyServerListener;
import com.broll.networklib.server.LobbyGameServer;
import com.broll.networklib.server.impl.Player;
import com.broll.networklib.server.impl.ServerLobby;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

public class ServerStatistic implements ILobbyServerListener<LobbyData, PlayerData> {
    private final static Logger Log = LoggerFactory.getLogger(ServerStatistic.class);
    private final static String STATISTIC_FILE = "stats.json";
    private LobbyGameServer<LobbyData, PlayerData> server;

    private File file = new File(STATISTIC_FILE);

    public ServerStatistic(LobbyGameServer<LobbyData, PlayerData> server) {
        this.server = server;
        writeStatistic();
    }

    private void writeStatistic() {
        CompletableFuture.runAsync(() -> {
            JSONObject json = new JSONObject();
            Iterator<ServerLobby<LobbyData, PlayerData>> iterator = server.getLobbyHandler().getLobbies().iterator();
            int players = 0;
            int games = 0;
            while (iterator.hasNext()) {
                ServerLobby<LobbyData, PlayerData> lobby = iterator.next();
                players += lobby.getPlayerCount();
                if (lobby.getData().getGame() != null) {
                    games++;
                }
            }
            json.put("players", players);
            json.put("games", games);
            try {
                FileUtils.write(file, json.toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                Log.error("Failed to write " + STATISTIC_FILE, e);
            }
        });
    }

    @Override
    public void lobbyOpened(ServerLobby<LobbyData, PlayerData> lobby) {
        lobby.getData().setGameStartListener(this::writeStatistic);
    }

    @Override
    public void playerJoined(ServerLobby<LobbyData, PlayerData> lobby, Player<PlayerData> player) {
        writeStatistic();
    }

    @Override
    public void playerLeft(ServerLobby<LobbyData, PlayerData> lobby, Player<PlayerData> player) {
        writeStatistic();
    }

    @Override
    public void playerDisconnected(ServerLobby<LobbyData, PlayerData> lobby, Player<PlayerData> player) {

    }

    @Override
    public void playerReconnected(ServerLobby<LobbyData, PlayerData> lobby, Player<PlayerData> player) {

    }

    @Override
    public void lobbyClosed(ServerLobby<LobbyData, PlayerData> lobby) {
        writeStatistic();
    }
}
