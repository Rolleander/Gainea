package com.broll.gainea.client.ui.ingame.actions;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.ClientMapContainer;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.utils.ActionListener;
import com.broll.gainea.client.ui.ingame.map.ActionTrail;
import com.broll.gainea.client.ui.ingame.map.MapAction;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Coordinates;
import com.broll.gainea.server.core.map.Location;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        moves.forEach(move -> Arrays.stream(move.possibleLocations).forEach(target -> {
            Location from = map.getLocation(move.units[0].location);
            Location to = map.getLocation(target);
            createMove(from, to, move, playerPerformAction);
        }));
        attacks.forEach(attack -> Arrays.stream(attack.attackLocations).forEach(target -> {
            Location from = map.getLocation(attack.units[0].location);
            Location to = map.getLocation(target);
            createAttack(from, to, attack, playerPerformAction);
        }));
    }

    private void createMove(Location from, Location to, NT_Action_Move move, PlayerPerformOptionalAction playerPerformAction) {
        createMapAction(from, to, move, 0, from.getNumber(), () -> {
            int option = getSelectedLocation(to, move.possibleLocations);
            int options[] = getSelectedUnits(selectedUnits, move.units);
            playerPerformAction.perform(move, option, options);
        });
    }

    private void createAttack(Location from, Location to, NT_Action_Attack attack, PlayerPerformOptionalAction playerPerformAction) {
        createMapAction(from, to, attack, 1, from.getNumber(), () -> {
            int option = getSelectedLocation(to, attack.attackLocations);
            int options[] = getSelectedUnits(selectedUnits, attack.units);
            playerPerformAction.perform(attack, option, options);
        });
    }

    private void createMapAction(Location from, Location to, Object nt_action, int type, int unitId, ActionListener listener) {
        MapAction action = new MapAction(game, type, unitId, listener);
        action.setAction(nt_action);
        mapActions.add(action);
        Coordinates toC = to.getCoordinates();
        Coordinates fromC = from.getCoordinates();
        action.setPosition(toC.getDisplayX(), toC.getDisplayY());
        float angle = MathUtils.atan2(toC.getDisplayY() - fromC.getDisplayY(), toC.getDisplayX() - fromC.getDisplayX());
        action.setRotation((float) Math.toDegrees(angle - Math.PI / 2));
        action.setFromTo(from.getNumber(), to.getNumber());
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
        int[] locations = units.stream().mapToInt(it -> it.location).distinct().toArray();
        mapActions.stream().filter(it -> ArrayUtils.contains(locations, it.getLocationId())).filter(it -> isActionForUnits(it.getAction(), units)).forEach(it -> it.setVisible(true));
    }

    private int getSelectedLocation(Location location, int[] selection) {
        for (int i = 0; i < selection.length; i++) {
            if (location.getNumber() == selection[i]) {
                return i;
            }
        }
        return 0;
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

    private boolean isActionForUnits(Object nt_action, List<NT_Unit> units) {
        int[] unitIds = units.stream().mapToInt(it -> it.id).toArray();
        NT_Unit[] actionUnits = null;
        if (nt_action instanceof NT_Action_Move) {
            actionUnits = ((NT_Action_Move) nt_action).units;
        } else if (nt_action instanceof NT_Action_Attack) {
            actionUnits = ((NT_Action_Attack) nt_action).units;
        } else {
            return false;
        }
        for (int id : Arrays.stream(actionUnits).mapToInt(it -> it.id).toArray()) {
            if (ArrayUtils.contains(unitIds, id)) {
                return true;
            }
        }
        return false;
    }

}
