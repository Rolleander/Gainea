package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_KillOwnSoldier extends DirectlyPlayedCard {

    public C_KillOwnSoldier() {
        super(24, "Opferritual", "Jeder Spieler muss eine eigene Einheit opfern.");
        setDrawChance(0.8f);
    }

    @Override
    protected void play() {
        PlayerUtils.iteratePlayers(game, 500, player -> {
            BattleObject unit = SelectionUtils.selectPlayerUnit(game, player ,player, "Welche Einheit soll geopfert werden?", it->true );
            if (unit != null) {
                UnitControl.kill(game, unit);
            }
        });
    }

}