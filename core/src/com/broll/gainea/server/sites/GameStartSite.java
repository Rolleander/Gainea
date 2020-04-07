package com.broll.gainea.server.sites;

import java.util.List;

import com.broll.gainea.net.NT_LoadedGame;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.actions.impl.PlaceUnitAction;
import com.broll.gainea.server.core.map.LocationPicker;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.server.ExpansionSetting;
import com.broll.gainea.server.LobbyData;
import com.broll.gainea.server.PlayerData;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.server.PackageRestriction;
import com.broll.networklib.server.RestrictionType;
import com.broll.networklib.server.impl.ServerLobby;

import java.util.stream.Collectors;

public class GameStartSite extends AbstractGameSite {

    private final static String PLAYER_LOADED = "PLAYER_LOADED";

    private final static String GAME_LOADING = "GAME_LOADING";

    private final static String PLAYER_START_LOCATIONS = "START_LOCATIONS";

    private final static String START_UNITS_PLACED = "START_UNITS_PLACED";

    private GameBoardSite gameBoardSite;

    public GameStartSite(GameBoardSite gameBoardSite) {
        this.gameBoardSite = gameBoardSite;
    }

    public void startGame() {
        ServerLobby<LobbyData, PlayerData> lobby = getLobby();
        LobbyData data = lobby.getData();
        ExpansionSetting expansionSetting = data.getExpansionSetting();
        GameContainer game = new GameContainer(expansionSetting, lobby.getPlayers());
        data.setGame(game);
        game.getData().put(GAME_LOADING, true);
        gameBoardSite.init(game);
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
        game.getData().put(START_UNITS_PLACED, 0);
        int playerCount = game.getPlayers().size();
        List<Area> startLocations = LocationPicker.pickRandom(game.getMap(), playerCount * startLocationsCount);
        for (Player player : game.getPlayers()) {
            List<Location> playerStartLocations = startLocations.stream().limit(startLocationsCount).collect(Collectors.toList());
            startLocations.removeAll(playerStartLocations);
            player.getData().put(PLAYER_START_LOCATIONS, playerStartLocations);
        }
    }

    private void placeUnit() {
        GameContainer game = getGame();
        int playerCount = getPlayersCount();
        int placed = getPlacedCount();
        int placingRound = placed / playerCount;
        Player player = getPlacingPlayer();
        List<Location> locations = (List<Location>) player.getData().get(PLAYER_START_LOCATIONS);
        BattleObject unitToPlace;
        if (placingRound == 0) {
            unitToPlace = player.getFraction().createCommander(null);
        } else {
            unitToPlace = player.getFraction().createSoldier(null);
        }
        String text = "Setze " + unitToPlace.getName() + " auf einen Startpunkt";
        ActionHandlers actionHandlers = game.getTurnBuilder().getActionHandlers();
        PlaceUnitAction placeUnitAction = (PlaceUnitAction) actionHandlers.getHandler(NT_Action_PlaceUnit.class);
        actionHandlers.getReactionResult().requireAction(player, new RequiredActionContext<>(placeUnitAction.placeUnit(unitToPlace, locations, this::placedUnit), text));
    }

    private void placedUnit(BattleObject battleObject, Location location) {
        GameContainer game = getGame();
        int startLocationsCount = getLobby().getData().getStartLocations();
        int placed = (Integer) game.getData().get(START_UNITS_PLACED);
        Player player = getPlacingPlayer();
        //remove selected location from start locations
        ((List<Location>) player.getData().get(PLAYER_START_LOCATIONS)).remove(location);
        placed++;
        game.getData().put(START_UNITS_PLACED, placed);
        if (placed < game.getPlayers().size() * startLocationsCount) {
            //next player places unit
            placeUnit();
        } else {
            //all start units placed, start first turn
            gameBoardSite.nextTurn();
        }
    }

    private int getPlacedCount() {
        return (Integer) getGame().getData().get(START_UNITS_PLACED);
    }

    private Player getPlacingPlayer() {
        int playerNr = getPlacedCount() % getPlayersCount();
        return getGame().getPlayers().get(playerNr);
    }

    @PackageReceiver
    @PackageRestriction(RestrictionType.LOBBY_LOCKED)
    public void playerLoaded(NT_LoadedGame loadedGame) {
        GameContainer game = getGame();
        if ((Boolean) game.getData().get(GAME_LOADING)) {
            getGamePlayer().getData().put(PLAYER_LOADED, true);
            if (allPlayersLoaded()) {
                gameLoaded();
            }
        }
    }

    private boolean allPlayersLoaded() {
        return getGame().getPlayers().stream().map(player -> player.getData().containsKey(PLAYER_LOADED)).reduce(true, Boolean::logicalAnd);
    }


}
