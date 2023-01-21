package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.GlobalBuff;
import com.broll.gainea.server.core.objects.buffs.IntBuff;

public class C_NoAttacks extends DirectlyPlayedCard {

    private final static int DURATION = 1;

    public C_NoAttacks() {
        super(26, "Nachtruhe", "Für " + DURATION + " Runde kann niemand angreifen");
    }

    @Override
    protected void play() {
        IntBuff buff = new IntBuff(BuffType.SET, 0);
        GlobalBuff.createForAllPlayers(game, buff, unit -> unit.getAttacksPerTurn().addBuff(buff), NT_Abstract_Event.EFFECT_DEBUFF);
        game.getBuffProcessor().timeoutBuff(buff, DURATION);
    }

}
