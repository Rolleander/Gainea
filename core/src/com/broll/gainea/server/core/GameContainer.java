package com.broll.gainea.server.core;

import com.broll.gainea.net.NT_GameOver;
import com.broll.gainea.net.NT_ReconnectGame;
import com.broll.gainea.net.NT_StartGame;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.actions.ReactionActions;
import com.broll.gainea.server.core.battle.BattleHandler;
import com.broll.gainea.server.core.cards.CardStorage;
import com.broll.gainea.server.core.goals.GoalStorage;
import com.broll.gainea.server.core.map.MapContainer;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.MonsterFactory;
import com.broll.gainea.server.core.objects.buffs.BuffDurationProcessor;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.player.PlayerFactory;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverProxy;
import com.broll.gainea.server.core.processing.ProcessingCore;
import com.broll.gainea.server.core.stats.GameStatistic;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.ReactionHandler;
import com.broll.gainea.server.core.actions.TurnBuilder;
import com.broll.gainea.server.core.map.Location;
import com.broll.networklib.server.impl.ServerLobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameContainer {

    private MapContainer map;
    private List<Player> players;
    private List<MapObject> objects = new ArrayList<>();
    private Map<Integer, ActionContext> actions = new HashMap<>();
    private int turns = 0;
    private int currentPlayer = -1;
    private int actionCounter = 0;
    private int boardObjectCounter = 0;
    private ReactionHandler reactionHandler;
    private TurnBuilder turnBuilder;
    private BattleHandler battleHandler;
    private GoalStorage goalStorage;
    private CardStorage cardStorage;
    private ProcessingCore processingCore;
    private MonsterFactory monsterFactory;
    private GameUpdateReceiverProxy gameUpdateReceiver;
    private BuffDurationProcessor buffDurationProcessor;
    private ServerLobby<LobbyData, PlayerData> lobby;
    private GameStatistic statistic;
    private boolean gameOver = false;

    public GameContainer(ServerLobby<LobbyData, PlayerData> lobby) {
        this.lobby = lobby;
        lobby.getData().setGame(this);
        this.gameUpdateReceiver = new GameUpdateReceiverProxy();
        this.map = new MapContainer(lobby.getData().getExpansionSetting());
        this.players = PlayerFactory.create(this, lobby.getPlayers());
        this.monsterFactory = new MonsterFactory();
        this.statistic = new GameStatistic(this);
        this.buffDurationProcessor = new BuffDurationProcessor(this);
        this.gameUpdateReceiver.register(new TurnEvents(this));
    }

    public void initHandlers(ReactionActions reactionResult) {
        ActionHandlers actionHandlers = new ActionHandlers(this, reactionResult);
        this.reactionHandler = new ReactionHandler(this, actionHandlers);
        this.processingCore = new ProcessingCore(this, reactionHandler::finishedProcessing);
        this.turnBuilder = new TurnBuilder(this, actionHandlers);
        this.battleHandler = new BattleHandler(this, reactionResult);
        this.goalStorage = new GoalStorage(this, actionHandlers, lobby.getData().getGoalTypes());
        this.cardStorage = new CardStorage(this, actionHandlers);
    }

    public synchronized int newObjectId() {
        boardObjectCounter++;
        return boardObjectCounter;
    }

    public synchronized void pushAction(ActionContext action) {
        action.getAction().actionId = actionCounter;
        actions.put(actionCounter, action);
        actionCounter++;
    }

    public synchronized ActionContext getAction(int id) {
        return actions.get(id);
    }

    public synchronized void consumeAction(int id) {
        actions.remove(id);
    }

    public void clearActions() {
        actions.clear();
    }

    public NT_StartGame start() {
        NT_StartGame startGame = new NT_StartGame();
        fillUpdate(startGame);
        startGame.expansionsSetting = map.getExpansionSetting().ordinal();
        return startGame;
    }

    public void end() {
        processingCore.execute(() -> {
            NT_GameOver gameOver = new NT_GameOver();
            fillUpdate(gameOver);
            reactionHandler.getActionHandlers().getReactionActions().sendGameUpdate(gameOver);
            lobby.setLocked(false);
            lobby.getData().setGame(null);
        }, 2000);
        gameOver = true;
        processingCore.shutdown();
    }

    public NT_ReconnectGame reconnect(Player player) {
        NT_ReconnectGame reconnectGame = new NT_ReconnectGame();
        fillUpdate(reconnectGame);
        reconnectGame.expansionsSetting = map.getExpansionSetting().ordinal();
        reconnectGame.cards = player.getCardHandler().ntCards();
        reconnectGame.goals = player.getGoalHandler().ntGoals();
        return reconnectGame;
    }

    private void fillUpdate(NT_BoardUpdate update) {
        update.turns = turns;
        update.players = players.stream().map(Player::nt).toArray(NT_Player[]::new);
        update.objects = objects.stream().map(MapObject::nt).toArray(NT_BoardObject[]::new);
    }

    public NT_BoardUpdate nt() {
        NT_BoardUpdate board = new NT_BoardUpdate();
        fillUpdate(board);
        return board;
    }

    public synchronized Player nextTurn() {
        actions.clear();
        currentPlayer++;
        if (currentPlayer >= players.size()) {
            currentPlayer = 0;
            statistic.nextTurn();
            turns++;
        }
        return players.get(currentPlayer);
    }

    public MapContainer getMap() {
        return map;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getTurns() {
        return turns;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean isPlayersTurn(Player player) {
        return players.indexOf(player) == currentPlayer;
    }

    public ReactionHandler getReactionHandler() {
        return reactionHandler;
    }

    public TurnBuilder getTurnBuilder() {
        return turnBuilder;
    }

    public BattleHandler getBattleHandler() {
        return battleHandler;
    }

    public List<MapObject> getObjects() {
        return objects;
    }

    public GoalStorage getGoalStorage() {
        return goalStorage;
    }

    public CardStorage getCardStorage() {
        return cardStorage;
    }

    public ProcessingCore getProcessingCore() {
        return processingCore;
    }

    public MonsterFactory getMonsterFactory() {
        return monsterFactory;
    }

    public GameUpdateReceiverProxy getUpdateReceiver() {
        return gameUpdateReceiver;
    }

    public LobbyData getGameSettings() {
        return lobby.getData();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public BuffDurationProcessor getBuffDurationProcessor() {
        return buffDurationProcessor;
    }
}
