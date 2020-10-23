package com.broll.gainea.client.ui.elements;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
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
        addAction(Actions.fadeIn(0.3f));
        if (removeAfter > 0) {
            addAction(Actions.delay(removeAfter, Actions.removeActor()));
        }
        add(content);
    }

    public static Cell<Actor> info(Gainea game, Actor content) {
        return game.ui.inGameUI.showCenterOverlay(new Popup(game.ui.skin, content, MESSAGE_DURATION)).expandY().padTop(100).top();
    }

    public static Cell<Actor> show(Gainea game, Actor content) {
        return game.ui.inGameUI.showCenterOverlay(new Popup(game.ui.skin, content)).expandY().padTop(100).top();
    }
}
