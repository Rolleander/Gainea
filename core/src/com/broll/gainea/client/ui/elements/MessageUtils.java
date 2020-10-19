package com.broll.gainea.client.ui.elements;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.broll.gainea.Gainea;

public final class MessageUtils {

    private final static int MESSAGE_DURATION = 2;

    private MessageUtils() {
    }

    public static Cell<Actor> showCenterMessage(Gainea game, String message) {
        return game.ui.inGameUI.showCenterOverlay(TableUtils.removeAfter(LabelUtils.title(game.ui.skin, message), MESSAGE_DURATION)).expandY().center();
    }

    public static Cell<Actor> showActionMessage(Gainea game, String message) {
        return game.ui.inGameUI.showCenterOverlay(TableUtils.removeAfter(LabelUtils.title(game.ui.skin, message), MESSAGE_DURATION)).expandY().top().padTop(100);
    }
}
