package com.broll.gainea.server.core.objects.buffs;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;

public class TimedEffect extends GameUpdateReceiverAdapter {

    private GameContainer game;
    private Player owner;
    private boolean forThisTurn;
    private int rounds;

    @Override
    public void turnStarted(Player player) {
        if (owner == player || forThisTurn) {
            decrease();
        }
    }

    @Override
    public void roundStarted() {
        if (owner == null) {
            decrease();
        }
    }

    private void decrease() {
        rounds--;
        if (rounds <= 0) {
            unregister();
        }
    }

    protected void unregister() {
        this.game.getUpdateReceiver().unregister(this);
    }

    public static void forCurrentTurn(GameContainer game, TimedEffect effect) {
        effect.game = game;
        effect.forThisTurn = true;
        game.getUpdateReceiver().register(effect);
    }

    public static void forCurrentRound(GameContainer game, TimedEffect effect) {
        effect.game = game;
        game.getUpdateReceiver().register(effect);
    }

    public static void forPlayersTurn(GameContainer game, Player player, TimedEffect effect) {
        effect.game = game;
        effect.owner = player;
        game.getUpdateReceiver().register(effect);
    }

    public static void forPlayerRounds(GameContainer game, Player player, int rounds, TimedEffect effect) {
        effect.game = game;
        effect.rounds = rounds;
        effect.owner = player;
        game.getUpdateReceiver().register(effect);
    }

    public static void forGameRounds(GameContainer game, int rounds, TimedEffect effect) {
        effect.game = game;
        effect.rounds = rounds;
        game.getUpdateReceiver().register(effect);
    }

}
