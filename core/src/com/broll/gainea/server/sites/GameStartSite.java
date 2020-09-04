package com.broll.gainea.server.sites;

import java.util.HashMap;
import java.util.List;

import com.broll.gainea.net.NT_LoadedGame;
import com.broll.gainea.server.core.ReactionResultHandler;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.actions.impl.PlaceUnitAction;
import com.broll.gainea.server.core.map.LocationPicker;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.init.ExpansionSetting;
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
import com.broll.networklib.server.impl.ServerLobby;

import java.util.Map;
import java.util.stream.Collectors;

public class GameStartSite extends AbstractGameSite {

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

    public void startGame() {
        ServerLobby<LobbyData, PlayerData> lobby = getLobby();
        LobbyData data = lobby.getData();
        ExpansionSetting expansionSetting = data.getExpansionSetting();
        GameContainer game = new GameContainer(expansionSetting, lobby.getPlayers());
        data.setGame(game);
        game.initHandlers(new ReactionResultHandler(game, lobby));
        gameStart.loading = true;
        gameStart.startUnitsPlaced = 0;
        gameStart.playerData = new HashMap<>();
        lobby.getPlayers().forEach(p -> gameStart.playerData.put(p.getData().getGamePlayer(), new PlayerStartData()));
        lobby.setLocked(true);
        lobby.sendToAllTCP(game.start());
    }

    private void gameLoaded() {
        //give random goals and start locations to everyone
        drawStartLocations();
        assignGoals();
        //start  placing units after delay
        getGame().schedule(5000, () -> placeUnit());
    }

    private void assignGoals() {
        GameContainer game = getGame();
        int startGoalsCount = getLobby().getData().getStartGoals();
        for (int i = 0; i < startGoalsCount; i++) {
            game.getPlayers().forEach(game.getGoalStorage()::assignNewRandomGoal);
        }
    }

    private void drawStartLocations() {
        GameContainer game = getGame();
        int startLocationsCount = getLobby().getData().getStartLocations();
        int playerCount = game.getPlayers().size();
        List<Area> startLocations = LocationPicker.pickRandom(game.getMap(), playerCount * startLocationsCount);
        for (Player player : game.getPlayers()) {
            List<Location> playerStartLocations = startLocations.stream().limit(startLocationsCount).collect(Collectors.toList());
            startLocations.removeAll(playerStartLocations);
            gameStart.playerData.get(player).startLocations = playerStartLocations;
        }
    }

    private void placeUnit() {
        GameContainer game = getGame();
        int playerCount = getPlayersCount();
        int placingRound = gameStart.startUnitsPlaced / playerCount;
        Player player = getPlacingPlayer();
        List<Location> locations = gameStart.playerData.get(player).startLocations;
        BattleObject unitToPlace;
        if (placingRound == 0) {
            unitToPlace = player.getFraction().createCommander(null);
        } else {
            unitToPlace = player.getFraction().createSoldier(null);
        }
        String text = "Setze " + unitToPlace.getName() + " auf einen Startpunkt";
        ActionHandlers actionHandlers = game.getTurnBuilder().getActionHandlers();
        PlaceUnitAction placeUnitAction = actionHandlers.getHandler(PlaceUnitAction.class);
        actionHandlers.getReactionActions().requireAction(player, new RequiredActionContext<>(placeUnitAction.placeUnit(unitToPlace, locations, this::placedUnit), text));
    }

    private void placedUnit(BattleObject battleObject, Location location) {
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
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    public void playerLoaded(NT_LoadedGame loadedGame) {
        if (gameStart.loading) {
            gameStart.playerData.get(getGamePlayer()).loaded = true;
            if (allPlayersLoaded()) {
                gameLoaded();
            }
        }
    }

    private boolean allPlayersLoaded() {
        return gameStart.playerData.values().stream().map(it -> it.loaded).reduce(true, Boolean::logicalAnd);
    }


}
