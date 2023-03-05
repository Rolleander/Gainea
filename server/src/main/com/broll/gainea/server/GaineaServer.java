package com.broll.gainea.server;

import com.broll.gainea.NetworkSetup;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.gainea.server.init.ServerSetup;
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

    public GaineaServer(String version) {
        com.esotericsoftware.minlog.Log.INFO();
        Log.info("Start Gainea Server " + version);
        server = new LobbyGameServer<>("GaineaServer", NetworkSetup::registerNetwork);
        server.setVersion(version);
        ServerSetup.setup(server);
        server.open();
        server.addListener(new ServerStatistic(server));
    }

    public void appendCLI() {
        LobbyServerCLI.open(server, nextTurn(), gameInfo(), giveCard());
    }

    private LobbyServerCLI.CliCommand giveCard() {
        return LobbyServerCLI.cmd("givecard", "gives current player a specific card", options -> {
            if (options.size() < 2) {
                System.err.println("givecard needs to be followed by a lobby id and card picture id");
                return;
            }
            int id = Integer.parseInt(options.get(0));
            int picId = Integer.parseInt(options.get(1));
            getGame(id).ifPresent(game -> {
                game.getCardStorage().getAllCards().stream().filter(it -> it.getPicture() == picId)
                        .findFirst().ifPresent(card ->
                                game.getCurrentPlayer().getCardHandler().receiveCard(card)
                        );
            });
        });
    }

    private LobbyServerCLI.CliCommand nextTurn() {
        return LobbyServerCLI.cmd("nextturn", "Ends the current turn", options -> {
            if (options.isEmpty()) {
                System.err.println("nextturn needs to be followed by a lobby id");
                return;
            }
            int id = Integer.parseInt(options.get(0));
            getGame(id).ifPresent(game -> {
                game.getBattleHandler().reset();
                game.getReactionHandler().getActionHandlers().getReactionActions().endTurn();
                print("Ended turn");
            });
        });
    }

    private LobbyServerCLI.CliCommand gameInfo() {
        return LobbyServerCLI.cmd("game", "Info about a running game", options -> {
            if (options.isEmpty()) {
                System.err.println("game needs to be followed by a lobby id");
                return;
            }
            int id = Integer.parseInt(options.get(0));
            getGame(id).ifPresent(game -> {
                LobbyData settings = game.getGameSettings();
                print("> Game Settings: Map:" + settings.getExpansionSetting().getName() + " Goals:" + settings.getGoalTypes().getName() + " PointLimit:" + settings.getPointLimit() + " MonsterCount:" + settings.getMonsterCount()
                        + " StartGoals:" + settings.getStartGoals() + " StartLocations:" + settings.getStartLocations());
                print("> Round:" + game.getRounds() + " Turn:" + (game.getCurrentTurn() + 1) + "/" + game.getAllPlayers().size() + " Player:" + (game.getCurrentTurn() >= 0 ? game.getCurrentPlayer().getServerPlayer().getName() : "None"));
                print("> State: ProcessingCore.Busy=" + game.getProcessingCore().isBusy() + " BattleHandler.Active=" + game.getBattleHandler().isBattleActive());
                print("> Game Objects [" + game.getObjects().size() + "]:");
                game.getObjects().forEach(object -> print(">> " + object.toString()));
                game.getAllPlayers().forEach(player -> {
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
