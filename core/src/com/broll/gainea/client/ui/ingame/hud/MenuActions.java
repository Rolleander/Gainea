package com.broll.gainea.client.ui.ingame.hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.ingame.windows.CardWindow;
import com.broll.gainea.client.ui.ingame.windows.ChatWindow;
import com.broll.gainea.client.ui.ingame.windows.FractionWindow;
import com.broll.gainea.client.ui.ingame.windows.LogWindow;
import com.broll.gainea.client.ui.ingame.windows.MenuWindow;
import com.broll.gainea.net.NT_Action_Card;

import java.util.List;

public class MenuActions extends Table {

    private Gainea game;

    private EndTurnButton endTurnButton;

    private ChatWindow chatWindow;

    private CardWindow cardWindow;

    private FractionWindow fractionWindow;

    private LogWindow logWindow;

    private RoundImageButton cardButton;

    private RoundInformation roundInformation;


    public MenuActions(Gainea game) {
        this.game = game;
        this.endTurnButton = new EndTurnButton(game);
        this.chatWindow = new ChatWindow(game);
        this.cardWindow = new CardWindow(game);
        this.fractionWindow = new FractionWindow(game);
        this.logWindow = new LogWindow(game);
        this.cardButton = windowButton(1, cardWindow);
        this.roundInformation = new RoundInformation(game);
        setFillParent(true);
        top();
        left();
        Table content = new Table().padLeft(10);
        content.defaults().padTop(5).left();
        content.add(endTurnButton).spaceBottom(30).row();
        content.add(cardButton).row();
        content.add(windowButton(4, fractionWindow)).row();
        content.add(windowButton(2, chatWindow)).row();
        add(content).padTop(Math.max(250, game.state.getGoals().size() * 30));
        game.state.addListener(endTurnButton);
    }

    public void show() {
        game.uiStage.addActor(roundInformation);
        game.uiStage.addActor(this);
        game.uiStage.addActor(chatWindow);
        game.uiStage.addActor(cardWindow);
        game.uiStage.addActor(fractionWindow);
    }


    private RoundImageButton windowButton(int nr, MenuWindow window) {
        RoundImageButton button = new RoundImageButton(game.ui.skin, getMenuIcon(nr));
        button.whenClicked(window::toggleVisiblity);
        return button;
    }

    private TextureRegion getMenuIcon(int nr) {
        return new TextureRegion(game.assets.get("textures/menu.png", Texture.class), 60 * nr, 0, 60, 60);
    }

    public void updateOptionalActions(PlayerPerformOptionalAction playerPerformAction) {
        this.endTurnButton.update(playerPerformAction);
    }

    public void update() {
        roundInformation.update();
        fractionWindow.update();
        cardWindow.update();
        chatWindow.update();
        int cards = game.state.getCards().size();
        if (cards > 0) {
            cardButton.setText(cards + "");
        } else {
            cardButton.setText(null);
        }
    }

    public void updatePlayableCards(List<NT_Action_Card> cards, PlayerPerformOptionalAction playerPerformAction) {
        cardWindow.updatePlayableCards(cards, playerPerformAction);
    }

    public LogWindow getLogWindow() {
        return logWindow;
    }
}
