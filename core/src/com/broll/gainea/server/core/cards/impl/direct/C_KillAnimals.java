package com.broll.gainea.server.core.cards.impl.direct;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.cards.events.E_SpawnMonster;
import com.broll.gainea.server.core.objects.GodDragon;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.utils.UnitControl;
import com.esotericsoftware.minlog.Log;
import com.google.common.collect.Lists;

public class C_KillAnimals extends DirectlyPlayedCard {
    private final static int COUNT = 3;

    public C_KillAnimals() {
        super(5, "Trockenzeit", COUNT + " schwache Monster sterben aus");
    }

    @Override
    protected void play() {
        game.getObjects().stream().filter(it -> it instanceof Monster && it instanceof GodDragon == false).map(it -> (Monster) it).
                sorted((m1, m2) -> Integer.compare(m1.getStars(), m2.getStars())).limit(COUNT).forEach(monster -> UnitControl.kill(game, monster));
    }

}
