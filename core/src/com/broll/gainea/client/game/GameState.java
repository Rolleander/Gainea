package com.broll.gainea.client.game;

import com.broll.gainea.Gainea;
import com.broll.gainea.client.render.MapObjectRender;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.init.ExpansionSetting;
import com.google.common.collect.Streams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {

    private Gainea game;
    private int turnNumber;
    private List<NT_BoardObject> objects;
    private List<NT_Player> players;
    private ClientMapContainer mapContainer;
    private MapObjectContainer mapObjectsContainer;

    public GameState(Gainea game) {
        this.game = game;
    }

    public void init(ExpansionSetting setting) {
        mapContainer = new ClientMapContainer(game, setting);
        mapObjectsContainer = new MapObjectContainer(this);
    }

    public void update(NT_BoardUpdate update) {
        this.turnNumber = update.turns;
        this.objects = Arrays.asList(update.objects);
        this.players = Arrays.asList(update.players);
        mapObjectsContainer.update(Streams.concat(objects.stream(), players.stream().flatMap(p -> Arrays.stream(p.units))).collect(Collectors.toList()));
    }

    public void performAction(NT_Action action, PlayerPerformAction playerPerformAction) {
        game.ui.getInGameUI().action(action, playerPerformAction);
    }

    public void performOptionalAction(List<NT_Action> actions, PlayerPerformOptionalAction playerPerformAction) {
        game.ui.getInGameUI().optionalActions(actions, playerPerformAction);
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

    public List<NT_Player> getPlayers() {
        return players;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public ClientMapContainer getMap() {
        return mapContainer;
    }

    public Gainea getContainer() {
        return game;
    }
}
