package com.broll.gainea.client.ui.elements;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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
        Button button = new Button(skin);
        button.add(LabelUtils.label(skin, text));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                listener.action();
            }
        });
        return button;
    }

    public static Container removeAfter(Widget widget, int milliseconds) {
        Container container = new Container(widget) {
            float duration = 0;

            @Override
            public void act(float delta) {
                super.act(delta);
                duration += delta;
                if (duration >= milliseconds) {
                    remove();
                }
            }
        };
        return container;
    }

}
