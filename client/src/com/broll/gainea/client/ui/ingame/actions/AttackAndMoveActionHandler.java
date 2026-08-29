package com.broll.gainea.client.ui.ingame.actions;

import com.broll.gainea.client.game.map.Coordinates;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.ClientMapContainer;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.game.BoardObjectState;
import com.broll.gainea.client.game.ClientActions;
import com.broll.gainea.client.ui.ingame.map.ActionTrail;
import com.broll.gainea.client.ui.ingame.map.MapAction;
import com.broll.gainea.client.ui.ingame.unit.UnitRender;
import com.broll.gainea.client.ui.utils.ActionListener;
import com.broll.gainea.net.proto.NT_Action_Attack;
import com.broll.gainea.net.proto.NT_Action_Move;
import com.broll.gainea.net.proto.NT_Unit;
import com.broll.gainea.client.game.map.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AttackAndMoveActionHandler {

    private Gainea game;
    private List<MapAction> mapActions = new ArrayList<>();
    private List<BoardObjectState> selectedUnits;

    public AttackAndMoveActionHandler(Gainea game) {
        this.game = game;
    }

    public void update(List<NT_Action_Move> moves, List<NT_Action_Attack> attacks, PlayerPerformOptionalAction playerPerformAction) {
        clear();
        ClientMapContainer map = game.state.getMap();
        moves.forEach(move -> {
            Location to = map.getLocation(move.getLocation());
            createDistinctMapActions(to, move, move.getUnitsList(), MapAction.TYPE_MOVE, playerPerformAction);
        });
        attacks.forEach(attack -> {
            Location to = map.getLocation(attack.getLocation());
            createDistinctMapActions(to, attack, attack.getUnitsList(), MapAction.TYPE_ATTACK, playerPerformAction);
        });
    }

    private void createDistinctMapActions(Location to, Object action, List<NT_Unit> actionUnits, int type, PlayerPerformOptionalAction playerPerformAction) {
        java.util.Map<Location, List<BoardObjectState>> paths = new java.util.HashMap<>();
        for (NT_Unit unit : actionUnits) {
            BoardObjectState state = game.state.getMapObjectsContainer() == null ? null : com.broll.gainea.client.game.GameUtils.findObject(game, unit.getId());
            paths.computeIfAbsent(game.state.getMap().getLocation(unit.getLocation()), ignored -> new ArrayList<>()).add(state);
        }
        paths.keySet().forEach(from -> {
            List<BoardObjectState> pathUnits = paths.get(from);
            createMapAction(from, to, action, pathUnits, type, () -> {
                int[] ids = getSelectedUnitIds(selectedUnits);
                selectedUnits.forEach(it ->
                        ((UnitRender) game.state.getMapObjectsContainer().getObjectRender(it)).setActionActive(false)
                );
                playerPerformAction.perform(action instanceof NT_Action_Move ? ClientActions.wrap((NT_Action_Move) action) : ClientActions.wrap((NT_Action_Attack) action), 0, ids);
            });
        });
    }

    private void createMapAction(Location from, Location to, Object nt_action, List<BoardObjectState> units, int type, ActionListener listener) {
        MapAction action = new MapAction(game, type, to.getNumber(), listener);
        action.setAction(nt_action);
        action.setUnits(units);
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

    public void showFor(List<BoardObjectState> units) {
        this.selectedUnits = units;
        mapActions.forEach(it -> {
            it.setVisible(false);
            it.toFront();
        });
        mapActions.stream().filter(it -> it.getUnits().stream().anyMatch(units::contains)).forEach(it -> it.setVisible(true));
    }

    private int[] getSelectedUnitIds(List<BoardObjectState> selectedUnits) {
        return selectedUnits.stream().mapToInt(BoardObjectState::id).toArray();
    }

}
