package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_KillSoldier extends Card {
    public C_KillSoldier() {
        super(34, "Scharfschütze", "Tötet eine beliebige feindliche Einheit (Außer Feldherr)");
        setDrawChance(0.6f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Unit unit = SelectionUtils.selectOtherPlayersUnit(game, owner, "Wählt eine Einheit aus die vernichtet werden soll", it -> it instanceof Commander == false);
        if (unit != null) {
            UnitControl.kill(game, unit);
        }
    }

}
