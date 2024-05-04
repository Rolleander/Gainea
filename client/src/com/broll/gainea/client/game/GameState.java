package com.broll.gainea.client.game;

import com.broll.gainea.Gainea;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_BoardEffect;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_GameStatistic;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Shop;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.networklib.client.impl.LobbyPlayer;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {

    public NT_Shop shop;
    private Gainea game;
    //game data:
    private int round;
    private int turn;
    private int pointLimit;
    private int roundLimit;
    private List<NT_BoardObject> objects;
    private List<NT_BoardEffect> effects;
    private List<NT_Player> players;
    private ClientMapContainer mapContainer;
    private MapObjectContainer mapObjectsContainer;
    private MapEffectContainer mapEffectContainer;
    //my player specific data:
    private List<NT_Goal> goals;
    private List<NT_Card> cards;
    private LobbyPlayer player;
    private List<GameStateListener> stateListeners = new ArrayList<>();
    private boolean playersTurn;
    private boolean actionsAllowed;
    private NT_GameStatistic statistic;

    public GameState(Gainea game) {
        this.game = game;
    }

    public void init(ExpansionSetting setting, int pointLimit, int roundLimit, LobbyPlayer player) {
        this.player = player;
        this.pointLimit = pointLimit;
        this.roundLimit = roundLimit;
        mapContainer = new ClientMapContainer(game, setting);
        mapObjectsContainer = new MapObjectContainer(this);
        mapEffectContainer = new MapEffectContainer(game);
        goals = new ArrayList<>();
        cards = new ArrayList<>();
        objects = new ArrayList<>();
        effects = new ArrayList<>();
    }

    public void addListener(GameStateListener listener) {
        this.stateListeners.add(listener);
    }

    public void playerTurnStart() {
        this.playersTurn = true;
        updateIdleState(false);
    }

    public void turnIdle() {
        if (playersTurn) {
            updateIdleState(true);
        }
    }

    public void playerTurnEnded() {
        this.playersTurn = false;
        updateIdleState(false);
    }

    public boolean isPlayersTurn() {
        return playersTurn;
    }

    public void updateIdleState(boolean idle) {
        actionsAllowed = idle;
        stateListeners.forEach(listener -> {
            if (idle) {
                listener.playerTurnIdle();
            } else {
                listener.gameBusy();
            }
        });
    }

    public boolean areActionsAllowed() {
        return actionsAllowed;
    }

    public void update(NT_BoardUpdate update) {
        this.round = update.round;
        this.turn = update.turn;
        this.objects = Lists.newArrayList(update.objects);
        this.players = Arrays.asList(update.players);
        this.effects = Lists.newArrayList(update.effects);
        updateMapObjects(0);
    }

    public void updateMapObjects(int effect) {
        mapObjectsContainer.update(effect, Streams.concat(objects.stream(), players.stream().flatMap(p -> Arrays.stream(p.units))).collect(Collectors.toList()));
        mapEffectContainer.update(this.effects);
        if (game.ui.inGameUI != null) {
            game.ui.inGameUI.getRequiredActionHandler().toFront();
        }
    }

    public void performAction(NT_Action action, PlayerPerformAction playerPerformAction) {
        updateIdleState(false);
        game.ui.inGameUI.action(action, playerPerformAction);
    }

    public void performOptionalAction(List<NT_Action> actions, PlayerPerformOptionalAction playerPerformAction) {
        game.ui.inGameUI.optionalActions(actions, playerPerformAction);
        updateIdleState(true);
    }

    public List<NT_Unit> getUnits(int playerId) {
        return Arrays.asList(getPlayer(playerId).units);
    }

    public NT_Player getPlayer(int id) {
        return players.stream().filter(it -> it.id == id).findFirst().orElse(null);
    }

    public List<NT_BoardObject> getObjects() {
        return objects;
    }

    public List<NT_BoardEffect> getEffects() {
        return effects;
    }

    public List<NT_Player> getPlayers() {
        return players;
    }

    public ClientMapContainer getMap() {
        return mapContainer;
    }

    public Gainea getContainer() {
        return game;
    }

    public List<NT_Card> getCards() {
        return cards;
    }

    public List<NT_Goal> getGoals() {
        return goals;
    }

    public MapObjectContainer getMapObjectsContainer() {
        return mapObjectsContainer;
    }

    public LobbyPlayer getPlayer() {
        return player;
    }

    public NT_GameStatistic getStatistic() {
        return statistic;
    }

    public void setStatistic(NT_GameStatistic nt) {
        this.statistic = nt;
    }

    public int getRound() {
        return round;
    }

    public int getTurn() {
        return turn;
    }

    public int getPointLimit() {
        return pointLimit;
    }

    public int getRoundLimit() {
        return roundLimit;
    }

}
