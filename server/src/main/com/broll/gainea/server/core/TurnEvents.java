package com.broll.gainea.server.core;

import com.broll.gainea.net.NT_PlayerWait;
import com.broll.gainea.server.core.cards.EventCard;
import com.broll.gainea.server.core.cards.events.E_GetCards;
import com.broll.gainea.server.core.cards.events.E_SpawnGoddrake;
import com.broll.gainea.server.core.cards.events.E_SpawnMonster;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;
import com.broll.gainea.server.core.utils.GameUtils;

public class TurnEvents extends GameUpdateReceiverAdapter {

    private final static int SPAWN_MONSTER_TURNS = 2;
    private final static int GET_CARDS_TURNS = 5;
    private final static int SPAWN_GODDRAKE_TURNS = SPAWN_MONSTER_TURNS * 5;
    private final static int SPAWN_TURNS_START = SPAWN_GODDRAKE_TURNS; //first spawn is goddrake

    private GameContainer game;

    public TurnEvents(GameContainer game) {
        this.game = game;
    }


    private void turnEvent(Class<? extends EventCard> event) {
        NT_PlayerWait nt = new NT_PlayerWait();
        nt.playersTurn = -1;
        game.getReactionHandler().getActionHandlers().getReactionActions().sendGameUpdate(nt);
        EventCard.run(event, game);
    }

    @Override
    public void roundStarted() {
        int turn = game.getRounds();
        if (turn % GET_CARDS_TURNS == 0) {
            turnEvent(E_GetCards.class);
        }
        if (turn >= SPAWN_TURNS_START) {
            if (turn % SPAWN_GODDRAKE_TURNS == 0) {
                if (!E_SpawnGoddrake.isGoddrakeAlive(game)) {
                    turnEvent(E_SpawnGoddrake.class);
                    return;
                }
            }
            if (turn % SPAWN_MONSTER_TURNS == 0) {
                int totalAtStart = GameUtils.getTotalStartMonsters(game);
                int currentMonsters = GameUtils.getCurrentMonsters(game);
                int missing = totalAtStart - currentMonsters;
                if (missing > 0) {
                    turnEvent(E_SpawnMonster.class);
                }
            }
        }
    }

}
