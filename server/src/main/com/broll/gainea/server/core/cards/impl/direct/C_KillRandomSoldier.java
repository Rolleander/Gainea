package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.StreamUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.concurrent.atomic.AtomicInteger;

public class C_KillRandomSoldier extends DirectlyPlayedCard {

    private final static int COUNT = 3;

    public C_KillRandomSoldier() {
        super(42, "Gemetzel", "Verursacht 1 Schaden and jeder " + COUNT + "ten Einheit jedes Spielers.");
        setDrawChance(0.4f);
    }

    @Override
    protected void play() {
        PlayerUtils.iteratePlayers(game, 500, player -> {
            AtomicInteger integer = new AtomicInteger(0);
            StreamUtils.safeForEach(player.getUnits().stream().filter(it -> integer.incrementAndGet() % COUNT == 0),
                    unit -> UnitControl.damage(game, unit, 1));
        });
    }

}
