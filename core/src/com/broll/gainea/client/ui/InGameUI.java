package com.broll.gainea.client.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.render.MapObjectRender;
import com.broll.gainea.client.ui.elements.TableUtils;
import com.broll.gainea.client.ui.ingame.AttackAndMoveActionHandler;
import com.broll.gainea.client.ui.ingame.BattleHandler;
import com.broll.gainea.client.ui.ingame.MenuWindows;
import com.broll.gainea.client.ui.ingame.RequiredActionHandler;
import com.broll.gainea.client.ui.ingame.UnitSelectionWindow;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InGameUI {

    private Gainea game;
    private Skin skin;
    private Table topBar;
    private Table bottomBar;
    private Table centerOverlay;
    private Table center = new Table();
    private Table centerContent = new Table();
    private AttackAndMoveActionHandler attackAndMoveHandler;
    private RequiredActionHandler requiredActionHandler;
    private BattleHandler battleHandler;
    private MenuWindows windows;

    public InGameUI(Gainea game, Skin skin) {
        this.attackAndMoveHandler = new AttackAndMoveActionHandler(game);
        this.requiredActionHandler = new RequiredActionHandler(game, skin);
        this.battleHandler = new BattleHandler(game, skin);
        this.game = game;
        this.skin = skin;
        topBar = new Table(skin);
        bottomBar = new Table(skin);
        centerOverlay = new Table(skin);
        bottomBar.setBackground("menu-bg");
        bottomBar.defaults().spaceRight(20);
        bottomBar.add(TableUtils.textButton(skin, "Fraktionen", () -> windows.showFractionWindow()));
        bottomBar.add(TableUtils.textButton(skin, "Spieler", () -> windows.showPlayerWindow()));
        bottomBar.add(TableUtils.textButton(skin, "Ziele", () -> windows.showGoalWindow()));
        bottomBar.add(TableUtils.textButton(skin, "Karten", () -> windows.showCardWindow()));
    }

    public Cell<Actor> showCenter(Actor actor) {
        centerContent.clear();
        return centerContent.add(actor);
    }

    public Cell<Actor> showCenterOverlay(Actor actor) {
        centerOverlay.clear();
        return centerOverlay.add(actor);
    }

    public void show() {
        Table overlay = new Table();
        overlay.setFillParent(true);
        overlay.add(center).center().expand().fill().padTop(40).padBottom(40).row();
        game.uiStage.addActor(overlay);
        Table overlay2 = new Table();
        overlay2.setFillParent(true);
        overlay2.add(centerContent).expand();
        game.uiStage.addActor(overlay2);
        Table overlay3 = new Table();
        overlay3.setFillParent(true);
        overlay3.add(topBar).top().expandX().fillX().height(40).row();
        overlay3.add(centerOverlay).expand().fill().row();
        overlay3.add(bottomBar).bottom().expandX().fillX().height(40);
        game.uiStage.addActor(overlay3);
        this.windows = new MenuWindows(game, skin);
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
        attackAndMoveHandler.showFor(new ArrayList<>());
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

    public void startBattle(List<NT_Unit> attackers, List<NT_Unit> defenders, Location location) {
        clearSelection();
        showCenter(battleHandler.startBattle(center, attackers, defenders, location));
    }

    public void updateBattle(int[] attackRolls, int[] defenderRolls, List<Pair<NT_Unit, Integer>> damagedAttackers, List<Pair<NT_Unit, Integer>> damagedDefenders, int state) {
        battleHandler.updateBattle(attackRolls, defenderRolls, damagedAttackers, damagedDefenders, state);
    }

    public void updateWindows() {
        windows.updateWindows();
    }
}
