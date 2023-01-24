package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.GlobalBuff;
import com.broll.gainea.server.core.objects.buffs.IntBuff;

public class C_Mutiny extends Card {

    private final static int TURNS = 3;

    public C_Mutiny() {
        super(15, "Meuterei", "Alle Einheiten auf Schiffen können sich für " + TURNS + " Runden weder angreifen noch sich bewegen");
        setDrawChance(0.6f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        IntBuff buff = new IntBuff(BuffType.SET, 0);
        GlobalBuff.createForAllPlayers(game, buff, unit -> {
            if (unit.getLocation() instanceof Ship) {
                unit.getMovesPerTurn().addBuff(buff);
                unit.getAttacksPerTurn().addBuff(buff);
            }
        }, NT_Abstract_Event.EFFECT_DEBUFF);
        game.getBuffProcessor().timeoutBuff(buff, 3);
    }

}
