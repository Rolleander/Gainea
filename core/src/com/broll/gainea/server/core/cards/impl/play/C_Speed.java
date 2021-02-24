package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.GlobalBuff;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.player.Player;

public class C_Speed extends AbstractCard {


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
        IntBuff buttNoAttaks = new IntBuff(BuffType.SET, 0);
        GlobalBuff.createForPlayer(game, owner, buffSpeed, unit -> unit.getMovesPerTurn().addBuff(buffSpeed), NT_Abstract_Event.EFFECT_BUFF);
        GlobalBuff.createForPlayer(game, owner, buttNoAttaks, unit -> unit.getAttacksPerTurn().addBuff(buttNoAttaks), NT_Abstract_Event.EFFECT_DEBUFF);
        game.getBuffProcessor().timeoutBuff(buffSpeed, 1);
        game.getBuffProcessor().timeoutBuff(buttNoAttaks, 1);
    }

}
