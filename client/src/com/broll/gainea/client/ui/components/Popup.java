package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;

public class Popup extends Table {
    private final static int MESSAGE_DURATION = 2;

    public Popup(Skin skin, Actor content) {
        this(skin, content, 0);
    }

    public Popup(Skin skin, Actor content, float removeAfter) {
        super(skin);
        setBackground("menu-bg");
        pad(10, 20, 10, 20);
        addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.3f)));
        if (removeAfter > 0) {
            addAction(Actions.delay(removeAfter, Actions.sequence(Actions.fadeOut(0.3f), Actions.removeActor())));
        }
        add(content);
    }

    public static Popup info(Gainea game, Actor content) {
        Popup popup = new Popup(game.ui.skin, content, MESSAGE_DURATION);
        game.ui.inGameUI.showCenterOverlay(popup).expandY().padTop(100).top();
        return popup;
    }

    public static Popup show(Gainea game, Actor content) {
        Popup popup = new Popup(game.ui.skin, content);
        game.ui.inGameUI.showCenterOverlay(popup).expandY().padTop(100).top();
        return popup;
    }
}
