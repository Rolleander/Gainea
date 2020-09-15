package com.broll.gainea.client.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.render.MapObjectRender;
import com.broll.gainea.client.ui.ingame.AttackAndMoveActionHandler;
import com.broll.gainea.client.ui.ingame.RequiredActionHandler;
import com.broll.gainea.client.ui.ingame.UnitSelectionWindow;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InGameUI {

    private Gainea game;
    private Skin skin;
    private Table topBar = new Table();
    private Table bottomBar = new Table();
    private Table center = new Table();
    private Table centerContent = new Table();
    private AttackAndMoveActionHandler attackAndMoveHandler;
    private RequiredActionHandler requiredActionHandler;

    public InGameUI(Gainea game, Skin skin) {
        this.attackAndMoveHandler = new AttackAndMoveActionHandler(game);
        this.requiredActionHandler = new RequiredActionHandler(game, skin);
        this.game = game;
        this.skin = skin;
    }

    public Cell<Table> showCenter(Table table) {
        centerContent.clear();
        return centerContent.add(table);
    }

    public void show() {
        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.add(topBar).top().height(40).row();
        overlay.add(center).center().expand().fill().row();
        overlay.add(bottomBar).bottom().height(40);
        game.uiStage.addActor(overlay);
        Table overlay2 = new Table();
        overlay2.setFillParent(true);
        overlay2.add(centerContent).expand();
        game.uiStage.addActor(overlay2);
    }

    public void selectStack(Location location, Collection<MapObjectRender> stack) {
        clearSelection();
        Table window = UnitSelectionWindow.create(game, skin, location, stack);
        center.add(window).right().top().expand().width(230);
        center.layout();
    }

    public void selectedUnits(List<NT_Unit> units) {
        attackAndMoveHandler.showFor(units);
    }

    public void clearSelection() {
        center.clear();
    }

    public void optionalActions(List<NT_Action> actions, PlayerPerformOptionalAction playerPerformAction) {
        List<NT_Action_Move> moves = actions.stream().filter(it -> it instanceof NT_Action_Move).map(it -> (NT_Action_Move) it).collect(Collectors.toList());
        List<NT_Action_Attack> attacks = actions.stream().filter(it -> it instanceof NT_Action_Attack).map(it -> (NT_Action_Attack) it).collect(Collectors.toList());
        List<NT_Action_Card> cards = actions.stream().filter(it -> it instanceof NT_Action_Card).map(it -> (NT_Action_Card) it).collect(Collectors.toList());
        attackAndMoveHandler.update(moves, attacks, playerPerformAction);
    }

    //required ui action
    public void action(NT_Action action, PlayerPerformAction playerPerformAction) {
        attackAndMoveHandler.clear();
        requiredActionHandler.handleAction(action, playerPerformAction);
    }

}
