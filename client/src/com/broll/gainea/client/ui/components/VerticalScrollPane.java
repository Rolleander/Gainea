package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class VerticalScrollPane extends ScrollPane {


    public VerticalScrollPane(Actor actor, Skin skin) {
        super(actor, skin);
        setScrollBarPositions(false, true);
        setOverscroll(false, false);
        setScrollingDisabled(true, false);
        setFadeScrollBars(false);
    }
}
