package com.broll.gainea.server.core;

import com.broll.gainea.net.NT_StartGame;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.actions.ReactionActions;
import com.broll.gainea.server.core.battle.BattleHandler;
import com.broll.gainea.server.core.goals.GoalStorage;
import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.server.core.map.AreaID;
import com.broll.gainea.server.core.map.Continent;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.Island;
import com.broll.gainea.server.core.map.IslandID;
import com.broll.gainea.server.core.map.MapContainer;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.player.PlayerFactory;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.server.ExpansionSetting;
import com.broll.gainea.server.PlayerData;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.ReactionHandler;
import com.broll.gainea.server.core.actions.TurnBuilder;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.MapFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private Map<String, Object> data = new HashMap<>();
    private BattleHandler battleHandler;
    private ScheduledExecutorService executor;
    private GoalStorage goalStorage;

    public GameContainer(ExpansionSetting setting, Collection<com.broll.networklib.server.impl.Player<PlayerData>> players) {
        this.executor = Executors.newScheduledThreadPool(3);
        this.map =new MapContainer(setting);
        this.players = players.stream().map(player -> PlayerFactory.create(this, player)).collect(Collectors.toList());
    }

    public void initHandlers(ReactionActions reactionResult) {
        ActionHandlers actionHandlers = new ActionHandlers(this, reactionResult);
        reactionHandler = new ReactionHandler(this, actionHandlers);
        turnBuilder = new TurnBuilder(this, actionHandlers);
        this.battleHandler = new BattleHandler(this, reactionResult);
        this.goalStorage = new GoalStorage(this, actionHandlers);
    }

    public synchronized int newObjectId() {
        boardObjectCounter++;
        return boardObjectCounter;
    }

    public void moveObject(MapObject object, Location target) {
        if (object.getLocation() != null) {
            object.getLocation().getInhabitants().remove(object);
        }
        object.setLocation(target);
        target.getInhabitants().add(object);
    }

    public void schedule(int inMilliseconds, Runnable runnable) {
        executor.schedule(runnable, inMilliseconds, TimeUnit.MILLISECONDS);
    }

    public Map<String, Object> getData() {
        return data;
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

    public NT_StartGame start() {
        NT_StartGame startGame = new NT_StartGame();
        fillUpdate(startGame);
        startGame.expansionsSetting = map.getExpansionSetting().ordinal();
        return startGame;
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
}
