package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.GameState;
import com.broll.gainea.client.render.ExpansionRender;
import com.broll.gainea.client.ui.AbstractScreen;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_MoveUnit;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.MapFactory;
import com.broll.gainea.server.init.ExpansionSetting;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

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
            NT_Unit bo = new NT_Unit();
            bo.name = "test";
            bo.name = "Tier";
            bo.power = 3;
            bo.health = 1;
            bo.maxHealth = 3;
            bo.location = i / 2;
            bo.id =i;
            bo.icon = i;
            update.objects[i] = bo;
        }
        update.players = new NT_Player[1];
        update.players[0] = new NT_Player();
        int c2 = 40;
        update.players[0].units = new NT_Unit[c2];
        List<NT_Action> actions = new ArrayList<>();
        for (int i = 0; i < c2; i++) {
            NT_Unit bo = new NT_Unit();
            bo.owner = 0;
            bo.id = c+i;
            bo.name = "test";
            bo.icon = i;
            bo.location = i / MathUtils.random(1, 10);
            update.players[0].units[i] = bo;
            NT_Action_MoveUnit m = new NT_Action_MoveUnit();
            m.unit = bo;
            m.possibleLocations =  state.getMap().getLocation(m.unit.location).getConnectedLocations().stream().mapToInt(Location::getNumber).toArray();
            actions.add(m);
        }
        state.update(update).forEach(render -> game.gameStage.addActor(render));
        state.performOptionalAction(actions, null);
        return new Table();
    }
}
