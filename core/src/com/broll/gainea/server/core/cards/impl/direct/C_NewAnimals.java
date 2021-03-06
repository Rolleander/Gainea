package com.broll.gainea.server.core.cards.impl.direct;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.cards.events.E_SpawnMonster;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_NewAnimals extends DirectlyPlayedCard {
    public C_NewAnimals() {
        super(58, "Fette Jahre", "Neue Monster bevölkern die Welt erneut");
    }

    @Override
    protected void play() {
        int totalAtStart = E_SpawnMonster.getTotalStartMonsters(game);
        int currentMonsters = E_SpawnMonster.getCurrentMonsters(game);
        int missing = totalAtStart - currentMonsters;
        UnitControl.spawnMonsters(game, MathUtils.random(1, Math.max(missing, 1)));
    }



}
