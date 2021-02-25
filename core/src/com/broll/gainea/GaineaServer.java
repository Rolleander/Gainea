package com.broll.gainea;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.LobbyFactory;
import com.broll.gainea.server.init.NetworkSetup;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.server.LobbyGameServer;
import com.broll.networklib.server.LobbyServerCLI;
import com.broll.networklib.server.impl.ServerLobby;
import com.esotericsoftware.minlog.Log;

import java.util.Optional;

public class GaineaServer {

    private LobbyGameServer<LobbyData, PlayerData> server;

    public static void main(String[] args) {
        GaineaServer server = new GaineaServer();
        server.openTestLobby();
        server.appendCLI();
    }

    public GaineaServer() {
        Log.INFO();
        //  Log.DEBUG();
        server = new LobbyGameServer<>("GaineaServer", NetworkSetup::registerNetwork);
        NetworkSetup.setup(server);
        server.open();
     /*   PlayerData data = new PlayerData();
        data.setReady(true);
        lobby.createBot("bot_hans", data).ifPresent(bot -> {
            bot.register(new BotSite<PlayerData>() {
                @PackageReceiver
                void rec(NT_LobbyKicked f) {

                }
            });
        });*/
    }

    public void openTestLobby() {
        ServerLobby<LobbyData, PlayerData> lobby = server.getLobbyHandler().openLobby("Testlobby");
        LobbyFactory.initLobby(lobby, ExpansionSetting.BASIC_GAME);
        lobby.setAutoClose(false);
    }

    public void appendCLI() {
        LobbyServerCLI.open(server, nextTurn(), gameInfo());
    }

    private LobbyServerCLI.CliCommand nextTurn() {
        return LobbyServerCLI.cmd("nextturn", "Ends the current turn", options -> {
            if (options.isEmpty()) {
                System.err.println("nextturn needs to be followed by a lobby id");
            }
            int id = Integer.parseInt(options.get(0));
            getGame(id).ifPresent(game -> {
                game.getReactionHandler().getActionHandlers().getReactionActions().endTurn();
                print("Ended turn");
            });
        });
    }

    private LobbyServerCLI.CliCommand gameInfo() {
        return LobbyServerCLI.cmd("game", "Info about a running game", options -> {
            if (options.isEmpty()) {
                System.err.println("game needs to be followed by a lobby id");
            }
            int id = Integer.parseInt(options.get(0));
            getGame(id).ifPresent(game -> {
                LobbyData settings = game.getGameSettings();
                print("> Game Settings: Map:" + settings.getExpansionSetting().getName() + " Goals:" + settings.getGoalTypes().getName() + " PointLimit:" + settings.getPointLimit() + " MonsterCount:" + settings.getMonsterCount()
                        + " StartGoals:" + settings.getStartGoals() + " StartLocations:" + settings.getStartLocations());

            });
        });
    }

    private void print(String text) {
        System.out.println(text);
    }

    private Optional<GameContainer> getGame(int lobbyId) {
        ServerLobby<LobbyData, PlayerData> lobby = server.getLobbyHandler().getLobby(lobbyId);
        if (lobby == null) {
            System.err.println("Lobby #" + lobbyId + " not found!");
        } else {
            GameContainer game = lobby.getData().getGame();
            if (game == null) {
                System.err.println("Lobby #" + lobbyId + " has no active game!");
            } else {
                return Optional.of(game);
            }
        }
        return Optional.empty();
    }
}
