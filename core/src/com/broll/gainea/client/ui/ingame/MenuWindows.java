package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;


public class MenuWindows {

    private Gainea game;
    private FractionWindow fractionWindow;
    private PlayerWindow playerWindow;
    private GoalWindow goalWindow;
    private CardWindow cardWindow;

    public MenuWindows(Gainea game, Skin skin) {
        this.game = game;
        fractionWindow = new FractionWindow(game, skin);
        addWindow(fractionWindow);
        playerWindow = new PlayerWindow(game, skin);
        addWindow(playerWindow);
        goalWindow = new GoalWindow(game,skin);
        addWindow(goalWindow);
        cardWindow = new CardWindow(game,skin);
        addWindow(cardWindow);
    }

    public void showFractionWindow() {
        fractionWindow.setVisible(true);
        fractionWindow.toFront();
    }

    public void showPlayerWindow() {
        playerWindow.setVisible(true);
        playerWindow.toFront();
    }

    public void showGoalWindow(){
        goalWindow.setVisible(true);
        goalWindow.toFront();
    }

    public void showCardWindow(){
        cardWindow.setVisible(true);
        cardWindow.toFront();
    }

    public void updateWindows() {
        playerWindow.update();
        goalWindow.update();
        cardWindow.update();
    }

    private void addWindow(Table table) {
        Table pop = new Table();
        pop.setFillParent(true);
        pop.add(table).expand().fill().center();
        table.setVisible(false);
        game.uiStage.addActor(table);
    }
}
