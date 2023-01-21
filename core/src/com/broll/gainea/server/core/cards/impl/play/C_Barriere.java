package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.GlobalBuff;
import com.broll.gainea.server.core.objects.buffs.IntBuff;

public class C_Barriere extends Card {

    private final static int DURATION = 3;
    private final static int BUFF = 2;

    public C_Barriere() {
        super(41, "Magische Barriere", "Eure Einheiten erhalten +" + BUFF + " Leben für " + DURATION + " Runden, können solange aber nicht mehr angreifen.");
        setDrawChance(0.4f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        IntBuff healthBuff = new IntBuff(BuffType.ADD, BUFF);
        IntBuff noAttackBuff = new IntBuff(BuffType.SET, 0);
        GlobalBuff.createForPlayer(game, owner, healthBuff, unit -> unit.addHealthBuff(healthBuff), NT_Abstract_Event.EFFECT_BUFF);
        GlobalBuff.createForPlayer(game, owner, noAttackBuff, unit -> unit.getAttacksPerTurn().addBuff(noAttackBuff), NT_Abstract_Event.EFFECT_DEBUFF);
        game.getBuffProcessor().timeoutBuff(healthBuff, DURATION);
        game.getBuffProcessor().timeoutBuff(noAttackBuff, DURATION);
    }

}
