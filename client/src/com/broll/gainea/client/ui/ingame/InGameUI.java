package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.components.Popup;
import com.broll.gainea.client.ui.ingame.actions.AttackAndMoveActionHandler;
import com.broll.gainea.client.ui.ingame.actions.RequiredActionHandler;
import com.broll.gainea.client.ui.ingame.battle.BattleHandler;
import com.broll.gainea.client.ui.ingame.hud.GoalOverlay;
import com.broll.gainea.client.ui.ingame.hud.InfoMessageContainer;
import com.broll.gainea.client.ui.ingame.hud.MenuActions;
import com.broll.gainea.client.ui.ingame.hud.PlayerOverlay;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.windows.LogWindow;
import com.broll.gainea.client.ui.ingame.windows.MapObjectSelection;
import com.broll.gainea.client.ui.screens.ScoreScreen;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Battle_Damage;
import com.broll.gainea.net.NT_Battle_Roll;
import com.broll.gainea.net.NT_GameOver;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class InGameUI {

    public InfoMessageContainer infoMessages;
    private Gainea game;
    private Skin skin;
    private Table topCenter = new Table();
    private Table bottomCenter = new Table();
    private Table rightPanel = new Table();
    private Table centerContent = new Table();

    private AttackAndMoveActionHandler attackAndMoveHandler;
    private RequiredActionHandler requiredActionHandler;
    private BattleHandler battleHandler;
    private GoalOverlay goalOverlay;
    private PlayerOverlay playerOverlay;
    private MenuActions menuActions;
    private boolean selectionOpen = false;

    private Location selectionLocation;

    public InGameUI(Gainea game, Skin skin) {
        this.attackAndMoveHandler = new AttackAndMoveActionHandler(game);
        this.requiredActionHandler = new RequiredActionHandler(game, skin);
        this.battleHandler = new BattleHandler(game, skin);
        this.game = game;
        this.skin = skin;
        this.goalOverlay = new GoalOverlay(game);
        this.playerOverlay = new PlayerOverlay(game);
        this.menuActions = new MenuActions(game);
        this.infoMessages = new InfoMessageContainer(game);
    }

    public Cell<Actor> showCenter(Actor actor) {
        centerContent.clear();
        return centerContent.add(actor);
    }

    public Cell<Actor> showTopCenter(Actor actor) {
        topCenter.clear();
        return topCenter.add(actor);
    }

    public Cell<Actor> showBottomCenter(Actor actor) {
        bottomCenter.clear();
        return bottomCenter.add(actor);
    }

    public void clearCenter() {
        fadeActors(centerContent);
        fadeActors(topCenter);
        fadeActors(bottomCenter);
    }

    private void fadeActors(Table table) {
        table.getChildren().forEach(it ->
                it.addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.removeActor())));
    }

    public void show() {
        addCenterComponent(rightPanel).top().padTop(100).padBottom(40).right();
        addCenterComponent(infoMessages).center().top().padTop(50);
        addCenterComponent(centerContent).center();
        addCenterComponent(topCenter).center().top().padTop(200);
        addCenterComponent(bottomCenter).center().bottom().padBottom(200);
        game.uiStage.addActor(playerOverlay);
        game.uiStage.addActor(goalOverlay);
        menuActions.show();
    }

    private Table addCenterComponent(WidgetGroup component) {
        Table container = new Table();
        container.setFillParent(true);
        container.add(component);
        game.uiStage.addActor(container);
        return container;
    }

    public void selectStack(Location location, Collection<MapObjectRender> stack) {
        clearSelection();
        AudioPlayer.playSound("select.ogg");
        Table window = new MapObjectSelection(game, location, stack);
        rightPanel.add(window).right().top().expand();
        rightPanel.layout();
        selectionLocation = location;
        selectionOpen = true;
    }

    public void selectedUnits(List<NT_Unit> units) {
        if (game.state.areActionsAllowed()) {
            attackAndMoveHandler.showFor(units);
        }
    }

    public void clearSelection() {
        selectionOpen = false;
        rightPanel.clear();
        attackAndMoveHandler.showFor(new ArrayList<>());
    }

    public void optionalActions(List<NT_Action> actions, PlayerPerformOptionalAction playerPerformAction) {
        List<NT_Action_Move> moves = actions.stream().filter(it -> it instanceof NT_Action_Move).map(it -> (NT_Action_Move) it).collect(Collectors.toList());
        List<NT_Action_Attack> attacks = actions.stream().filter(it -> it instanceof NT_Action_Attack).map(it -> (NT_Action_Attack) it).collect(Collectors.toList());
        attackAndMoveHandler.update(moves, attacks, playerPerformAction);
        requiredActionHandler.toFront();
        menuActions.updateOptionalActions(actions, playerPerformAction);
        game.state.getMapObjectsContainer().updateActiveState(moves, attacks);
    }

    //required ui action
    public void action(NT_Action action, PlayerPerformAction playerPerformAction) {
        attackAndMoveHandler.clear();
        requiredActionHandler.handleAction(action, playerPerformAction);
    }

    public void startBattle(List<NT_Unit> attackers, List<NT_Unit> defenders, Location location, boolean allowRetreat) {
        clearSelection();
        clearCenter();
        showCenter(battleHandler.startBattle(attackers, defenders, location, allowRetreat));
    }

    public void updateBattle(NT_Battle_Roll[] attackRolls, NT_Battle_Roll[] defenderRolls, Stack<NT_Battle_Damage> damage, int state) {
        battleHandler.updateBattle(attackRolls, defenderRolls, damage, state);
    }

    public void updateWindows() {
        menuActions.update();
        goalOverlay.update();
        playerOverlay.update();
    }

    public void hideWindows() {
        //dont hide
        //menuActions.hideWindows();
    }

    public LogWindow getLogWindow() {
        return menuActions.getLogWindow();
    }

    public void gameOver(NT_GameOver end) {
        Table table = new Table();
        table.add(LabelUtils.label(game.ui.skin, "Game over!")).row();
        Button button = TableUtils.textButton(skin, "Score screen", () -> game.ui.showScreen(new ScoreScreen(end)));
        table.add(button);
        Popup.show(game, table);
    }

    public BattleHandler getBattleHandler() {
        return battleHandler;
    }

    public RequiredActionHandler getRequiredActionHandler() {
        return requiredActionHandler;
    }

    public Location getSelectionLocation() {
        return selectionLocation;
    }

    public boolean isSelectionOpen() {
        return selectionOpen;
    }
}
