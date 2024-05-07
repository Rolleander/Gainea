package com.broll.gainea.client.ui.ingame.hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.ingame.windows.CardWindow;
import com.broll.gainea.client.ui.ingame.windows.ChatWindow;
import com.broll.gainea.client.ui.ingame.windows.LogWindow;
import com.broll.gainea.client.ui.ingame.windows.MenuWindow;
import com.broll.gainea.client.ui.ingame.windows.SettingsWindow;
import com.broll.gainea.client.ui.ingame.windows.ShopWindow;
import com.broll.gainea.client.ui.ingame.windows.lib.LibraryWindow;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Action_Shop;

import java.util.List;
import java.util.stream.Collectors;

public class MenuActions extends Table {

    private Gainea game;

    private EndTurnButton endTurnButton;

    private ChatWindow chatWindow;

    private CardWindow cardWindow;

    private ShopWindow shopWindow;

    private LibraryWindow libraryWindow;

    private LogWindow logWindow;

    private SettingsWindow settingsWindow;

    private RoundImageButton cardButton, chatButton;

    private RoundInformation roundInformation;


    public MenuActions(Gainea game) {
        this.game = game;
        this.endTurnButton = new EndTurnButton(game);
        this.chatWindow = new ChatWindow(game, this::newChatMessages);
        this.cardWindow = new CardWindow(game);
        this.shopWindow = new ShopWindow(game);
        this.libraryWindow = new LibraryWindow(game);
        this.logWindow = new LogWindow(game);
        this.settingsWindow = new SettingsWindow(game);
        this.roundInformation = new RoundInformation(game);
        this.cardButton = windowButton(1, cardWindow);
        this.chatButton = windowButton(2, chatWindow);
        setFillParent(true);
        top();
        left();
        Table content = new Table().padLeft(10);
        content.defaults().padTop(5).left();
        content.add(endTurnButton).spaceBottom(30).row();
        content.add(cardButton).row();
        content.add(windowButton(5, shopWindow)).row();
        content.add(windowButton(4, libraryWindow)).row();
        content.add(windowButton(3, logWindow)).row();
        content.add(chatButton).row();
        content.add(windowButton(0, settingsWindow)).row();
        add(content).padTop(Math.max(200, game.state.getGoals().size() * 30));
        game.state.addListener(endTurnButton);
    }

    public void show() {
        game.uiStage.addActor(roundInformation);
        game.uiStage.addActor(this);
        game.uiStage.addActor(chatWindow);
        game.uiStage.addActor(cardWindow);
        game.uiStage.addActor(libraryWindow);
        game.uiStage.addActor(settingsWindow);
        game.uiStage.addActor(logWindow);
        game.uiStage.addActor(shopWindow);
    }


    private RoundImageButton windowButton(int nr, MenuWindow window) {
        RoundImageButton button = new RoundImageButton(game.ui.skin, getMenuIcon(nr));
        button.whenClicked(window::toggleVisiblity);
        return button;
    }

    private TextureRegion getMenuIcon(int nr) {
        return new TextureRegion(game.assets.get("textures/menu.png", Texture.class), 60 * nr, 0, 60, 60);
    }

    public void updateOptionalActions(List<NT_Action> actions, PlayerPerformOptionalAction playerPerformAction) {
        this.endTurnButton.update(playerPerformAction);
        cardWindow.updatePlayableCards(actions.stream().filter(it -> it instanceof NT_Action_Card).map(it -> (NT_Action_Card) it).collect(Collectors.toList()), playerPerformAction);
        shopWindow.updateState(actions.stream().filter(it -> it instanceof NT_Action_Shop).map(it -> (NT_Action_Shop) it).collect(Collectors.toList()), playerPerformAction);
    }

    public void update() {
        shopWindow.update();
        roundInformation.update();
        libraryWindow.update();
        cardWindow.update();
        chatWindow.update();
        int cards = game.state.getCards().size();
        cardButton.setDisabled(cards == 0);
        if (cards > 0) {
            cardButton.setText(cards + "");
        } else {
            cardButton.setText(null);
        }
    }

    private void newChatMessages(int count) {
        if (count == 0) {
            chatButton.setText(null);
        } else {
            chatButton.setText("" + count);
        }
    }

    public void updatePlayableCards(List<NT_Action_Card> cards, PlayerPerformOptionalAction playerPerformAction) {
        cardWindow.updatePlayableCards(cards, playerPerformAction);
    }

    public LogWindow getLogWindow() {
        return logWindow;
    }

    public void hideWindows() {
        cardWindow.setVisible(false);
        libraryWindow.setVisible(false);
        logWindow.setVisible(false);
        shopWindow.setVisible(false);
    }
}
