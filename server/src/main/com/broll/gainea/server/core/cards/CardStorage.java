package com.broll.gainea.server.core.cards;

import com.broll.gainea.misc.PackageLoader;
import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardStorage {

    private final static String PACKAGE_PATH = "com.broll.gainea.server.core.cards.impl";
    private PackageLoader<Card> loader;
    private GameContainer game;
    private ActionHandlers actionHandlers;
    private double[] drawChances;
    private final float totalDrawChances;

    public CardStorage(GameContainer gameContainer, ActionHandlers actionHandlers) {
        this.game = gameContainer;
        this.actionHandlers = actionHandlers;
        this.loader = new PackageLoader<>(Card.class, PACKAGE_PATH);
        List<Card> allCards = loader.instantiateAll();
        drawChances = allCards.stream().mapToDouble(Card::getDrawChance).toArray();
        totalDrawChances = (float) allCards.stream().mapToDouble(Card::getDrawChance).reduce(0, Double::sum);
    }

    public void drawRandomCard(Player player) {
        Card card = getRandomCard();
        player.getCardHandler().receiveCard(card);
    }

    public Card getRandomCard() {
        float chanceSum = 0;
        float chance = RandomUtils.random(0, totalDrawChances);
        for (int i = 0; i < drawChances.length; i++) {
            if (chance <= chanceSum) {
                Card card = loader.instantiate(i);
                return card;
            }
            chanceSum += drawChances[i];
        }
        return getRandomCard();
    }

    public Card getRandomPlayableCard() {
        while (true) {
            Card card = getRandomCard();
            if (!(card instanceof DirectlyPlayedCard)) {
                return card;
            }
        }
    }

    public List<Card> getPlayableCards(int count) {
        ArrayList<Class<? extends Card>> classes = new ArrayList<>(loader.getClasses());
        Collections.shuffle(classes);
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < classes.size(); i++) {
            Card card = loader.instantiate(classes.get(i));
            if (!(card instanceof DirectlyPlayedCard)) {
                cards.add(card);
                if (cards.size() == count) {
                    break;
                }
            }
        }
        return cards;
    }

    public List<Card> getAllCards() {
        return loader.instantiateAll();
    }

    public Card getCard(Class<? extends Card> cardClass) {
        return loader.instantiate(cardClass);
    }
}
