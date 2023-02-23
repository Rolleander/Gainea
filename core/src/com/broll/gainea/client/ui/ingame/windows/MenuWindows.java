package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;

import java.util.HashMap;
import java.util.Map;


public class MenuWindows {

    private Gainea game;
    private Map<Class<? extends MenuWindow>, MenuWindow> windows = new HashMap<>();

    public MenuWindows(Gainea game, Skin skin) {
        this.game = game;
        addWindow(new FractionWindow(game, skin));
        addWindow(new PlayerWindow(game, skin));
        addWindow(new CardWindow(game, skin));
        addWindow(new ChatWindow(game, skin));
        addWindow(new LogWindow(game, skin));
    }

    public void showFractionWindow() {
        toggle(FractionWindow.class);
    }

    public void showPlayerWindow() {
        toggle(PlayerWindow.class);
    }

    public void showCardWindow() {
        toggle(CardWindow.class);
    }

    public void showChatWindow() {
        toggle(ChatWindow.class);
    }

    public void showLogWindow() {
        toggle(LogWindow.class);
    }

    public void hideWindows() {
        windows.values().forEach(it -> it.setVisible(false));
    }

    private void toggle(Class<? extends MenuWindow> window) {
        windows.get(window).toggleVisiblity();
    }

    public void updateWindows() {
        windows.values().forEach(MenuWindow::update);
    }

    private void addWindow(MenuWindow window) {
        Table pop = new Table();
        pop.setFillParent(true);
        pop.add(window).expand().fill().center();
        window.setVisible(false);
        game.uiStage.addActor(window);
        windows.put(window.getClass(), window);
    }

    public CardWindow getCardWindow() {
        return (CardWindow) windows.get(CardWindow.class);
    }

    public LogWindow getLogWindow() {
        return (LogWindow) windows.get(LogWindow.class);
    }

}
