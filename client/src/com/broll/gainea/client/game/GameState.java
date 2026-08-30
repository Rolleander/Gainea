package com.broll.gainea.client.game;

import com.broll.gainea.Gainea;
import com.broll.gainea.net.proto.NT_Action;
import com.broll.gainea.net.proto.NT_BoardEffect;
import com.broll.gainea.net.proto.NT_BoardObject;
import com.broll.gainea.net.proto.NT_BoardUpdate;
import com.broll.gainea.net.proto.NT_Card;
import com.broll.gainea.net.proto.NT_GameStatistic;
import com.broll.gainea.net.proto.NT_Lib;
import com.broll.gainea.net.proto.NT_Player;
import com.broll.gainea.net.proto.NT_Shop;
import com.broll.mpnll.client.lobby.User;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    public NT_Shop shop;
    public NT_Lib library;

    private Gainea game;
    //game data:
    private int round;
    private int turn;
    private int pointLimit;
    private int roundLimit;
    private List<BoardObjectState> objects;
    private List<NT_BoardEffect> effects;
    private List<PlayerState> players;
    private ClientMapContainer mapContainer;
    private MapObjectContainer mapObjectsContainer;
    private MapEffectContainer mapEffectContainer;
    //my player specific data:
    private List<GoalState> goals;
    private List<NT_Card> cards;
    private GamePlayer player;
    private List<GameStateListener> stateListeners = new ArrayList<>();
    private boolean playersTurn;
    private boolean actionsAllowed;
    private NT_GameStatistic statistic;

    public GameState(Gainea game) {
        this.game = game;
    }

    public void init(List<com.broll.gainea.net.proto.NT_Expansion> expansions, int pointLimit, int roundLimit, User player) {
        this.player = GamePlayer.connected(player);
        initState(expansions, pointLimit, roundLimit);
    }

    public void initOffline(List<com.broll.gainea.net.proto.NT_Expansion> expansions, int pointLimit, int roundLimit, int playerId) {
        this.player = GamePlayer.offline(playerId);
        initState(expansions, pointLimit, roundLimit);
    }

    private void initState(List<com.broll.gainea.net.proto.NT_Expansion> expansions, int pointLimit, int roundLimit) {
        this.pointLimit = pointLimit;
        this.roundLimit = roundLimit;
        mapContainer = new ClientMapContainer(game, expansions);
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
        update(update.getRound(), update.getTurn(), update.getObjectsList(), update.getPlayersList(), update.getEffectsList());
    }

    private void update(int round, int turn, List<NT_BoardObject> objects, List<NT_Player> players, List<NT_BoardEffect> effects) {
        this.round = round;
        this.turn = turn;
        this.objects = new ArrayList<>();
        objects.forEach(value -> this.objects.add(new BoardObjectState(value)));
        this.players = new ArrayList<>();
        players.forEach(value -> this.players.add(new PlayerState(value)));
        this.effects = new ArrayList<>(effects);
        updateMapObjects(0);
    }

    public void updateMapObjects(int effect) {
        List<BoardObjectState> allObjects = new ArrayList<>(objects);
        players.forEach(player -> allObjects.addAll(player.units()));
        mapObjectsContainer.update(effect, allObjects);
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

    public List<BoardObjectState> getUnits(int playerId) {
        return getPlayer(playerId).units();
    }

    public PlayerState getPlayer(int id) {
        return players.stream().filter(it -> it.id() == id).findFirst().orElse(null);
    }

    public List<BoardObjectState> getObjects() {
        return objects;
    }

    public List<NT_BoardEffect> getEffects() {
        return effects;
    }

    public List<PlayerState> getPlayers() {
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

    public List<GoalState> getGoals() {
        return goals;
    }

    public MapObjectContainer getMapObjectsContainer() {
        return mapObjectsContainer;
    }

    public GamePlayer getPlayer() {
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
