package com.broll.gainea.server.sites;

import java.util.HashMap;
import java.util.List;

import com.broll.gainea.net.NT_LoadedGame;
import com.broll.gainea.server.core.ReactionResultHandler;
import com.broll.gainea.server.core.actions.required.PlaceUnitAction;
import com.broll.gainea.server.core.map.LocationPicker;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.ProcessingUtils;
import com.broll.gainea.server.core.utils.UnitControl;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.server.Autoshared;
import com.broll.networklib.server.ConnectionRestriction;
import com.broll.networklib.server.RestrictionType;
import com.broll.networklib.server.ShareLevel;
import com.broll.networklib.server.impl.ConnectionSite;
import com.broll.networklib.server.impl.ServerLobby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.stream.Collectors;

public class GameStartSite extends AbstractGameSite {
    private final static Logger Log = LoggerFactory.getLogger(GameStartSite.class);

    private class GameStartData {
        boolean loading = true;
        int startUnitsPlaced = 0;
        Map<Player, PlayerStartData> playerData;
    }

    private class PlayerStartData {
        boolean loaded = false;
        List<Location> startLocations;
    }

    @Autoshared(ShareLevel.LOBBY)
    private GameStartData gameStart;

    private final static int DELAY = 1000;

    public void startGame() {
        ServerLobby<LobbyData, PlayerData> lobby = getLobby();
        lobby.chat(null, "Starte Spiel...");
        GameContainer game = new GameContainer(lobby);
        game.initHandlers(new ReactionResultHandler(game, lobby));
        gameStart.loading = true;
        gameStart.startUnitsPlaced = 0;
        gameStart.playerData = new HashMap<>();
        lobby.getPlayers().forEach(p -> gameStart.playerData.put(p.getData().getGamePlayer(), new PlayerStartData()));
        lobby.sendToAllTCP(game.start());
        Log.info("Started game in lobby " + lobby.getName());
    }

    private void gameLoaded() {
        gameStart.loading = false;
        getGame().getProcessingCore().execute(() -> {
            //spawn monsters
            int totalMonsters = getLobby().getData().getMonsterCount() * getGame().getMap().getActiveExpansionTypes().size();
            Log.info("Spawn Monsters: " + totalMonsters);
            UnitControl.spawnMonsters(getGame(), totalMonsters);
            ProcessingUtils.pause(DELAY);
            //give random goals and start locations to everyone
            drawStartLocations();
            assignGoals();
            //players start placing units
            placeUnit();
        }, DELAY);
    }

    private void assignGoals() {
        Log.info("Assign Goals");
        GameContainer game = getGame();
        int startGoalsCount = getLobby().getData().getStartGoals();
        for (int i = 0; i < startGoalsCount; i++) {
            game.getPlayers().forEach(player -> {
                Log.debug("Add goal to " + player);
                game.getGoalStorage().assignNewRandomGoal(player);
                ProcessingUtils.pause(DELAY);
            });
        }
    }

    private void drawStartLocations() {
        Log.info("Draw start locations");
        GameContainer game = getGame();
        int startLocationsCount = getLobby().getData().getStartLocations();
        int playerCount = game.getPlayers().size();
        List<Area> startLocations = LocationPicker.pickRandomEmpty(game.getMap(), playerCount * startLocationsCount);
        for (Player player : game.getPlayers()) {
            List<Location> playerStartLocations = startLocations.stream().limit(startLocationsCount).collect(Collectors.toList());
            startLocations.removeAll(playerStartLocations);
            gameStart.playerData.get(player).startLocations = playerStartLocations;
        }
    }

    private void placeUnit() {
        Log.info("placeUnit");
        GameContainer game = getGame();
        int playerCount = getPlayersCount();
        int placingRound = gameStart.startUnitsPlaced / playerCount;
        Player player = getPlacingPlayer();
        List<Location> locations = gameStart.playerData.get(player).startLocations;
        BattleObject unitToPlace;
        if (placingRound == 0) {
            unitToPlace = player.getFraction().createCommander();
        } else {
            unitToPlace = player.getFraction().createSoldier();
        }
        String text = "Setze " + unitToPlace.getName() + " auf einen Startpunkt";
        ActionHandlers actionHandlers = game.getReactionHandler().getActionHandlers();
        PlaceUnitAction placeUnitAction = actionHandlers.getHandler(PlaceUnitAction.class);
        Pair<BattleObject, Location> result = placeUnitAction.placeUnit(player, unitToPlace, locations, text);
        placedUnit(result.getRight());
    }

    private void placedUnit(Location location) {
        Log.info("player placedUnit");
        GameContainer game = getGame();
        int startLocationsCount = getLobby().getData().getStartLocations();
        Player player = getPlacingPlayer();
        //remove selected location from start locations
        gameStart.playerData.get(player).startLocations.remove(location);
        gameStart.startUnitsPlaced++;
        if (gameStart.startUnitsPlaced < game.getPlayers().size() * startLocationsCount) {
            //next player places unit
            placeUnit();
        } else {
            //all start units placed, start first turn
            nextTurn();
        }
    }

    private Player getPlacingPlayer() {
        int playerNr = gameStart.startUnitsPlaced % getPlayersCount();
        return getGame().getPlayers().get(playerNr);
    }

    @PackageReceiver
    //   @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    public void playerLoaded(NT_LoadedGame loadedGame) {
        if (gameStart.loading) {
            Log.info(getGamePlayer() + " loaded game!");
            gameStart.playerData.get(getGamePlayer()).loaded = true;
            if (allPlayersLoaded()) {
                Log.info("All players loaded game, start with init!");
                gameLoaded();
            }
        }
    }

    private boolean allPlayersLoaded() {
        return gameStart.playerData.values().stream().map(it -> it.loaded).reduce(true, Boolean::logicalAnd);
    }


}
