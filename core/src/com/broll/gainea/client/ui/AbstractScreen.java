package com.broll.gainea.client.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.screens.StartScreen;

public abstract class AbstractScreen {

    protected Skin skin;
    protected Gainea game;

    public AbstractScreen() {

    }

    void init(Skin skin, Gainea game) {
        this.skin = skin;
        this.game = game;
    }

    protected void backToTitle() {
        game.ui.showScreen(new StartScreen());
    }


    protected Label info(String text) {
        return new Label(text, skin);
    }


    protected Label title(String text) {
        Label l = new Label(text, skin, "title");
        return l;
    }

    protected Label label(String text) {
        Label l = new Label(text, skin, "title");
        l.setFontScale(0.7f);
        return l;
    }


    public abstract Actor build();
}
