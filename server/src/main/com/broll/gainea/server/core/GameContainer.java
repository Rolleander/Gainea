package com.broll.gainea.server.core;

import com.broll.gainea.net.NT_BoardEffect;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_GameOver;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_ReconnectGame;
import com.broll.gainea.net.NT_StartGame;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.ReactionActions;
import com.broll.gainea.server.core.actions.ReactionHandler;
import com.broll.gainea.server.core.actions.TurnBuilder;
import com.broll.gainea.server.core.battle.BattleHandler;
import com.broll.gainea.server.core.cards.CardStorage;
import com.broll.gainea.server.core.goals.GoalStorage;
import com.broll.gainea.server.core.map.MapContainer;
import com.broll.gainea.server.core.objects.MapEffect;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.MonsterFactory;
import com.broll.gainea.server.core.objects.buffs.BuffProcessor;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.player.PlayerFactory;
import com.broll.gainea.server.core.processing.GameUpdateReceiverProxy;
import com.broll.gainea.server.core.processing.ProcessingCore;
import com.broll.gainea.server.core.stats.GameStatistic;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.server.impl.ServerLobby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameContainer {

    private final static Logger Log = LoggerFactory.getLogger(GameContainer.class);
    private MapContainer map;
    private List<Player> players;
    private List<MapObject> objects = new ArrayList<>();
    private List<MapEffect> effects = new ArrayList<>();
    private Map<Integer, ActionContext> actions = new HashMap<>();
    private int rounds = 1;
    private int currentTurn = -1;
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
    private BuffProcessor buffProcessor;
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
        this.buffProcessor = new BuffProcessor(this);
        this.statistic = new GameStatistic(this);
        this.gameUpdateReceiver.register(new TurnEvents(this));
        this.gameUpdateReceiver.register(statistic);
    }

    public void initHandlers(ReactionActions reactionResult) {
        ActionHandlers actionHandlers = new ActionHandlers(this, reactionResult);
        this.reactionHandler = new ReactionHandler(this, actionHandlers);
        this.processingCore = new ProcessingCore(this, reactionHandler::finishedProcessing, lobby);
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
        action.getAction().actionId = (short) actionCounter;
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

    public NT_StartGame start(Player player) {
        NT_StartGame nt = new NT_StartGame();
        fillUpdate(nt);
        nt.expansionsSetting = map.getExpansionSetting().ordinal();
        nt.pointLimit = lobby.getData().getPointLimit();
        nt.roundLimit = lobby.getData().getRoundLimit();
        nt.cards = player.getCardHandler().ntCards();
        return nt;
    }

    public void end() {
        Log.trace("Gamend called");
        gameOver = true;
        processingCore.ensureExecute(() -> {
            processingCore.shutdown();
            Log.trace("Process gameend");
            NT_GameOver gameOver = new NT_GameOver();
            fillUpdate(gameOver);
            reactionHandler.getActionHandlers().getReactionActions().sendGameUpdate(gameOver);
            statistic.sendStatistic();
            lobby.unlock();
            lobby.getData().setGame(null);
        }, 2000);
    }

    public NT_ReconnectGame reconnect(Player player) {
        NT_ReconnectGame nt = new NT_ReconnectGame();
        fillUpdate(nt);
        nt.expansionsSetting = map.getExpansionSetting().ordinal();
        nt.cards = player.getCardHandler().ntCards();
        nt.goals = player.getGoalHandler().ntGoals();
        nt.pointLimit = lobby.getData().getPointLimit();
        nt.roundLimit = lobby.getData().getRoundLimit();
        return nt;
    }

    private void fillUpdate(NT_BoardUpdate update) {
        update.round = (short) rounds;
        update.turn = (short) getCurrentTurn();
        update.players = players.stream().map(Player::nt).toArray(NT_Player[]::new);
        update.objects = objects.stream().map(MapObject::nt).toArray(NT_BoardObject[]::new);
        update.effects = effects.stream().map(MapEffect::nt).toArray(NT_BoardEffect[]::new);
    }

    public NT_BoardUpdate nt() {
        NT_BoardUpdate board = new NT_BoardUpdate();
        fillUpdate(board);
        return board;
    }

    public synchronized Player nextTurn() {
        actions.clear();
        currentTurn++;
        if (GameUtils.noActivePlayersRemaining(this)) {
            return null;
        }
        if (currentTurn >= players.size()) {
            currentTurn = 0;
            rounds++;
            if (GameUtils.isGameEnd(this)) {
                return null;
            }
        }
        return players.get(currentTurn);
    }

    public MapContainer getMap() {
        return map;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public int getRounds() {
        return rounds;
    }

    public List<Player> getActivePlayers() {
        return players.stream().filter(Player::isActive).collect(Collectors.toList());
    }

    public Player getCurrentPlayer() {
        return players.get(currentTurn);
    }

    public List<Player> getAllPlayers() {
        return players;
    }

    public boolean isPlayersTurn(Player player) {
        return players.indexOf(player) == currentTurn;
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

    public BuffProcessor getBuffProcessor() {
        return buffProcessor;
    }

    public List<MapEffect> getEffects() {
        return effects;
    }
}
