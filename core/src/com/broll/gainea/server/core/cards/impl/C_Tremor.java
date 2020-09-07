package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitUtils;

public class C_Tremor extends DirectlyPlayedCard {
    public C_Tremor() {
        super("Beben", "Verursacht 1 Schaden an einer zufälligen Einheit jedes Spielers");
    }

    @Override
    public void play() {
        PlayerUtils.iteratePlayers(game, 1500, player -> {
            BattleObject unit = RandomUtils.pickRandom(player.getUnits());
            if (unit != null) {
                //deal 1 damage
                UnitUtils.damageUnit(game, unit, 1);
            }
        });
    }

}
