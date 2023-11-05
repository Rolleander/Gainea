package com.broll.gainea.client.ui.utils;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.Popup;

public final class MessageUtils {


    private MessageUtils() {
    }

    public static Popup showConfirmMessage(Gainea game, String message) {
        Table content = new Table();
        content.add(LabelUtils.label(game.ui.skin, message)).row();
        Button close = TableUtils.textButton(game.ui.skin, "Ok", () ->
                content.getParent().addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.removeActor())));
        content.add(close).row();
        return Popup.show(game, content);
    }

}
