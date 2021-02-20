package com.broll.gainea.client.ui.elements;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.broll.gainea.Gainea;

public final class MessageUtils {


    private MessageUtils() {
    }

    public static Cell<Actor> showCenterMessage(Gainea game, String message) {
        return Popup.info(game, LabelUtils.title(game.ui.skin, message));
    }

    public static Cell<Actor> showActionMessage(Gainea game, String message) {
        return game.ui.inGameUI.showCenterOverlay(new Popup(game.ui.skin, LabelUtils.title(game.ui.skin, message))).padBottom(300);
    }
}
