package com.broll.gainea.server.core.cards;

import com.broll.gainea.misc.PackageLoader;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.player.Player;

public class CardStorage {

    private final static String PACKAGE_PATH = "com.broll.gainea.server.core.cards.impl";
    private PackageLoader<AbstractCard> loader;
    private GameContainer game;
    private ActionHandlers actionHandlers;

    public CardStorage(GameContainer gameContainer, ActionHandlers actionHandlers) {
        this.game = gameContainer;
        this.actionHandlers = actionHandlers;
        this.loader = new PackageLoader<>(AbstractCard.class, PACKAGE_PATH);
    }

    public void drawRandomCard(Player player) {
        do {
            int id = loader.getRandomIndex();
            AbstractCard card = loader.instantiate(id);
            if (card.init(game, player, id)) {
                player.getCardHandler().receiveCard(card);
                return;
            }
        } while (true);
    }

}
