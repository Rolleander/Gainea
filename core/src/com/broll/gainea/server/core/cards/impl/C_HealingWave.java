package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_HealingWave extends DirectlyPlayedCard {

    public C_HealingWave() {
        super(64, "Kristallenergie", "Heilt alle neutralen Einheiten");
    }

    @Override
    protected void play() {
        game.getObjects().stream().filter(it -> it instanceof BattleObject).map(it -> (BattleObject) it).filter(BattleObject::isHurt).forEach(unit -> {
            UnitControl.heal(game, unit, 10000);
        });
    }

}
