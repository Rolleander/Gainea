package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.GameState;
import com.broll.gainea.client.ui.AbstractScreen;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_PlayerAction;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.init.ExpansionSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestMapScreen extends AbstractScreen {

    private Gainea game;
    private GameState state;

    public TestMapScreen(Gainea game) {
        this.game = game;
        state = new GameState(game);
        state.init(ExpansionSetting.FULL);
        game.state = state;
    }

    @Override
    public Actor build() {
        game.ui.getInGameUI().show();
        state.getMap().getRenders().forEach(render -> game.gameStage.addActor(render));
        NT_BoardUpdate update = new NT_BoardUpdate();
        int c = 20;
        update.objects = new NT_BoardObject[c];
        for (int i = 0; i < c; i++) {
            NT_Monster bo = new NT_Monster();
            bo.name = "test";
            bo.name = "Tier";
            bo.power = 3;
            bo.health = 1;
            bo.stars = MathUtils.random(1, 8);
            bo.maxHealth = 3;
            bo.location = i * 2;
            bo.id = i;
            bo.icon = 50 + i;
            update.objects[i] = bo;
        }
        update.players = new NT_Player[1];
        update.players[0] = new NT_Player();
        update.players[0].color=1;
        int c2 = 40;
        update.players[0].units = new NT_Unit[c2];
        List<NT_Action> actions = new ArrayList<>();
        for (int i = 0; i < c2; i++) {
            NT_Unit bo = new NT_Unit();
            bo.owner = 0;
            bo.id = c + i;
            bo.name = "test";
            bo.icon = i;
            bo.location = i / MathUtils.random(1, 10);
            update.players[0].units[i] = bo;
            //     NT_Action_Move m = new NT_Action_Move();
            //      m.unit = bo;
            //    m.possibleLocations = state.getMap().getLocation(m.unit.location).getConnectedLocations().stream().mapToInt(Location::getNumber).toArray();
            //    actions.add(m);
        }

        Arrays.stream(update.players[0].units).map(it -> state.getMap().getLocation(it.location)).forEach(from -> {
            List<Location> walkTo = from.getConnectedLocations().stream().collect(Collectors.toList());
            List<Location> attackTo = walkTo.stream().filter(it -> Arrays.stream(update.objects).anyMatch(unit -> unit.location == it.getNumber())).collect(Collectors.toList());
            walkTo.removeAll(attackTo);
            NT_Unit[] units = Arrays.stream(update.players[0].units).filter(it -> it.location == from.getNumber()).collect(Collectors.toList()).toArray(new NT_Unit[0]);
            if (!walkTo.isEmpty()) {
                NT_Action_Move move = new NT_Action_Move();
                move.possibleLocations = walkTo.stream().mapToInt(Location::getNumber).toArray();
                move.units =  units;
                actions.add(move);
            }
            if (!attackTo.isEmpty()) {
                NT_Action_Attack attack = new NT_Action_Attack();
                attack.attackLocations = attackTo.stream().mapToInt(Location::getNumber).toArray();
                attack.units = units;
                actions.add(attack);
            }
        });
        state.update(update).forEach(render -> game.gameStage.addActor(render));
        NT_Action_PlaceUnit action = new NT_Action_PlaceUnit();
        action.unitToPlace = (NT_Unit) update.objects[5];
        action.possibleLocations = new int[]{1,5,8,12};
        state.performAction(action,null);
        //state.performOptionalAction(actions, null);
        return new Table();
    }
}
