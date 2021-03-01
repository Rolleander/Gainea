package com.broll.gainea.client.ui.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.client.AudioPlayer;

public class TableUtils {

    public static void consumeClicks(Table table) {
        table.setTouchable(Touchable.enabled);
        table.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
            }
        });
    }

    public static Button textButton(Skin skin, String text, ActionListener listener) {
        TextButton button = new TextButton(text, skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioPlayer.playSound("button.ogg");
                listener.action();
                event.stop();
            }
        });
        return button;
    }

    public static Actor removeAfter(Actor widget, float delay) {
        widget.addAction(Actions.delay(delay, Actions.sequence(Actions.fadeIn(0.3f), Actions.removeActor())));
        return widget;
    }

}
