package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_NewAnimals extends DirectlyPlayedCard {
    public C_NewAnimals() {
        super(58, "Fette Jahre", "Neue Monster bevölkern die Welt erneut");
    }

    @Override
    protected void play() {
        int totalAtStart = GameUtils.getTotalStartMonsters(game);
        int currentMonsters = GameUtils.getCurrentMonsters(game);
        int missing = totalAtStart - currentMonsters;
        UnitControl.spawnMonsters(game, RandomUtils.random(1, Math.max(missing, 3)));
    }


}
