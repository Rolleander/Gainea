package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.utils.GameUtils;
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
            //TODO cardplayer selects all
            BattleObject unit = SelectionUtils.selectPlayerUnit(game, player, "Welche Einheit soll geopfert werden?");
            if (unit != null) {
                UnitControl.kill(game, unit);
            }
        });
    }

}
