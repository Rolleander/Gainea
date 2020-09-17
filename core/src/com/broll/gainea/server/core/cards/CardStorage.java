package com.broll.gainea.server.core.cards;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.misc.PackageLoader;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.player.Player;

import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class CardStorage {

    private final static String PACKAGE_PATH = "com.broll.gainea.server.core.cards.impl";
    private PackageLoader<AbstractCard> loader;
    private GameContainer game;
    private ActionHandlers actionHandlers;
    private double[] drawChances;
    private final float totalDrawChances;

    public CardStorage(GameContainer gameContainer, ActionHandlers actionHandlers) {
        this.game = gameContainer;
        this.actionHandlers = actionHandlers;
        this.loader = new PackageLoader<>(AbstractCard.class, PACKAGE_PATH);
        List<AbstractCard> allCards = loader.instantiateAll();
        drawChances = allCards.stream().mapToDouble(AbstractCard::getDrawChance).toArray();
        totalDrawChances = (float) allCards.stream().mapToDouble(AbstractCard::getDrawChance).reduce(0, Double::sum);
    }

    public void drawRandomCard(Player player) {
        float chanceSum = 0;
        float chance = MathUtils.random(0, totalDrawChances);
        for (int i = 0; i < drawChances.length; i++) {
            if (chance <= chanceSum) {
                AbstractCard card = loader.instantiate(i);
                card.init(game, player, i);
                player.getCardHandler().receiveCard(card);
                return;
            }
            chanceSum += drawChances[i];
        }
    }

}
