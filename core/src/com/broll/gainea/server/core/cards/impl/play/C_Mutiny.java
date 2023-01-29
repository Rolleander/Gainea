package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.GlobalBuff;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.objects.buffs.TimedEffect;

public class C_Mutiny extends Card {

    private final static int ROUNDS = 3;

    public C_Mutiny() {
        super(15, "Meuterei", "Alle Schiffe sind für" + ROUNDS + " Runden nicht mehr begehbar");
        setDrawChance(0.6f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        game.getMap().getAllShips().forEach(it -> it.setTraversable(false));
        TimedEffect.forPlayerRounds(game, owner, ROUNDS, new TimedEffect() {
            @Override
            protected void unregister() {
                super.unregister();
                game.getMap().getAllShips().forEach(it -> it.setTraversable(true));
            }
        });
    }

}
