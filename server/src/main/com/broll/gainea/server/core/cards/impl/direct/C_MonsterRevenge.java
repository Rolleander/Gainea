package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public class C_MonsterRevenge extends DirectlyPlayedCard {

    private final static int ROUNDS = 3;
    private final static int BUFF = 3;

    public C_MonsterRevenge() {
        super(33, "Gesetz des Stärkeren", "Für " + ROUNDS + " Runden erhalten alle wilden Monster +" + BUFF + "/+" + BUFF);
        setDrawChance(0.6f);
    }

    @Override
    protected void play() {
        IntBuff buff = new IntBuff(BuffType.ADD, BUFF);
        List<Monster> monsters = game.getObjects().stream().filter(it -> it instanceof Monster).map(it -> (Monster) it).collect(Collectors.toList());
        monsters.forEach(monster -> {
            monster.addHealthBuff(buff);
            monster.getPower().addBuff(buff);
        });
        UnitControl.update(game, monsters);
        game.getBuffProcessor().timeoutBuff(buff, ROUNDS);
    }

}
