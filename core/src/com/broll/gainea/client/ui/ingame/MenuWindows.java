package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;


public class MenuWindows {

    private Gainea game;
    private FractionWindow fractionWindow;
    private PlayerWindow playerWindow;

    public MenuWindows(Gainea game, Skin skin) {
        this.game = game;
        fractionWindow = new FractionWindow(game, skin);
        addWindow(fractionWindow);
        playerWindow = new PlayerWindow(game, skin);
        addWindow(playerWindow);
    }

    public void showFractionWindow() {
        fractionWindow.setVisible(true);
    }

    public void showPlayerWindow() {
        playerWindow.setVisible(true);
    }

    public void updateWindows() {
        playerWindow.update();
    }

    private void addWindow(Table table) {
        Table pop = new Table();
        pop.setFillParent(true);
        pop.add(table).expand().fill().center();
        table.setVisible(false);
        game.uiStage.addActor(table);
    }
}
