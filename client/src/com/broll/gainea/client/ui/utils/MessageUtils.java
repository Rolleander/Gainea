package com.broll.gainea.client.ui.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.Popup;

public final class MessageUtils {


    private MessageUtils() {
    }

    public static Cell<Actor> showCenterMessage(Gainea game, String message) {
        return Popup.info(game, LabelUtils.title(game.ui.skin, message));
    }

    public static Cell<Actor> showConfirmMessage(Gainea game, String message) {
        Table content = new Table();
        content.add(LabelUtils.title(game.ui.skin, message)).row();
        Button close = new Button(game.ui.skin, "Ok");
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                content.getParent().addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.removeActor()));
            }
        });
        content.add(close).row();
        return Popup.show(game, content);
    }

    public static Cell<Actor> showActionMessage(Gainea game, String message) {
        return game.ui.inGameUI.showCenterOverlay(new Popup(game.ui.skin, LabelUtils.title(game.ui.skin, message))).padBottom(350);
    }

}