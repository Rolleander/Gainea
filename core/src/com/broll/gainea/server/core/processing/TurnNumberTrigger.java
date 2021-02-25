package com.broll.gainea.server.core.processing;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;

public class TurnNumberTrigger extends GameUpdateReceiverAdapter {

    private int turns;
    private Runnable event;
    private GameContainer game;
    private Player player;

    public TurnNumberTrigger(GameContainer game, int turns, Runnable event) {
        this.game = game;
        this.player = game.getPlayers().get(game.getCurrentPlayer());
        this.turns = turns;
        this.event = event;
    }

    @Override
    public void turnStarted(Player player) {
        if (this.player == player) {
            turns--;
            if (turns <= 0) {
                game.getUpdateReceiver().unregister(this);
                event.run();
            }
        }
    }
}
