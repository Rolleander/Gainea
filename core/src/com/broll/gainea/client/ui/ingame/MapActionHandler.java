package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.ClientMapContainer;
import com.broll.gainea.client.game.GameState;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.elements.ActionListener;
import com.broll.gainea.client.ui.elements.ActionTrail;
import com.broll.gainea.client.ui.elements.MapAction;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_MoveUnit;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Coordinates;
import com.broll.gainea.server.core.map.Location;
import com.esotericsoftware.minlog.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapActionHandler {

    private Gainea game;
    private List<MapAction> mapActions = new ArrayList<>();
    private List<NT_Unit> selectedUnits;

    public MapActionHandler(Gainea game) {
        this.game = game;
    }

    public void update(List<NT_Action_MoveUnit> moves, List<NT_Action_Attack> attacks, PlayerPerformOptionalAction playerPerformAction) {
        clear();
        ClientMapContainer map = game.state.getMap();
        moves.forEach(move -> Arrays.stream(move.possibleLocations).forEach(target -> {
            Location from = map.getLocation(move.unit.location);
            Location to = map.getLocation(target);
            createMove(from, to, move, move.unit.id, playerPerformAction);
        }));
        attacks.forEach(attack -> Arrays.stream(attack.attackLocations).forEach(target -> {
            Location from = map.getLocation(attack.units[0].location);
            Location to = map.getLocation(target);
            createAttack(from, to, attack, playerPerformAction);
        }));
    }

    private void createMove(Location from, Location to, NT_Action_MoveUnit move, int unitId, PlayerPerformOptionalAction playerPerformAction) {
        createMapAction(from, to, 0, unitId, () -> {

        });
    }

    private void createAttack(Location from, Location to, NT_Action_Attack attack, PlayerPerformOptionalAction playerPerformAction) {
        createMapAction(from, to, 1, from.getNumber(), () -> {
            //attack location
            int option = to.getNumber();
            List<Integer> units = new ArrayList<>();
            Arrays.stream(attack.units).forEach(unit -> {
                int i = ArrayUtils.indexOf(attack.units, unit);
                if (selectedUnits.stream().filter(it -> it.id == attack.units[i].id).findFirst().isPresent()) {
                    units.add(i);
                }
            });
            playerPerformAction.perform(attack, option, units.stream().mapToInt(i -> i).toArray());
        });
    }

    private void createMapAction(Location from, Location to, int type, int unitId, ActionListener listener) {
        MapAction action = new MapAction(game, type, unitId, listener);
        mapActions.add(action);
        Coordinates toC = to.getCoordinates();
        Coordinates fromC = from.getCoordinates();
        action.setPosition(toC.getDisplayX() - 50, toC.getDisplayY() - 50);
        float angle = MathUtils.atan2(toC.getDisplayY() - fromC.getDisplayY(), toC.getDisplayX() - fromC.getDisplayX());
        action.setRotation((float) Math.toDegrees(angle - Math.PI / 2));
        action.setFromTo(from.getNumber(), to.getNumber());
        ActionTrail trail = new ActionTrail(game, type, toC, fromC);
        game.gameStage.addActor(trail);
        action.setTrail(trail);
        game.gameStage.addActor(action);
    }

    public void clear() {
        mapActions.forEach(Actor::remove);
        mapActions.clear();
    }

    public void showFor(List<NT_Unit> units) {
        this.selectedUnits = units;
        mapActions.forEach(it -> it.setVisible(false));
        List<MapAction> moves = mapActions.stream().filter(it -> it.getType() == 0).collect(Collectors.toList());
        List<MapAction> attacks = mapActions.stream().filter(it -> it.getType() == 1).collect(Collectors.toList());
        int[] ids = units.stream().mapToInt(u -> u.id).toArray();
        moves.stream().filter(it -> ArrayUtils.contains(ids, it.getUnitId())).distinct().forEach(it -> it.setVisible(true));
        int[] locations = units.stream().mapToInt(it -> it.location).distinct().toArray();
        attacks.stream().filter(it -> ArrayUtils.contains(locations, it.getUnitId())).distinct().forEach(it -> it.setVisible(true));
    }

}
