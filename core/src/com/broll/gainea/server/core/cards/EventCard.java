package com.broll.gainea.server.core.cards;

import com.broll.gainea.server.core.GameContainer;
import com.esotericsoftware.minlog.Log;

public abstract class EventCard extends DirectlyPlayedCard {
    public EventCard(int picture, String title, String text) {
        super(picture, title, text);
    }

    public void run(GameContainer game) {
        init(game, null, game.newObjectId());
        play(game.getReactionHandler().getActionHandlers());
    }

    public static void run(Class<? extends EventCard> event, GameContainer game) {
        try {
            event.newInstance().run(game);
        } catch (InstantiationException | IllegalAccessException e) {
            Log.error("Failed to instantiate event card "+event, e);
        }
    }
}
