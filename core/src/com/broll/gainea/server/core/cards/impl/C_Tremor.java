package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_Tremor extends DirectlyPlayedCard {
    public C_Tremor() {
        super(23,"Auge um Auge", "Verursacht 1 Schaden an einer zufälligen Einheit jedes Spielers");
    }

    @Override
    protected void play() {
        PlayerUtils.iteratePlayers(game, 500, player -> {
            BattleObject unit = RandomUtils.pickRandom(player.getUnits());
            if (unit != null) {
                //deal 1 damage
                UnitControl.damage(game, unit, 1);
            }
        });
    }

}
