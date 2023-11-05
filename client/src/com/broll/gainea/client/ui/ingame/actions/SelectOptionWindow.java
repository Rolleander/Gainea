package com.broll.gainea.client.ui.ingame.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.network.sites.GameActionSite;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.net.NT_Action_SelectChoice;

import java.util.List;

public class SelectOptionWindow {

    public static Table create(Gainea game, RequiredActionContainer container, NT_Action_SelectChoice action, List<Actor> options) {
        Skin skin = game.ui.skin;
        Table window = new Table(skin);
        window.pad(15, 20, 15, 20);
        window.defaults().space(15).left();
        if (GameActionSite.CURRENT_PLAYER_ACTION.text != null) {
            window.add(
                    PlaceUnitWindow.actionLabel(skin)
            ).center().spaceBottom(20).row();
        }
        if (!options.isEmpty()) {
            Table content = new Table();
            options.forEach(option -> {
                Button button = new Button(skin);
                button.add(option);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        container.reactionResult(action, options.indexOf(option));
                    }
                });
                content.add(button).row();
            });
            ScrollPane scrollPane = new ScrollPane(content, skin);
            scrollPane.setScrollBarPositions(false, true);
            scrollPane.setOverscroll(false, false);
            scrollPane.setScrollingDisabled(true, false);
            scrollPane.setFadeScrollBars(false);
            window.add(scrollPane).maxHeight(530);
            TableUtils.consumeClicks(window);
        }
        return window;
    }
}
