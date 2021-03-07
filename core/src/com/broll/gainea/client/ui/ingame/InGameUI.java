package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.components.RoundInformation;
import com.broll.gainea.client.ui.ingame.actions.EndTurnButton;
import com.broll.gainea.client.ui.ingame.windows.MenuWindows;
import com.broll.gainea.client.ui.ingame.windows.UnitSelectionWindow;
import com.broll.gainea.client.ui.screens.ScoreScreen;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.components.Popup;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.client.ui.ingame.actions.AttackAndMoveActionHandler;
import com.broll.gainea.client.ui.ingame.battle.BattleHandler;
import com.broll.gainea.client.ui.ingame.actions.RequiredActionHandler;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_GameOver;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
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
    private RoundInformation roundInformation;
    private EndTurnButton endTurnButton;

    public InGameUI(Gainea game, Skin skin) {
        this.attackAndMoveHandler = new AttackAndMoveActionHandler(game);
        this.requiredActionHandler = new RequiredActionHandler(game, skin);
        this.battleHandler = new BattleHandler(game, skin);
        this.game = game;
        this.skin = skin;
        this.windows = new MenuWindows(game, skin);
        this.roundInformation = new RoundInformation(skin);
        topBar = new Table(skin);
        bottomBar = new Table(skin);
        centerOverlay = new Table(skin);
        bottomBar.setBackground("menu-bg");
        bottomBar.add(roundInformation).left().padLeft(10);
        Table buttonBar = new Table(skin);
        buttonBar.defaults().spaceRight(20).center();
        buttonBar.add(TableUtils.textButton(skin, "Fraktionen", () -> windows.showFractionWindow()));
        buttonBar.add(TableUtils.textButton(skin, "Spieler", () -> windows.showPlayerWindow()));
        buttonBar.add(TableUtils.textButton(skin, "Ziele", () -> windows.showGoalWindow()));
        buttonBar.add(TableUtils.textButton(skin, "Karten", () -> windows.showCardWindow()));
        bottomBar.add(buttonBar).right().expandX().padRight(10);
        endTurnButton = new EndTurnButton(skin);
        topBar.add(endTurnButton).left().space(15).padTop(10);
        game.state.addListener(endTurnButton);
    }

    public Cell<Actor> showCenter(Actor actor) {
        centerContent.clear();
        return centerContent.add(actor);
    }

    public Cell<Actor> showCenterOverlay(Actor actor) {
        centerOverlay.clear();
        return centerOverlay.add(actor);
    }

    public void clearCenter() {
        centerContent.clear();
        centerOverlay.clear();
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
    }

    public void selectStack(Location location, Collection<MapObjectRender> stack) {
        clearSelection();
        AudioPlayer.playSound("select.ogg");
        Table window = UnitSelectionWindow.create(game, skin, location, stack);
        center.add(window).right().top().expand();
        center.layout();
    }

    public void selectedUnits(List<NT_Unit> units) {
        if (game.state.areActionsAllowed()) {
            attackAndMoveHandler.showFor(units);
        }
    }

    public void clearSelection() {
        center.clear();
        attackAndMoveHandler.showFor(new ArrayList<>());
    }

    public void optionalActions(List<NT_Action> actions, PlayerPerformOptionalAction playerPerformAction) {
        List<NT_Action_Move> moves = actions.stream().filter(it -> it instanceof NT_Action_Move).map(it -> (NT_Action_Move) it).collect(Collectors.toList());
        List<NT_Action_Attack> attacks = actions.stream().filter(it -> it instanceof NT_Action_Attack).map(it -> (NT_Action_Attack) it).collect(Collectors.toList());
        activeCards(actions.stream().filter(it -> it instanceof NT_Action_Card).map(it -> (NT_Action_Card) it).collect(Collectors.toList()), playerPerformAction);
        attackAndMoveHandler.update(moves, attacks, playerPerformAction);
        endTurnButton.update(playerPerformAction);
    }

    private void activeCards(List<NT_Action_Card> cards, PlayerPerformOptionalAction playerPerformAction) {
        windows.getCardWindow().updatePlayableCards(cards, playerPerformAction);
    }

    //required ui action
    public void action(NT_Action action, PlayerPerformAction playerPerformAction) {
        attackAndMoveHandler.clear();
        requiredActionHandler.handleAction(action, playerPerformAction);
    }

    public void startBattle(List<NT_Unit> attackers, List<NT_Unit> defenders, Location location, boolean allowRetreat) {
        clearSelection();
        showCenter(battleHandler.startBattle(attackers, defenders, location,allowRetreat));
    }

    public void updateBattle(int[] attackRolls, int[] defenderRolls, Stack<NT_Unit> damagedAttackers, Stack<NT_Unit> damagedDefenders, int state) {
        battleHandler.updateBattle(attackRolls, defenderRolls, damagedAttackers, damagedDefenders, state);
    }

    public void updateWindows() {
        windows.updateWindows();
    }

    public void hideWindows() {
        windows.hideWindows();
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

    public RoundInformation getRoundInformation() {
        return roundInformation;
    }
}
