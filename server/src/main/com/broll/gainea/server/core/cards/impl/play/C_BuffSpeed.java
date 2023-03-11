package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.utils.SelectionUtils;

public class C_BuffSpeed extends Card {


    public C_BuffSpeed() {
        super(28, "Reittier", "Verleiht einer eurer Einheiten eine zusätzliche Bewegungsaktion pro Zug");
    }

    @Override
    public boolean isPlayable() {
        return !owner.getUnits().isEmpty();
    }

    @Override
    protected void play() {
        Unit unit = SelectionUtils.selectPlayerUnit(game, owner, "Wählt eine Einheit");
        IntBuff buff = new IntBuff(BuffType.ADD, 1);
        unit.getMovesPerTurn().addBuff(buff);
    }
}
