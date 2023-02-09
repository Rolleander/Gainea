package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Event;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_Buff extends Card {
    public C_Buff() {
        super(30, "Aufstieg", "Verleiht einer eurer Einheiten +1/+1");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        BattleObject unit = SelectionUtils.selectPlayerUnit(game, owner, "Welche Einheit soll gestärkt werden?");
        if(unit!=null){
            unit.getPower().addValue(1);
            unit.getHealth().addValue(1);
            unit.getMaxHealth().addValue(1);
            UnitControl.focus(game, unit, NT_Event.EFFECT_BUFF);
        }
    }

}
