package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.GlobalBuff;
import com.broll.gainea.server.core.objects.buffs.IntBuff;

public class C_Speed extends Card {


    public C_Speed() {
        super(21, "Friedliche Eroberung", "Eure Einheiten können diesen Zug ein zusätzliches Feld bewegt werden. Während dieses Zuges könnt ihr nicht angreifen.");
        setDrawChance(0.7f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        IntBuff buffSpeed = new IntBuff(BuffType.ADD, 1);
        IntBuff buffNoAttaks = new IntBuff(BuffType.SET, 0);
        GlobalBuff.createForPlayer(game, owner, buffSpeed, unit -> unit.getMovesPerTurn().addBuff(buffSpeed), NT_Abstract_Event.EFFECT_BUFF);
        GlobalBuff.createForPlayer(game, owner, buffNoAttaks, unit -> unit.getAttacksPerTurn().addBuff(buffNoAttaks), NT_Abstract_Event.EFFECT_DEBUFF);
        game.getBuffProcessor().timeoutBuff(buffSpeed, 1);
        game.getBuffProcessor().timeoutBuff(buffNoAttaks, 1);
    }

}
