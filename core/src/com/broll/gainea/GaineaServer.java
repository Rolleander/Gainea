package com.broll.gainea;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.bot.RandomBot;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.LobbyFactory;
import com.broll.gainea.server.init.NetworkSetup;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.server.LobbyGameServer;
import com.broll.networklib.server.LobbyServerCLI;
import com.broll.networklib.server.impl.ServerLobby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Collectors;

public class GaineaServer {

    private final static Logger Log = LoggerFactory.getLogger(GaineaServer.class);

    private LobbyGameServer<LobbyData, PlayerData> server;

    public GaineaServer() {
        com.esotericsoftware.minlog.Log.INFO();
        server = new LobbyGameServer<>("GaineaServer", NetworkSetup::registerNetwork);
        NetworkSetup.setup(server);
        server.open();
    }

    public void openTestLobby() {
        ServerLobby<LobbyData, PlayerData> lobby = server.getLobbyHandler().openLobby("Testlobby");
        LobbyFactory.initLobby(lobby, ExpansionSetting.BASIC_GAME);
        lobby.setAutoClose(false);
        PlayerData data = new PlayerData();
        data.setReady(true);
        lobby.createBot("bot_hans", data).ifPresent(bot -> {
            bot.register(new RandomBot());
        });
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
                print("> Round:" + game.getRounds() + " Turn:" + (game.getCurrentPlayer() + 1) + "/" + game.getPlayers().size() + " Player:" + (game.getCurrentPlayer() >= 0 ? game.getPlayers().get(game.getCurrentPlayer()) : "None"));
                print("> State: ProcessingCore.Busy=" + game.getProcessingCore().isBusy() + " BattleHandler.Active=" + game.getBattleHandler().isBattleActive());
                print("> Game Objects [" + game.getObjects().size() + "]:");
                game.getObjects().forEach(object -> print(">> " + object.toString()));
                game.getPlayers().forEach(player -> {
                    print("> Player [" + player.toString() + " Points:" + player.getGoalHandler().getScore()
                            + " Stars:" + player.getGoalHandler().getStars() + " Online=" + player.getServerPlayer().isOnline() + "]:");
                    print(">> Goals (" + player.getGoalHandler().getGoals().size() + "):" + player.getGoalHandler().getGoals().stream().map(Object::toString).collect(Collectors.joining(",")));
                    print(">> Cards (" + player.getCardHandler().getCards().size() + "): " + player.getCardHandler().getCards().stream().map(Object::toString).collect(Collectors.joining(",")));
                    print(">> Controlled Locations: [" + player.getControlledLocations().stream().map(Location::toString).collect(Collectors.joining(",")) + "]");
                    print(">> Objects [" + player.getUnits().size() + "]:");
                    player.getUnits().forEach(object -> print(">>> " + object.toString()));
                });
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
