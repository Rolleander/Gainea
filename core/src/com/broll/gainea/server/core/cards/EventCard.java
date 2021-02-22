package com.broll.gainea.server.core.cards;

import com.broll.gainea.net.NT_Event_PlayedCard;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.utils.MessageUtils;
import com.broll.gainea.server.core.utils.ProcessingUtils;
import com.esotericsoftware.minlog.Log;

public abstract class EventCard extends DirectlyPlayedCard {
    public EventCard(int picture, String title, String text) {
        super(picture, title, text);
    }

    public void run(GameContainer game) {
        init(game, null, game.newObjectId());
        play(game.getReactionHandler().getActionHandlers());
    }

    public static void run(Class<? extends EventCard> eventClass, GameContainer game) {
        try {
            EventCard event = eventClass.newInstance();
            NT_Event_PlayedCard playedCard = new NT_Event_PlayedCard();
            playedCard.player = -1;
            playedCard.card = event.nt();
            game.getReactionHandler().getActionHandlers().getReactionActions().sendGameUpdate(playedCard);
            ProcessingUtils.pause(3000);
            event.run(game);
        } catch (InstantiationException | IllegalAccessException e) {
            Log.error("Failed to instantiate event card " + eventClass, e);
        }
    }
}
