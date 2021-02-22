package com.broll.gainea.server.core;

import com.broll.gainea.server.core.cards.EventCard;
import com.broll.gainea.server.core.cards.events.E_SpawnGoddrake;
import com.broll.gainea.server.core.cards.events.E_SpawnMonster;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;

public class TurnEvents extends GameUpdateReceiverAdapter {

    private final static int SPAWN_MONSTER_TURNS = 2;
    private final static int SPAWN_GODDRAKE_TURNS = SPAWN_MONSTER_TURNS * 5;
    private final static int SPAWN_TURNS_START = SPAWN_GODDRAKE_TURNS; //first spawn is goddrake

    private GameContainer game;

    public TurnEvents(GameContainer game) {
        this.game = game;
    }

    @Override
    public void roundStarted() {
        int turn = game.getTurns();
        if (turn >= SPAWN_TURNS_START) {
            if (turn % SPAWN_GODDRAKE_TURNS == 0) {
                if (!E_SpawnGoddrake.isGoddrakeAlive(game)) {
                    EventCard.run(E_SpawnGoddrake.class, game);
                    return;
                }
            }
            if (turn % SPAWN_MONSTER_TURNS == 0) {
                int totalAtStart = E_SpawnMonster.getTotalStartMonsters(game);
                int currentMonsters = E_SpawnMonster.getCurrentMonsters(game);
                int missing = totalAtStart - currentMonsters;
                if (missing > 0) {
                    EventCard.run(E_SpawnMonster.class, game);
                }
            }
        }
    }

}
