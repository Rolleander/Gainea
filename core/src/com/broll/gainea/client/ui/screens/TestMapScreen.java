package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.MapScrollHandler;
import com.broll.gainea.client.game.GameState;
import com.broll.gainea.client.game.sites.GameEventSite;
import com.broll.gainea.client.ui.AbstractScreen;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_Event_MovedObject;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.init.ExpansionSetting;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestMapScreen extends AbstractScreen {

    private Gainea game;
    private GameState state;

    public TestMapScreen(Gainea game) {
        this.game = game;
        state = new GameState(game);
        game.state = state;
    }

    @Override
    public Actor build() {
        state.init(ExpansionSetting.FULL, null);
        game.ui.initInGameUi();
        game.ui.inGameUI.show();
        for (int i = 0; i < 3; i++) {
            NT_Card card = new NT_Card();
            card.picture = i + 5;
            card.title = "Testkarte #" + i;
            card.text = StringUtils.repeat("Wenn du diese Kart ausspielst dann geht es richtig ab. Du könntest das Spiel gewinnen vermutlich. Aber man weiß es nicht so wirklich :)", i + 1);
            game.state.getCards().add(card);
            NT_Goal goal = new NT_Goal();
            goal.points = i + 1;
            goal.description = StringUtils.repeat("Erobere mindestens " + i * 3 + " verschiedene Gebiete und das sehr schnell bitte!", i + 1);
            goal.restriction = "Gainea";
            game.state.getGoals().add(goal);
        }
        NT_Goal goal = new NT_Goal();
        goal.points = 3;
        goal.description = "Mach erst das zuerst\nund dann das bitte....";
        goal.restriction = "Alle";
        game.state.getGoals().add(goal);
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
            bo.owner = NT_Unit.NO_OWNER;
            bo.icon = 50 + i;
            update.objects[i] = bo;
        }
        update.players = new NT_Player[2];
        NT_Player p = new NT_Player();
        p.color = 1;
        int c2 = 40;
        p.fraction = 0;
        p.name = "Peter";
        p.points = 0;
        p.stars = 3;
        p.cards = 0;
        p.units = new NT_Unit[c2];
        update.players[0] = p;
        p = new NT_Player();
        p.color = 2;
        p.fraction = 5;
        p.name = "Lord Hans";
        p.units = new NT_Unit[0];
        p.points = 2;
        p.stars = 5;
        p.cards = 1;
        update.players[1] = p;
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
                move.units = units;
                actions.add(move);
            }
            if (!attackTo.isEmpty()) {
                NT_Action_Attack attack = new NT_Action_Attack();
                attack.attackLocations = attackTo.stream().mapToInt(Location::getNumber).toArray();
                attack.units = units;
                actions.add(attack);
            }
        });
        state.update(update);
        NT_Action_PlaceUnit action = new NT_Action_PlaceUnit();
        action.unitToPlace = (NT_Unit) update.objects[5];
        action.possibleLocations = new int[]{1, 5, 8, 12};
        //  state.performAction(action,null);
        state.performOptionalAction(actions, null);

        List<NT_Unit> attackers = new ArrayList<>();
        List<NT_Unit> defenders = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            NT_Unit u = new NT_Unit();
            u.health = 1;
            u.maxHealth = 1;
            u.power = 1;
            u.icon = i + 1;
            u.id = i;
            attackers.add(u);
        }
        for (int i = 0; i < 13; i++) {
            NT_Unit u = new NT_Unit();
            u.health = 1;
            u.maxHealth = 1;
            u.power = 1;
            u.icon = i + 20;
            u.id = i + 100;
            defenders.add(u);
        }
        int[] attackRolls = new int[attackers.size()];
        int[] defendRolls = new int[defenders.size()];
        attackRolls = Arrays.stream(attackRolls).map(i -> MathUtils.random(1, 6)).toArray();
        defendRolls = Arrays.stream(defendRolls).map(i -> MathUtils.random(1, 6)).toArray();
        Arrays.sort(attackRolls);
        Arrays.sort(defendRolls);
        ArrayUtils.reverse(attackRolls);
        ArrayUtils.reverse(defendRolls);

        List<Pair<NT_Unit, Integer>> damagedAttackers = new ArrayList<>();
        List<Pair<NT_Unit, Integer>> damagedDefenders = new ArrayList<>();
        for (int i = 0; i < Math.min(attackRolls.length, defendRolls.length); i++) {
            if (attackRolls[i] > defendRolls[i]) {
                damagedDefenders.add(Pair.of(defenders.get(i), 1));
            } else {
                damagedAttackers.add(Pair.of(attackers.get(i), 1));
            }
        }
        //    game.ui.inGameUI.startBattle(attackers, defenders, state.getMap().getArea(GaineaMap.Areas.MITSUMA_SEE));
        //   game.ui.inGameUI.updateBattle(attackRolls, defendRolls, damagedAttackers, damagedDefenders, 0);
        game.ui.inGameUI.updateWindows();

        GameEventSite eventSite = new GameEventSite();
        eventSite.init(game);
        NT_Event_MovedObject event = new NT_Event_MovedObject();
        NT_Monster monster = (NT_Monster) Arrays.stream(update.objects).filter(it -> it.location == game.state.getMap().getArea(GaineaMap.Areas.MOORKUESTE).getNumber()).findFirst().get();
        NT_Monster unit = new NT_Monster();
        unit.id = monster.id;
        unit.owner = monster.owner;
        unit.location = game.state.getMap().getArea(GaineaMap.Areas.XOMDELTA).getNumber();
        unit.icon = monster.icon;
        unit.name = monster.name;
        event.objects = new NT_Unit[]{unit};
        eventSite.received(event);
        return new Table();
    }
}
