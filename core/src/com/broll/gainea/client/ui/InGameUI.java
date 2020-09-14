package com.broll.gainea.client.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.render.MapObjectRender;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.ingame.MapActionHandler;
import com.broll.gainea.client.ui.ingame.UnitSelection;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Action_MoveUnit;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InGameUI {

    private Gainea game;
    private Skin skin;
    private Table topBar = new Table();
    private Table bottomBar = new Table();
    private Table center = new Table();
    private MapActionHandler mapActionHandler;

    public InGameUI(Gainea game, Skin skin) {
        this.mapActionHandler = new MapActionHandler(game);
        this.game = game;
        this.skin = skin;
    }

    public void show() {
        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.add(topBar).top().height(40).row();
        overlay.add(center).center().expand().fill().row();
        overlay.add(bottomBar).bottom().height(40);
        game.uiStage.addActor(overlay);
    }

    public void selectStack(Location location, Collection<MapObjectRender> stack) {
        closeAll();
        Table window = UnitSelection.create(game, skin, location, stack);
        center.add(window).right().top().expand().width(200);
        center.layout();
    }

    public void selectedUnits(List<NT_Unit> units) {
        mapActionHandler.showFor(units);
    }

    public void closeAll() {
        center.clear();
    }

    public void optionalActions(List<NT_Action> actions, PlayerPerformOptionalAction playerPerformAction) {
        List<NT_Action_MoveUnit> moves = actions.stream().filter(it -> it instanceof NT_Action_MoveUnit).map(it -> (NT_Action_MoveUnit) it).collect(Collectors.toList());
        List<NT_Action_Attack> attacks = actions.stream().filter(it -> it instanceof NT_Action_Attack).map(it -> (NT_Action_Attack) it).collect(Collectors.toList());
        List<NT_Action_Card> cards = actions.stream().filter(it -> it instanceof NT_Action_Card).map(it -> (NT_Action_Card) it).collect(Collectors.toList());
        mapActionHandler.update(moves, attacks,playerPerformAction);
    }

    //required ui action
    public void action(NT_Action action, PlayerPerformAction playerPerformAction) {
        mapActionHandler.clear();

    }

}
