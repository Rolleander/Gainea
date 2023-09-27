package com.broll.gainea.client.ui.ingame.actions;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.ClientMapContainer;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.ingame.map.ActionTrail;
import com.broll.gainea.client.ui.ingame.map.MapAction;
import com.broll.gainea.client.ui.utils.ActionListener;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Coordinates;
import com.broll.gainea.server.core.map.Location;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AttackAndMoveActionHandler {

    private Gainea game;
    private List<MapAction> mapActions = new ArrayList<>();
    private List<NT_Unit> selectedUnits;

    public AttackAndMoveActionHandler(Gainea game) {
        this.game = game;
    }

    public void update(List<NT_Action_Move> moves, List<NT_Action_Attack> attacks, PlayerPerformOptionalAction playerPerformAction) {
        clear();
        ClientMapContainer map = game.state.getMap();
        moves.forEach(move -> {
            Location to = map.getLocation(move.location);
            createDistinctMapActions(to, move, it -> it.units, MapAction.TYPE_MOVE, playerPerformAction);
        });
        attacks.forEach(attack -> {
            Location to = map.getLocation(attack.location);
            createDistinctMapActions(to, attack, it -> it.units, MapAction.TYPE_ATTACK, playerPerformAction);
        });
    }

    private <A extends NT_Action> void createDistinctMapActions(Location to, A action, Function<A, NT_Unit[]> getUnits, int type, PlayerPerformOptionalAction playerPerformAction) {
        MultiValueMap<Location, NT_Unit> paths = new MultiValueMap<>();
        for (NT_Unit unit : getUnits.apply(action)) {
            paths.put(game.state.getMap().getLocation(unit.location), unit);
        }
        paths.keySet().forEach(from -> {
            List<NT_Unit> pathUnits = (List<NT_Unit>) paths.getCollection(from);
            createMapAction(from, to, action, pathUnits, type, () -> {
                int[] options = getSelectedUnits(selectedUnits, getUnits.apply(action));
                playerPerformAction.perform(action, 0, options);
            });
        });
    }

    private void createMapAction(Location from, Location to, Object nt_action, List<NT_Unit> units, int type, ActionListener listener) {
        MapAction action = new MapAction(game, type, to.number, listener);
        action.setAction(nt_action);
        action.setUnits(units);
        mapActions.add(action);
        Coordinates toC = to.coordinates;
        Coordinates fromC = from.coordinates;
        action.setPosition(toC.getDisplayX(), toC.getDisplayY());
        float angle = MathUtils.atan2(toC.getDisplayY() - fromC.getDisplayY(), toC.getDisplayX() - fromC.getDisplayX());
        action.setRotation((float) Math.toDegrees(angle - Math.PI / 2));
        action.setFromTo(from.number, to.number);
        ActionTrail trail = new ActionTrail(game, type, toC, fromC);
        game.gameStage.addActor(trail);
        action.setTrail(trail);
        game.gameStage.addActor(action);
    }

    public void clear() {
        mapActions.forEach(MapAction::remove);
        mapActions.clear();
    }

    public void showFor(List<NT_Unit> units) {
        this.selectedUnits = units;
        mapActions.forEach(it -> {
            it.setVisible(false);
            it.toFront();
        });
        mapActions.stream().filter(it -> CollectionUtils.containsAny(it.getUnits(), units)).forEach(it -> it.setVisible(true));
    }

    private int[] getSelectedUnits(List<NT_Unit> selectedUnits, NT_Unit[] units) {
        List<Integer> selection = new ArrayList<>();
        int[] selectedIds = selectedUnits.stream().mapToInt(it -> it.id).toArray();
        for (int i = 0; i < units.length; i++) {
            if (ArrayUtils.contains(selectedIds, units[i].id)) {
                selection.add(i);
            }
        }
        return selection.stream().mapToInt(i -> i).toArray();
    }

}
