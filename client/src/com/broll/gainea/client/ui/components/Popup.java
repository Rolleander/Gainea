package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;

public class Popup extends Table {

    public Popup(Skin skin, Actor content) {
        super(skin);
        setBackground("info-bg");
        pad(10, 20, 10, 20);
        addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.3f)));
        add(content);
    }

    public static Popup show(Gainea game, Actor content) {
        Popup popup = new Popup(game.ui.skin, content);
        game.ui.inGameUI.showTopCenter(popup).expandY().padTop(100).top();
        return popup;
    }
}
