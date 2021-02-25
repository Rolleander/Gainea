package com.broll.gainea.client.ui.ingame.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.net.NT_Action_SelectChoice;

import java.util.List;

public class SelectOptionWindow {

    public static Table create(Gainea game, RequiredActionContainer container, NT_Action_SelectChoice action, List<Actor> options) {
        Skin skin = game.ui.skin;
        Table window = new Table(skin);
        window.pad(30, 20, 10, 20);
        window.defaults().space(15).left();
        window.setBackground("menu-bg");
        options.forEach(option -> {
            Button button = new Button(skin);
            button.add(option);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    container.reactionResult(action, options.indexOf(option));
                }
            });
            window.add(button).row();
        });
        TableUtils.consumeClicks(window);
        return window;
    }
}
