package com.broll.gainea.server.core.cards.events;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.cards.EventCard;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.utils.UnitControl;

import java.awt.Event;

public class E_SpawnMonster extends EventCard {
    public E_SpawnMonster() {
        super(60, "Rückkehr der Natur", "Ein wildes Monster taucht auf!");
    }

    @Override
    protected void play() {
        UnitControl.spawnMonsters(game, 1);
    }

    public static int getTotalStartMonsters(GameContainer game) {
        int expansions = game.getMap().getExpansions().size();
        int monstersPerExpansion = game.getGameSettings().getMonsterCount();
        return expansions * monstersPerExpansion;
    }

    public static int getCurrentMonsters(GameContainer game) {
        return (int) game.getObjects().stream().filter(it -> it instanceof Monster).count();
    }
}
