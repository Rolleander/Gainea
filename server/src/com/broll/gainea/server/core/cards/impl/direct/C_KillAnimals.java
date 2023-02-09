package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.GodDragon;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.utils.StreamUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_KillAnimals extends DirectlyPlayedCard {
    private final static int COUNT = 3;

    public C_KillAnimals() {
        super(5, "Trockenzeit", COUNT + " schwache Monster sterben aus");
    }

    @Override
    protected void play() {
        StreamUtils.safeForEach(
                game.getObjects().stream().filter(it -> it instanceof Monster && it instanceof GodDragon == false).map(it -> (Monster) it).
                        sorted((m1, m2) -> Integer.compare(m1.getStars(), m2.getStars())).limit(COUNT),
                monster -> UnitControl.kill(game, monster));
    }

}
