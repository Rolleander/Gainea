package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_MonsterRevenge extends DirectlyPlayedCard {

    private final static int TURNS = 3;
    private final static int BUFF = 3;

    public C_MonsterRevenge() {
        super(33, "Gesetz des Stärkeren", "Für " + TURNS + " Runden erhalten alle wilden Monster +" + BUFF + "/+" + BUFF);
        setDrawChance(0.6f);
    }

    @Override
    protected void play() {
        IntBuff buff = new IntBuff(BuffType.ADD, BUFF);
        game.getObjects().stream().filter(it -> it instanceof Monster).map(it -> (Monster) it).forEach(monster->{
            monster.addHealthBuff(buff);
            monster.getPower().addBuff(buff);
            UnitControl.focus(game, monster, NT_Abstract_Event.EFFECT_BUFF);
        });
        game.getBuffDurationProcessor().timeoutBuff(buff, TURNS);
    }

}
