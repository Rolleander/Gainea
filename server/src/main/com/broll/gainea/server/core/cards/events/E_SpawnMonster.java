package com.broll.gainea.server.core.cards.events;

import com.broll.gainea.server.core.cards.EventCard;
import com.broll.gainea.server.core.utils.UnitControl;


public class E_SpawnMonster extends EventCard {
    public E_SpawnMonster() {
        super(60, "Rückkehr der Natur", "Ein wildes Monster taucht auf!");
    }

    @Override
    protected void play() {
        UnitControl.spawnMonsters(game, 1);
    }

}
