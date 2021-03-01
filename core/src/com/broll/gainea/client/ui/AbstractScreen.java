package com.broll.gainea.client.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.LabelUtils;
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
        return LabelUtils.info(skin, text);
    }

    protected Label info(String text, Color color) {
        return LabelUtils.color(info(text), color);
    }

    protected Label title(String text) {
        return LabelUtils.title(skin, text);
    }

    protected Label title(String text, Color color) {
        return LabelUtils.color(title(text), color);
    }

    protected Label label(String text) {
        return LabelUtils.title(skin, text);
    }

    protected Label label(String text, Color color) {
        return LabelUtils.color(label(text), color);
    }

    public abstract Actor build();
}
