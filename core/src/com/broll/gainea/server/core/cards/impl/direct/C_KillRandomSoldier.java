package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class C_KillRandomSoldier extends DirectlyPlayedCard {

    private final static int COUNT = 3;

    public C_KillRandomSoldier() {
        super(42, "Gemetzel", "Verursacht 1 Schaden and jeder " + COUNT + "ten Einheit jedes Spielers.");
        setDrawChance(0.4f);
    }

    @Override
    protected void play() {
        PlayerUtils.iteratePlayers(game, 500, player -> {
            List<BattleObject> units = player.getUnits();
            for (int i = 0; i < units.size() / COUNT; i++) {
                UnitControl.damage(game, units.get(((i + 1) * COUNT) - 1), 1);
            }
        });
    }

}
