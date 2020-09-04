package com.broll.gainea.server.core.utils;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IteratePlayersProcess implements IPlayerIterator {

    private final static int DEFAULT_DELAY = 1500;
    private GameContainer game;
    private int playerDelta;
    private Runnable whenFinished;
    private BiConsumer<Player, IPlayerIterator> consumer;

    public static void run(GameContainer game, BiConsumer<Player, IPlayerIterator> consumer) {
        run(game, consumer, null);
    }

    public static void run(GameContainer game, BiConsumer<Player, IPlayerIterator> consumer, Runnable whenFinished) {
        IteratePlayersProcess process = new IteratePlayersProcess();
        process.game = game;
        process.whenFinished = whenFinished;
        process.consumer = consumer;
        process.consumePlayer();
    }

    private void consumePlayer() {
        //consume players in turn direction
        int nr = (game.getCurrentPlayer() + playerDelta) % game.getPlayers().size();
        Player player = game.getPlayers().get(nr);
        consumer.accept(player, this);
    }

    @Override
    public void nextPlayer() {
        nextPlayer(DEFAULT_DELAY);
    }

    @Override
    public void continueNextPlayer() {
        nextPlayer(0);
    }

    @Override
    public void nextPlayer(int delay) {
        playerDelta++;
        if (playerDelta < game.getPlayers().size()) {
            if (delay > 0) {
                game.schedule(delay, this::consumePlayer);
            } else {
                consumePlayer();
            }
        } else {
            if (whenFinished != null) {
                whenFinished.run();
            }
            whenFinished = null;
        }
    }

}
